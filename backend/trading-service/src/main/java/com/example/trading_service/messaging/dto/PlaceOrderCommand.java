package com.example.trading_service.messaging.dto;

import com.example.trading_service.entities.order.OrderSide;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class PlaceOrderCommand {
    private String symbol;
    private int quantity;
    private BigDecimal price;
    private String side;
    private String userId;  // To track who placed the order
    private String correlationId;  // To track the request-response

    public PlaceOrderCommand() {
    }

    public PlaceOrderCommand(String symbol, int quantity, BigDecimal price, String side, String userId, String correlationId) {
        this.symbol = symbol;
        this.quantity = quantity;
        this.price = price;
        this.side = side;
        this.userId = userId;
        this.correlationId = correlationId;
    }
}
