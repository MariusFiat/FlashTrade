package com.example.gateway_service.messaging.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class PlaceOrderCommand {
    private String symbol;
    private int quantity;
    private double price;
    private String orderType;  // Using String instead of enum for gateway
    private String userId;
    private String correlationId;

    public PlaceOrderCommand() {
    }

    public PlaceOrderCommand(String symbol, int quantity, double price, String orderType, String userId, String correlationId) {
        this.symbol = symbol;
        this.quantity = quantity;
        this.price = price;
        this.orderType = orderType;
        this.userId = userId;
        this.correlationId = correlationId;
    }
}
