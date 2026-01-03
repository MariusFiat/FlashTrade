package com.example.gateway_service.messaging.dto;

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

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
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
