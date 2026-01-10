package com.example.trading_service.service;

import com.example.trading_service.engine.MatchingEngine;
import com.example.trading_service.engine.OrderBook;
import com.example.trading_service.entities.order.OrderSide;
import com.example.trading_service.entities.order.OrderStatus;
import com.example.trading_service.entities.trade.Trade;
import com.example.trading_service.repository.TradeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.trading_service.dto.OrderResponse;
import com.example.trading_service.entities.order.Order;
import com.example.trading_service.messaging.dto.PlaceOrderCommand;
import com.example.trading_service.repository.OrderRepository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OrderService {
    private static final Logger log = LoggerFactory.getLogger(OrderService.class);
    private final OrderRepository orderRepository;
    private final TradeRepository tradeRepository;

    private final MatchingEngine engine = new MatchingEngine();
    private final Map<String, OrderBook> books = new ConcurrentHashMap<>();

    public OrderService(OrderRepository orderRepository, TradeRepository tradeRepository) {
        this.orderRepository = orderRepository;
        this.tradeRepository = tradeRepository;
    }

    public OrderResponse placeOrderFromCommand(PlaceOrderCommand command) {
        log.info("Processing order from command: symbol={}, quantity={}, price={}",
                command.getSymbol(), command.getQuantity(), command.getPrice());

        Order order = new Order();
        order.setUserId(command.getUserId());
        order.setSymbol(command.getSymbol());
        order.setOriginalQty(command.getQuantity());
        order.setSide(command.getOrderSide());
        order.setFilledQty(0);
        order.setStatus(OrderStatus.OPEN);
        order.setCreatedAt(Instant.now());

        orderRepository.save(order);

        OrderBook book = books.computeIfAbsent(order.getSymbol(), s -> new OrderBook());

        // TODO: I will need to check the wallet and update the balance accordingly. Also I would have to send it to a matching engine
//        Order best;
//        if(order.getSide() == OrderSide.BUY){
//            best = book.bestAsk();
//        }else {
//            best = book.bestBid();
//        }
//
//        if (best == null){
//            order.setStatus(OrderStatus.CANCELED);
//        }
//
//        BigDecimal estimate =
//                best.getPrice().multiply(BigDecimal.valueOf(order.getOriginalQty()));
//
//        userClient.reserveFunds(order.getId(), order.getUserId(), estimate);

        List<Trade> trades = engine.matchMarket(order, book);
        tradeRepository.saveAll(trades);

//        trades.forEach(userClient::commitTrade);

        if(order.getFilledQty() == 0) {
            order.setStatus(OrderStatus.CANCELED);
        }else if(order.getFilledQty() < order.getOriginalQty()){
            order.setStatus(OrderStatus.PARTIALLY_FILLED);
        }else {
            order.setStatus(OrderStatus.FILLED);
        }

        orderRepository.save(order);

        return new OrderResponse(
                order.getId(),
                order.getSymbol(),
                order.getOriginalQty(),
                order.getPrice(),
                order.getCreatedAt(),
                order.getOrderSide()
        );
    }
}
