package com.example.trading_service.messaging;

import com.example.trading_service.config.RabbitMQConfig;
import com.example.trading_service.entities.order.Order;
import com.example.trading_service.messaging.dto.ActiveOrderRecord;
import com.example.trading_service.messaging.dto.ActiveOrdersRequest;
import com.example.trading_service.messaging.dto.ActiveOrdersResponse;
import com.example.trading_service.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ActiveOrdersListener {
    private static final Logger log = LoggerFactory.getLogger(ActiveOrdersListener.class);
    private final OrderRepository orderRepository;

    public ActiveOrdersListener(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @RabbitListener(
            queues = RabbitMQConfig.ACTIVE_ORDERS_REQUEST_QUEUE,
            containerFactory = "singleListenerFactory"
    )
    @SendTo
    public ActiveOrdersResponse handleActiveOrdersRequest(ActiveOrdersRequest request) {
        log.info("Received active orders request for userId={}", request.getUserId());

        List<Order> activeOrders = orderRepository.findActiveOrdersByUserId(request.getUserId());

        List<ActiveOrderRecord> records = activeOrders.stream()
                .map(order -> new ActiveOrderRecord(
                        order.getId(),
                        order.getSymbol(),
                        order.getOriginalQty(),
                        order.getFilledQty(),
                        order.getPrice(),
                        order.getSide().name(),
                        order.getStatus().name(),
                        order.getCreatedAt()
                ))
                .collect(Collectors.toList());

        log.info("Returning {} active orders for userId={}", records.size(), request.getUserId());

        return new ActiveOrdersResponse(request.getUserId(), records);
    }
}
