package com.example.trading_service.messaging;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import com.example.trading_service.config.RabbitMQConfig;
import com.example.trading_service.dto.OrderResponse;
import com.example.trading_service.messaging.dto.OrderCreatedEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventPublisher {
    
    private final RabbitTemplate rabbitTemplate;
    
    public void publishOrderCreated(OrderResponse order, String correlationId, String status, String errorMessage) {
        OrderCreatedEvent event = new OrderCreatedEvent(
                order.getId(),
                order.getSymbol(),
                order.getQuantity(),
                order.getPrice(),
                order.getOrderType(),
                null,  // userId can be added later
                order.getCreatedAt(),
                correlationId,
                status,
                errorMessage
        );
        
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.TRADING_EVENTS_EXCHANGE,
                RabbitMQConfig.ORDER_CREATED_KEY,
                event
        );
        
        log.info("📤 Published order created event: orderId={}, status={}", order.getId(), status);
    }
    
    public void publishOrderFailed(String correlationId, String errorMessage) {
        OrderCreatedEvent event = new OrderCreatedEvent();
        event.setCorrelationId(correlationId);
        event.setStatus("FAILED");
        event.setErrorMessage(errorMessage);
        
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.TRADING_EVENTS_EXCHANGE,
                RabbitMQConfig.ORDER_FAILED_KEY,
                event
        );
        
        log.info("📤 Published order failed event: correlationId={}, error={}", correlationId, errorMessage);
    }
}
