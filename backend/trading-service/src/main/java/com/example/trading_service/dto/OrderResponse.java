package com.example.trading_service.dto;

import com.example.trading_service.entities.Order;
import com.example.trading_service.entities.OrderType;

import java.time.Instant;

public class OrderResponse {
    private Long id;
    private String symbol;
    private int quantity;
    private double price;
    private OrderType orderType;
    private Instant createdAt;

    public OrderResponse(Long id, String symbol, int quantity, double price, OrderType orderType, Instant createdAt) {
        this.id = id;
        this.symbol = symbol;
        this.quantity = quantity;
        this.price = price;
        this.orderType = orderType;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
