package com.example.trading_service.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.trading_service.dto.OrderResponse;
import com.example.trading_service.engine.MatchingEngine;
import com.example.trading_service.engine.OrderBook;
import com.example.trading_service.entities.market.Stock;
import com.example.trading_service.entities.order.Order;
import com.example.trading_service.entities.order.OrderSide;
import com.example.trading_service.entities.order.OrderStatus;
import com.example.trading_service.entities.trade.Trade;
import com.example.trading_service.messaging.OrderEventPublisher;
import com.example.trading_service.messaging.TradeSettlementPublisher;
import com.example.trading_service.messaging.WalletVerificationPublisher;
import com.example.trading_service.messaging.dto.BuyOrderCloseRequest;
import com.example.trading_service.messaging.dto.PlaceOrderCommand;
import com.example.trading_service.messaging.dto.SellOrderCloseRequest;
import com.example.trading_service.repository.OrderRepository;
import com.example.trading_service.repository.StockRepository;
import com.example.trading_service.repository.TradeRepository;

import jakarta.transaction.Transactional;

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
                    order.setStatus(OrderStatus.PENDING_WALLET);
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

                    CompletableFuture<Boolean> walletFuture = walletCoordinator.register(order.getId().toString());

                    log.info(
                            "Registering wallet verification future for orderId={}",
                            order.getId()
                    );

                    if (order.getSide() == OrderSide.BUY){
                        walletPublisher.sendBuyOrderVerification(
                                order.getId(),
                                order.getUserId(),
                                symbol,
                                order.getOriginalQty(),
                                currentPrice.doubleValue()
                        );

                    }else {
                        walletPublisher.sendSellOrderVerification(
                                order.getId(),
                                order.getUserId(),
                                symbol,
                                order.getOriginalQty()
                        );
                    }
                    log.info(
                            "Wallet verification request SENT: orderId={}, side={}, correlationId={}",
                            order.getId(),
                            order.getSide(),
                            correlationId
                    );

                    log.info(
                            "Waiting for wallet verification response: orderId={}, corelationId={}",
                            order.getId(),
                            correlationId
                    );

                    boolean approved;
                    try {
                        approved = walletFuture.get(120, TimeUnit.SECONDS);
                    } catch (CancellationException e) {
                        log.info(
                                "Wallet verification canceled for orderId={}, correlationId={}",
                                order.getId(), correlationId
                        );

                        order.setStatus(OrderStatus.CANCELED);
                        orderRepository.save(order);
                        return;
                    }

                    log.info(
                            "Wallet response RECEIVED: orderId={}, approved={}",
                            order.getId(),
                            approved
                    );

                    if (!approved) {
                        order.setStatus(OrderStatus.REJECTED);
                        incomingOrdersToSave.add(order);

                        eventPublisher.publishOrderFailed(
                                correlationId,
                                "Wallet verification failed"
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

                    OrderResponse response = new OrderResponse(
                            order.getId(),
                            symbol,
                            order.getOriginalQty(),
                            currentPrice,
                            order.getCreatedAt(),
                            order.getSide(),
                            command.getUserId()
                    );
                    eventPublisher.publishOrderCreated(response, command.getCorrelationId(), "SUCCESS", null);
                }catch (TimeoutException e){
                    log.error("Wallet verification timeout for command correlationId={}",
                            command.getCorrelationId(), e);
                    eventPublisher.publishOrderFailed(command.getCorrelationId(), "Wallet verification timeout");
                } catch (Exception e){
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

                    double amount = trade.getPrice().doubleValue() * trade.getQuantity();

                    BuyOrderCloseRequest buyClose = new BuyOrderCloseRequest(
                            String.valueOf(trade.getBuyOrderId()),
                            trade.getBuyerId(),
                            "MATCHED",
                            amount,
                            trade.getSymbol(),
                            (double) trade.getQuantity()
                    );
                    settlementPublisher.publishBuyClose(buyClose);

                    SellOrderCloseRequest sellClose = new SellOrderCloseRequest();
                    sellClose.setOrderId(String.valueOf(trade.getSellOrderId()));
                    sellClose.setUserId(trade.getSellerId());
                    sellClose.setSymbol(trade.getSymbol());
                    sellClose.setQuantity((double) trade.getQuantity());
                    sellClose.setAmountReceived(amount);
                    sellClose.setStatus("MATCHED");

                    settlementPublisher.publishSellClose(sellClose);
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
                        
                        // Notify Maker
                        OrderResponse makerResponse = new OrderResponse(
                                maker.getId(),
                                maker.getSymbol(),
                                maker.getOriginalQty(),
                                maker.getPrice(),
                                maker.getCreatedAt(),
                                maker.getSide()
                        );
                        // Use userId as correlationId placeholder or just rely on userId topic
                        eventPublisher.publishOrderUpdate(makerResponse, maker.getUserId(), "MAKER_UPDATE", "SUCCESS");
                    }
                    orderRepository.saveAll(makersToUpdate);
                    orderRepository.flush();
                }
            }
            log.info("Batch processed for {}: {} orders, {} trades", symbol, incomingOrdersToSave.size(), allTradesToSave.size());

        } catch (Exception e){
            log.error("Critical batch failure for symbol: {}", symbol, e);
            for(PlaceOrderCommand cmd : commands) {
                eventPublisher.publishOrderFailed(cmd.getCorrelationId(), "Batch Processing Failed: " + e.getMessage());
            }
        }
    }

    @Transactional
    public void cancelOrder(Long orderId, String userId, String correlationId) {

        if (orderId == null) {
            log.error("Cancel ignored: orderId is null, correlationId={}", correlationId);
            return;
        }

        log.info(
                "Cancel request received: orderId={}, userId={}, correlationId={}",
                orderId, userId, correlationId
        );

        Optional<Order> optOrder = orderRepository.findById(orderId);

        if (optOrder.isEmpty()) {
            log.warn(
                    "Cancel ignored: order not found. orderId={}, userId={}, correlationId={}",
                    orderId, userId, correlationId
            );

            eventPublisher.publishOrderCanceled(correlationId, orderId, userId);
            return;
        }

        Order order = optOrder.get();

        if (!order.getUserId().equals(userId)) {
            log.error("SECURITY ALERT: User {} tried to cancel order {} belonging to {}", 
                    userId, orderId, order.getUserId());
            return;
        }

        if (order.getStatus() == OrderStatus.FILLED ||
                order.getStatus() == OrderStatus.CANCELED ||
                order.getStatus() == OrderStatus.REJECTED) {

            log.warn("Order cannot be canceled: orderId={}, status={}",
                    orderId, order.getStatus());
            return;
        }

        // If wallet verification is still pending, cancel immediately without settlement
        if (order.getStatus() == OrderStatus.PENDING_WALLET) {
            log.info("Canceling order while wallet verification is pending: orderId={}", orderId);

            walletCoordinator.cancel(order.getId().toString());

            order.setStatus(OrderStatus.CANCELED);
            orderRepository.save(order);

            eventPublisher.publishOrderCanceled(correlationId, orderId, userId);
            return;
        }

        OrderBook book = books.get(order.getSymbol());
        if (book != null) {
            book.removeById(orderId);
        }

        order.setStatus(OrderStatus.CANCELED);
        orderRepository.save(order);

        log.info("Order canceled: orderId={}", orderId);

        if (order.getSide() == OrderSide.BUY &&
                order.getStatus() != OrderStatus.PENDING_WALLET) {

            BuyOrderCloseRequest closeRequest =
                    new BuyOrderCloseRequest(
                            order.getId().toString(),
                            order.getUserId(),
                            "CLOSED",
                            calculateRemainingReservedAmount(order),
                            order.getSymbol(),
                            (double) (order.getOriginalQty() - order.getFilledQty())
                    );

            settlementPublisher.publishBuyClose(closeRequest);
        }

        eventPublisher.publishOrderCanceled(correlationId, orderId, userId);
    }

    private Double calculateRemainingReservedAmount(Order order) {
        BigDecimal price = order.getPrice();
        int remainingQty = order.getOriginalQty() - order.getFilledQty();

        return price
                .multiply(BigDecimal.valueOf(remainingQty))
                .doubleValue();
    }
}
