package com.example.trading_service.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.example.trading_service.config.RabbitMQConfig;
import com.example.trading_service.messaging.dto.PlaceOrderCommand;
import com.example.trading_service.service.OrderService;

@Component
public class OrderCommandListener {
    private static final Logger log = LoggerFactory.getLogger(OrderCommandListener.class);
    
    private final OrderService orderService;
    private final OrderEventPublisher eventPublisher;

    public OrderCommandListener(OrderService orderService, OrderEventPublisher eventPublisher) {
        this.orderService = orderService;
        this.eventPublisher = eventPublisher;
    }
    
    @RabbitListener(queues = RabbitMQConfig.ORDER_COMMAND_QUEUE)
    public void handleOrderCommand(PlaceOrderCommand command) {
        log.info("Received order command: {}", command);
        
        try {
            // Process the order
            var orderResponse = orderService.placeOrderFromCommand(command);
            
            // Publish success event back to gateway
            eventPublisher.publishOrderCreated(orderResponse, command.getCorrelationId(), "SUCCESS", null);
            
            log.info("Order processed successfully: orderId={}, symbol={}, quantity={}",
                    orderResponse.getId(), orderResponse.getSymbol(), orderResponse.getQuantity());
            
        } catch (Exception e) {
            log.error("Failed to process order command: {}", e.getMessage(), e);
            
            // Publish failure event
            eventPublisher.publishOrderFailed(command.getCorrelationId(), e.getMessage());
        }
    }
}
