package com.example.trading_service.messaging;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.example.trading_service.config.RabbitMQConfig;
import com.example.trading_service.messaging.dto.PlaceOrderCommand;
import com.example.trading_service.service.OrderService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderCommandListener {
    
    private final OrderService orderService;
    private final OrderEventPublisher eventPublisher;
    
    @RabbitListener(queues = RabbitMQConfig.ORDER_COMMAND_QUEUE)
    public void handleOrderCommand(PlaceOrderCommand command) {
        log.info("📨 Received order command: {}", command);
        
        try {
            // Process the order
            var orderResponse = orderService.placeOrderFromCommand(command);
            
            // Publish success event back to gateway
            eventPublisher.publishOrderCreated(orderResponse, command.getCorrelationId(), "SUCCESS", null);
            
            log.info("✅ Order processed successfully: orderId={}, symbol={}, quantity={}", 
                    orderResponse.getId(), orderResponse.getSymbol(), orderResponse.getQuantity());
            
        } catch (Exception e) {
            log.error("❌ Failed to process order command: {}", e.getMessage(), e);
            
            // Publish failure event
            eventPublisher.publishOrderFailed(command.getCorrelationId(), e.getMessage());
        }
    }
}
