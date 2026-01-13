package com.example.trading_service.service;

import com.example.trading_service.engine.MatchingEngine;
import com.example.trading_service.engine.OrderBook;
import com.example.trading_service.entities.market.Stock;
import com.example.trading_service.entities.order.OrderSide;
import com.example.trading_service.entities.order.OrderStatus;
import com.example.trading_service.entities.trade.Trade;
import com.example.trading_service.messaging.OrderEventPublisher;
import com.example.trading_service.repository.StockRepository;
import com.example.trading_service.repository.TradeRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.trading_service.dto.OrderResponse;
import com.example.trading_service.entities.order.Order;
import com.example.trading_service.messaging.dto.PlaceOrderCommand;
import com.example.trading_service.repository.OrderRepository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Service
public class OrderService {
    private static final Logger log = LoggerFactory.getLogger(OrderService.class);
    private final OrderRepository orderRepository;
    private final TradeRepository tradeRepository;
    private final StockRepository stockRepository;
    private final OrderEventPublisher eventPublisher;

    private final MatchingEngine engine = new MatchingEngine();
    private final Map<String, OrderBook> books = new ConcurrentHashMap<>();

    private final Map<String, ExecutorService> symbolExecutors = new ConcurrentHashMap<>();

    public OrderService(OrderRepository orderRepository, TradeRepository tradeRepository, StockRepository stockRepository, OrderEventPublisher eventPublisher) {
        this.orderRepository = orderRepository;
        this.tradeRepository = tradeRepository;
        this.stockRepository = stockRepository;
        this.eventPublisher = eventPublisher;
    }

    public void placeOrderBatch(String symbol, List<PlaceOrderCommand> commands) {
        ExecutorService executor = symbolExecutors.computeIfAbsent(
                symbol,
                k -> Executors.newSingleThreadExecutor()
        );

        executor.submit(() -> processOrderLogic(symbol, commands));
    }

    @Transactional
    protected void processOrderLogic(String symbol, List<PlaceOrderCommand> commands) {
        try {
            log.info("Processing batch for symbol: {} and size: {}", symbol, commands.size());

            Stock stock = stockRepository.findById(symbol)
                    .orElseThrow(() -> new IllegalArgumentException("Stock not found" + symbol));
            BigDecimal currentPrice = BigDecimal.valueOf(stock.getPrice());

            OrderBook book = books.computeIfAbsent(symbol, s -> new OrderBook());

            List<Order> incomingOrdersToSave = new ArrayList<>();
            List<Trade> allTradesToSave = new ArrayList<>();
            Set<Long> makerOrderIdsToUpdate = new HashSet<>();

            for (PlaceOrderCommand command : commands) {
                try{
                    Order order = new Order();
                    order.setUserId(command.getUserId());
                    order.setSymbol(symbol);
                    order.setOriginalQty(command.getQuantity());
                    order.setSide(OrderSide.valueOf(command.getSide()));
                    order.setFilledQty(0);
                    order.setStatus(OrderStatus.OPEN);
                    order.setPrice(currentPrice);
                    order.setCreatedAt(Instant.now());

                    orderRepository.save(order);

                    log.info("!!!! order: {}\nbook: {}", order, book);
                    List<Trade> trades = engine.matchMarket(order, book, currentPrice);
                    log.info("!!!! trades: {}", trades);

                    if (order.getFilledQty() == 0) {
                        // No match found -> Add to book to wait for a peer
                        book.add(order);
                        order.setStatus(OrderStatus.OPEN);
                    } else if (order.getFilledQty() < order.getOriginalQty()) {
                        // Partial match -> Remainder stays in book
                        book.add(order);
                        order.setStatus(OrderStatus.PARTIALLY_FILLED);
                    } else {
                        order.setStatus(OrderStatus.FILLED);
                    }

                    incomingOrdersToSave.add(order);
                    allTradesToSave.addAll(trades);

                    for (Trade t : trades) {
                        if (order.getSide() == OrderSide.BUY) makerOrderIdsToUpdate.add(t.getSellOrderId());
                        else makerOrderIdsToUpdate.add(t.getBuyOrderId());
                    }

                    System.out.println("MakerOrderIdsToUpdate" + makerOrderIdsToUpdate);

                    // EVENT PUBLISHING (Immediately notify user)
                    OrderResponse response = new OrderResponse(
                            null, // ID is pending DB save, can pass null or generate UUID if needed
                            symbol,
                            order.getOriginalQty(),
                            currentPrice,
                            order.getCreatedAt(),
                            order.getSide()
                    );
                    eventPublisher.publishOrderCreated(response, command.getCorrelationId(), "SUCCESS", null);

                }catch (Exception e){
                    log.error("Error processing individual order in batch: {}", command, e);
                    eventPublisher.publishOrderFailed(command.getCorrelationId(), e.getMessage());
                }
            }

            if(!incomingOrdersToSave.isEmpty()){
                log.info("Trying to save order: {}", incomingOrdersToSave);

                orderRepository.saveAll(incomingOrdersToSave);
            }

            if (!allTradesToSave.isEmpty()) {
                tradeRepository.saveAll(allTradesToSave);

                // Update the "Maker" orders that were modified in the book
                // TODO: when i add more buy orders and than a sell order that matches the first buy, the buy order is not updating correctly in db
                if (!makerOrderIdsToUpdate.isEmpty()) {
                    List<Order> makersToUpdate = orderRepository.findAllById(makerOrderIdsToUpdate);
                    for (Order maker : makersToUpdate) {
                        // Simple logic: if filled, mark filled.
                        // (In prod, you'd sync exact qty from book)
                        if (maker.getFilledQty() >= maker.getOriginalQty()) {
                            maker.setStatus(OrderStatus.FILLED);
                        } else {
                            maker.setStatus(OrderStatus.PARTIALLY_FILLED);
                        }
                        System.out.println("Maker:" + maker);
                    }
                    orderRepository.saveAll(makersToUpdate);
                }
            }
            log.info("Batch processed for {}: {} orders, {} trades", symbol, incomingOrdersToSave.size(), allTradesToSave.size());

            // TODO: I will need to check the wallet and update the balance accordingly. Also I would have to send it to a matching engine

//        userClient.reserveFunds(order.getId(), order.getUserId(), estimate);

//        trades.forEach(userClient::commitTrade);

        } catch (Exception e){
            log.error("Critical batch failure for symbol: {}", symbol, e);
            // Fail all commands in this batch if the DB or Stock fetch explodes
            for(PlaceOrderCommand cmd : commands) {
                eventPublisher.publishOrderFailed(cmd.getCorrelationId(), "Batch Processing Failed: " + e.getMessage());
            }
        }
    }
}
