package com.example.trading_service.messaging.dto;

import com.example.trading_service.entities.order.OrderSide;
import com.example.trading_service.model.OrderType;

import java.math.BigDecimal;
import java.time.Instant;

public class OrderCreatedEvent {
    private Long orderId;
    private String symbol;
    private int quantity;
    private BigDecimal price;
    private OrderSide orderSide;
    private String userId;
    private Instant createdAt;
    private String correlationId;
    private String status;  // SUCCESS, FAILED
    private String errorMessage;

    public OrderCreatedEvent() {
    }

    public OrderCreatedEvent(Long orderId, String symbol, int quantity, BigDecimal price, OrderSide orderSide, String userId, Instant createdAt, String correlationId, String status, String errorMessage) {
        this.orderId = orderId;
        this.symbol = symbol;
        this.quantity = quantity;
        this.price = price;
        this.orderSide = orderSide;
        this.userId = userId;
        this.createdAt = createdAt;
        this.correlationId = correlationId;
        this.status = status;
        this.errorMessage = errorMessage;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
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

//    public OrderType getOrderType() {
//        return orderType;
//    }
//
//    public void setOrderType(OrderType orderType) {
//        this.orderType = orderType;
//    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

}
