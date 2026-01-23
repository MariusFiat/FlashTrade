package com.example.gateway_service.messaging.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TradeRecord {
    private Long tradeId;
    private String symbol;
    private int quantity;
    private BigDecimal price;
    private String side; // BUY or SELL
    private Instant timestamp;
}