package com.example.trading_service.dto;

import com.example.trading_service.entities.order.OrderType;

import java.math.BigDecimal;
import java.time.Instant;

public class OrderResponse {
    private Long id;
    private String symbol;
    private int quantity;
    private BigDecimal price;
    private OrderType orderType;
    private Instant createdAt;

    public OrderResponse(Long id, String symbol, int quantity, BigDecimal price, OrderType orderType, Instant createdAt) {
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

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
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
