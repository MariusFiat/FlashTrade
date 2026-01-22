package com.example.trading_service.messaging.dto;

import com.example.trading_service.entities.order.OrderSide;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PlaceOrderCommand {
    private String symbol;
    private int quantity;
    private BigDecimal price;
    private OrderSide orderSide;
    private String userId;  // To track who placed the order
    private String correlationId;  // To track the request-response

    public PlaceOrderCommand() {
    }

    public PlaceOrderCommand(String symbol, int quantity, BigDecimal price, OrderSide orderSide, String userId, String correlationId) {
        this.symbol = symbol;
        this.quantity = quantity;
        this.price = price;
        this.orderSide = orderSide;
        this.userId = userId;
        this.correlationId = correlationId;
    }
}
