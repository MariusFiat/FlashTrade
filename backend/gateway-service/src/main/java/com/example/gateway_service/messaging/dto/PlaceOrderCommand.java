package com.example.gateway_service.messaging.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlaceOrderCommand {
    private String symbol;
    private int quantity;
    private double price;
    private String side;
    private String userId;
    private String correlationId;

    public PlaceOrderCommand() {
    }

    public PlaceOrderCommand(String symbol, int quantity, double price, String orderType, String userId, String correlationId) {
        this.symbol = symbol;
        this.quantity = quantity;
        this.price = price;
        this.side = orderType;
        this.userId = userId;
        this.correlationId = correlationId;
    }
}
