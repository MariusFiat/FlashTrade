package com.example.gateway_service.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.example.gateway_service.messaging.dto.OrderCreatedEvent;

@Component
public class OrderEventListener {
    private static final Logger log = LoggerFactory.getLogger(OrderEventListener.class);
    
    @RabbitListener(queues = RabbitMQConfig.GATEWAY_ORDER_EVENT_QUEUE)
    public void handleOrderEvent(OrderCreatedEvent event) {
        log.info("📨 Gateway received order event: correlationId={}, status={}, orderId={}", 
                event.getCorrelationId(), event.getStatus(), event.getOrderId());
        
        if ("SUCCESS".equals(event.getStatus())) {
            log.info("✅ Order created successfully: orderId={}, symbol={}, quantity={}", 
                    event.getOrderId(), event.getSymbol(), event.getQuantity());
            // TODO: Send to frontend via WebSocket/SignalR
            // TODO: Store correlation mapping for request-response pattern
        } else if ("FAILED".equals(event.getStatus())) {
            log.error("❌ Order creation failed: correlationId={}, error={}", 
                    event.getCorrelationId(), event.getErrorMessage());
            // TODO: Notify frontend of failure
        }
    }
}
