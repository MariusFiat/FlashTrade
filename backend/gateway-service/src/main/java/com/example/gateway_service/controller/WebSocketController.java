package com.example.gateway_service.controller;

import com.example.gateway_service.messaging.dto.TransactionUpdate;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {

    @MessageMapping("/test")
    @SendTo("/topic/transactions")
    public TransactionUpdate testMessage(String message) {
        return new TransactionUpdate(
                "test-id",
                "test-user",
                "AAPL",
                "BUY",
                10.0,
                150.0,
                "SUCCESS",
                java.time.LocalDateTime.now()
        );
    }
}
