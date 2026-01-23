package com.example.gateway_service.messaging;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import com.example.gateway_service.messaging.dto.OrderCreatedEvent;
import com.example.gateway_service.messaging.dto.TransactionUpdate;

@Component
public class OrderEventListener {
    private static final Logger log = LoggerFactory.getLogger(OrderEventListener.class);
    
    private final SimpMessagingTemplate messagingTemplate;
    private final WebSocketNotificationService notificationService;
    
    public OrderEventListener(WebSocketNotificationService notificationService, SimpMessagingTemplate messagingTemplate) {
        this.notificationService = notificationService;
        this.messagingTemplate = messagingTemplate;
    }
    
    @RabbitListener(queues = RabbitMQConfig.GATEWAY_ORDER_EVENT_QUEUE)
    public void handleOrderEvent(OrderCreatedEvent event) {
        log.info("Gateway received order event: correlationId={}, status={}, orderId={}",
                event.getCorrelationId(), event.getStatus(), event.getOrderId());
        
        // Push to frontend via WebSocket
        String userId = event.getUserId();
        if (userId != null) {
            messagingTemplate.convertAndSend("/topic/orders/" + userId, event);
            log.info("Pushed event to WebSocket: /topic/orders/{}", userId);
        } else {
            log.warn("Order event missing userId, cannot push to specific user topic. CorrelationId: {}", event.getCorrelationId());
        }

        if ("SUCCESS".equals(event.getStatus())) {
            log.info("Order created successfully: orderId={}, symbol={}, quantity={}",
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
            log.error("Order creation failed: correlationId={}, error={}",
                    event.getCorrelationId(), event.getErrorMessage());
        } else if ("CANCELED".equals(event.getStatus())) {
            log.info("Order canceled: orderId={}, correlationId={}",
                    event.getOrderId(), event.getCorrelationId());
        }
    }
}

/*
     How the Frontend should connect:
     1. Connect: Connect to http://localhost:8080/ws using SockJS/Stomp.
     2. Subscribe: Subscribe to /topic/orders/{myUserId}.
     3. Receive: Listen for messages on that topic to get real-time updates about order status (SUCCESS/FAILED).

*/
