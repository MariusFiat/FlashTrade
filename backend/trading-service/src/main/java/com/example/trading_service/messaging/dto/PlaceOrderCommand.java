package com.example.trading_service.messaging.dto;

import com.example.trading_service.entities.order.OrderSide;

import java.math.BigDecimal;

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

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public OrderSide getOrderSide() {
        return orderSide;
    }

    public void setOrderSide(OrderSide orderSide) {
        this.orderSide = orderSide;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }
}
