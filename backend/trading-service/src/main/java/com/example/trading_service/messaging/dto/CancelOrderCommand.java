package com.example.trading_service.messaging.dto;

import lombok.Data;

@Data
public class CancelOrderCommand {
    private Long orderId;
    private String userId;
    private String correlationId;
}
