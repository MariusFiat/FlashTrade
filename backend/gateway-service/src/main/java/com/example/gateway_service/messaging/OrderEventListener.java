package com.example.gateway_service.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.example.gateway_service.messaging.dto.OrderCreatedEvent;
import com.example.gateway_service.messaging.dto.TransactionUpdate;

import java.time.LocalDateTime;

@Component
public class OrderEventListener {
    private static final Logger log = LoggerFactory.getLogger(OrderEventListener.class);
    
    private final WebSocketNotificationService notificationService;
    
    public OrderEventListener(WebSocketNotificationService notificationService) {
        this.notificationService = notificationService;
    }
    
    @RabbitListener(queues = RabbitMQConfig.GATEWAY_ORDER_EVENT_QUEUE)
    public void handleOrderEvent(OrderCreatedEvent event) {
        log.info("📨 Gateway received order event: correlationId={}, status={}, orderId={}", 
                event.getCorrelationId(), event.getStatus(), event.getOrderId());
        
        if ("SUCCESS".equals(event.getStatus())) {
            log.info("✅ Order created successfully: orderId={}, symbol={}, quantity={}", 
                    event.getOrderId(), event.getSymbol(), event.getQuantity());
            
            TransactionUpdate update = new TransactionUpdate(
                event.getOrderId().toString(),
                event.getUserId(),
                event.getSymbol(),
                event.getOrderType(),
                (double) event.getQuantity(),
                event.getPrice(),
                "SUCCESS",
                LocalDateTime.now()
            );
            notificationService.broadcastTransactionUpdate(update);
        } else if ("FAILED".equals(event.getStatus())) {
            log.error("❌ Order creation failed: correlationId={}, error={}", 
                    event.getCorrelationId(), event.getErrorMessage());
        }
    }
}
