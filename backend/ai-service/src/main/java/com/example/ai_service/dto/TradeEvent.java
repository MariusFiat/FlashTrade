package com.example.ai_service.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TradeEvent {
    private String symbol;
    private String orderType; // BUY or SELL
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal priceChange;
    private LocalDateTime timestamp;
}