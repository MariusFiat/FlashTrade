package com.example.trading_service.messaging.dto;

import java.time.Instant;

import com.example.trading_service.entities.OrderType;

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
    private OrderType orderType;
    private String userId;
    private Instant createdAt;
    private String correlationId;
    private String status;  // SUCCESS, FAILED
    private String errorMessage;
}
