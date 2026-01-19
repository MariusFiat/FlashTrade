package com.example.trading_service.service;

import com.example.trading_service.engine.MatchingEngine;
import com.example.trading_service.engine.OrderBook;
import com.example.trading_service.entities.market.Stock;
import com.example.trading_service.entities.order.OrderSide;
import com.example.trading_service.entities.order.OrderStatus;
import com.example.trading_service.entities.trade.Trade;
import com.example.trading_service.messaging.OrderEventPublisher;
import com.example.trading_service.messaging.TradeSettlementPublisher;
import com.example.trading_service.messaging.WalletVerificationPublisher;
import com.example.trading_service.messaging.dto.TradeSettlementEvent;
import com.example.trading_service.messaging.dto.WalletVerificationResponse;
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
import java.util.concurrent.*;

@Service
public class OrderService {
    private static final Logger log = LoggerFactory.getLogger(OrderService.class);
    private final OrderRepository orderRepository;
    private final TradeRepository tradeRepository;
    private final StockRepository stockRepository;
    private final OrderEventPublisher eventPublisher;
    private final WalletVerificationPublisher walletPublisher;
    private final WalletVerificationCoordinator walletCoordinator;
    private final TradeSettlementPublisher settlementPublisher;

    private final MatchingEngine engine = new MatchingEngine();
    private final Map<String, OrderBook> books = new ConcurrentHashMap<>();
    private final Map<String, ExecutorService> symbolExecutors = new ConcurrentHashMap<>();

    private final ScheduledExecutorService timeoutScheduler = Executors.newScheduledThreadPool(1);

    public OrderService(
            OrderRepository orderRepository,
            TradeRepository tradeRepository,
            StockRepository stockRepository,
            OrderEventPublisher eventPublisher,
            WalletVerificationPublisher walletPublisher,
            WalletVerificationCoordinator walletCoordinator,
            TradeSettlementPublisher settlementPublisher
    ) {
        this.orderRepository = orderRepository;
        this.tradeRepository = tradeRepository;
        this.stockRepository = stockRepository;
        this.eventPublisher = eventPublisher;
        this.walletPublisher = walletPublisher;
        this.walletCoordinator = walletCoordinator;
        this.settlementPublisher = settlementPublisher;
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
                    log.info(
                            "START processing order: correlationId={}, userId={}, side={}, qty={}, symbol={}",
                            command.getCorrelationId(),
                            command.getUserId(),
                            command.getSide(),
                            command.getQuantity(),
                            symbol
                    );

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

                    String correlationId = command.getCorrelationId();

                    orderRepository.save(order);

                    log.info(
                            "Order saved: orderId={}, status={}, correlationId={}",
                            order.getId(),
                            order.getStatus(),
                            correlationId
                    );

                    CompletableFuture<WalletVerificationResponse> walletFuture = walletCoordinator.register(correlationId);

                    log.info(
                            "Registering wallet verification future for correlationId={}",
                            correlationId
                    );

                    if (order.getSide() == OrderSide.BUY){
                        BigDecimal cost = currentPrice.multiply(BigDecimal.valueOf(order.getOriginalQty()));
                        walletPublisher.sendBuyOrderVerification(
                                order.getUserId().toString(),
                                order.getId(),
                                cost,
                                correlationId
                        );

                    }else {
                        walletPublisher.sendSellOrderVerification(
                                order.getUserId().toString(),
                                order.getId(),
                                symbol,
                                order.getOriginalQty(),
                                correlationId
                        );
                    }
                    log.info(
                            "Wallet verification request SENT: orderId={}, side={}, correlationId={}",
                            order.getId(),
                            order.getSide(),
                            correlationId
                    );

                    log.info(
                            "Waiting for wallet verification response: correlationId={}",
                            correlationId
                    );

                    WalletVerificationResponse walletResponse = walletFuture.get(120, TimeUnit.SECONDS);

                    log.info(
                            "Wallet response RECEIVED: orderId={}, approved={}, correlationId={}",
                            walletResponse.getOrderId(),
                            walletResponse.isApproved(),
                            walletResponse.getCorrelationId()
                    );

                    if (!walletResponse.isApproved()) {
                        order.setStatus(OrderStatus.REJECTED);
                        incomingOrdersToSave.add(order);

                        eventPublisher.publishOrderFailed(
                                correlationId,
                                walletResponse.getMessage()
                        );
                        continue;
                    }

                    log.info(
                            "Wallet approved. Proceeding to matching: orderId={}, side={}, price={}",
                            order.getId(),
                            order.getSide(),
                            currentPrice
                    );

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

                    log.info(
                            "Matching finished: orderId={}, filledQty={}, tradesCount={}",
                            order.getId(),
                            order.getFilledQty(),
                            trades.size()
                    );

                    incomingOrdersToSave.add(order);
                    allTradesToSave.addAll(trades);

                    for (Trade t : trades) {
                        if (order.getSide() == OrderSide.BUY) makerOrderIdsToUpdate.add(t.getSellOrderId());
                        else makerOrderIdsToUpdate.add(t.getBuyOrderId());
                    }

                    System.out.println("MakerOrderIdsToUpdate" + makerOrderIdsToUpdate);

                    // EVENT PUBLISHING (Immediately notify user)
                    OrderResponse response = new OrderResponse(
                            null,
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

                for (Trade trade : allTradesToSave) {
                    TradeSettlementEvent settlementEvent =
                            new TradeSettlementEvent(
                                    trade.getId(),
                                    trade.getSymbol(),
                                    trade.getQuantity(),
                                    trade.getPrice(),
                                    trade.getBuyOrderId(),
                                    trade.getBuyerId(),
                                    trade.getSellOrderId(),
                                    trade.getSellerId()
                            );

                    settlementPublisher.publish(settlementEvent);
                }


                if (!makerOrderIdsToUpdate.isEmpty()) {
                    List<Order> makersToUpdate = orderRepository.findAllById(makerOrderIdsToUpdate);
                    for (Order maker : makersToUpdate) {
                        Order orderBook = book.getOrderById(maker.getId());
                        if (orderBook != null) {
                            maker.setFilledQty(orderBook.getFilledQty());
                            maker.setStatus(orderBook.getStatus());
                        }else {
                            maker.setFilledQty(maker.getOriginalQty());
                            maker.setStatus(OrderStatus.FILLED);
                        }
                        System.out.println("Maker:" + maker);
                    }
                    orderRepository.saveAll(makersToUpdate);
                    orderRepository.flush();
                }
            }
            log.info("Batch processed for {}: {} orders, {} trades", symbol, incomingOrdersToSave.size(), allTradesToSave.size());

        } catch (Exception e){
            log.error("Critical batch failure for symbol: {}", symbol, e);
            // Fail all commands in this batch if the DB or Stock fetch explodes
            for(PlaceOrderCommand cmd : commands) {
                eventPublisher.publishOrderFailed(cmd.getCorrelationId(), "Batch Processing Failed: " + e.getMessage());
            }
        }
    }
}
