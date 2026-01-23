package com.example.user_service.messaging.dto;

import lombok.Data;

@Data
public class SellOrderRequest {
    private String orderId;
    private String userId;
    private String symbol;
    private Double quantity;
}