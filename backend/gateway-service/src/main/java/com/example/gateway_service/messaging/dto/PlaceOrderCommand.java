package com.example.gateway_service.messaging.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlaceOrderCommand {
    private String symbol;
    private int quantity;
    private double price;
    private String orderType;  // Using String instead of enum for gateway
    private String userId;
    private String correlationId;
}
