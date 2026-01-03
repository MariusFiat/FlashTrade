package com.example.trading_service.messaging.dto;

import com.example.trading_service.entities.OrderType;

public class PlaceOrderCommand {
    private String symbol;
    private int quantity;
    private double price;
    private OrderType orderType;
    private String userId;  // To track who placed the order
    private String correlationId;  // To track the request-response

    public PlaceOrderCommand() {
    }

    public PlaceOrderCommand(String symbol, int quantity, double price, OrderType orderType, String userId, String correlationId) {
        this.symbol = symbol;
        this.quantity = quantity;
        this.price = price;
        this.orderType = orderType;
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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public OrderType getOrderType() {
        return orderType;
    }

    public void setOrderType(OrderType orderType) {
        this.orderType = orderType;
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
