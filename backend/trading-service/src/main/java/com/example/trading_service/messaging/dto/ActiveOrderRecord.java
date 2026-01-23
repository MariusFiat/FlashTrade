package com.example.trading_service.messaging.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ActiveOrderRecord {
    private Long orderId;
    private String symbol;
    private int originalQty;
    private int filledQty;
    private BigDecimal price;
    private String side; // BUY or SELL
    private String status;
    private Instant createdAt;
}
