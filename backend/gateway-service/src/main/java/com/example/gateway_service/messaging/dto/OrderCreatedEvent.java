package com.example.gateway_service.messaging.dto;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreatedEvent {
    private Long orderId;
    private String symbol;
    private int quantity;
    private double price;
    private String orderType;
    private String userId;
    private Instant createdAt;
    private String correlationId;
    private String status;
    private String errorMessage;
}
