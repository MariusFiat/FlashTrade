package com.example.user_service.dto;

import lombok.Data;

@Data
public class SellOrderRequest {
    private String orderId;
    private Long userId;
    private String symbol;
    private Double quantity;
}