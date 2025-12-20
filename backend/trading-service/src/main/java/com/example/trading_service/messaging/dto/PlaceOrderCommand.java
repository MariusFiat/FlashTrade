package com.example.trading_service.messaging.dto;

import com.example.trading_service.entities.OrderType;

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
    private OrderType orderType;
    private String userId;  // To track who placed the order
    private String correlationId;  // To track the request-response
}
