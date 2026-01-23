package com.example.gateway_service.messaging;

import com.example.gateway_service.messaging.dto.TransactionUpdate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class WebSocketNotificationService {
    
    private final SimpMessagingTemplate messagingTemplate;
    
    public WebSocketNotificationService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }
    
    public void sendTransactionUpdate(String userId, TransactionUpdate update) {
        messagingTemplate.convertAndSendToUser(userId, "/topic/transactions", update);
    }
    
    public void broadcastTransactionUpdate(TransactionUpdate update) {
        messagingTemplate.convertAndSend("/topic/transactions", update);
    }
}
