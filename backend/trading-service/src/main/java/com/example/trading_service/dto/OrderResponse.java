package com.example.trading_service.dto;

import java.math.BigDecimal;
import java.time.Instant;

import com.example.trading_service.entities.order.OrderSide;

public class OrderResponse {
    private Long id;
    private String symbol;
    private int quantity;
    private BigDecimal price;
    private Instant createdAt;
    private OrderSide orderSide;
    private String userId;

    public OrderResponse(Long id, String symbol, int quantity, BigDecimal price, Instant createdAt, OrderSide orderSide) {
        this.id = id;
        this.symbol = symbol;
        this.quantity = quantity;
        this.price = price;
        this.createdAt = createdAt;
        this.orderSide = orderSide;
    }

    public OrderResponse(Long id, String symbol, int quantity, BigDecimal price, Instant createdAt, OrderSide orderSide, String userId) {
        this.id = id;
        this.symbol = symbol;
        this.quantity = quantity;
        this.price = price;
        this.createdAt = createdAt;
        this.orderSide = orderSide;
        this.userId = userId;
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

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }


    public OrderSide getOrderSide() {
        return orderSide;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
