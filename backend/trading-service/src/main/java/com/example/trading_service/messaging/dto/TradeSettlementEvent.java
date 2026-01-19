package com.example.trading_service.messaging.dto;

import com.example.trading_service.entities.order.OrderSide;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class TradeSettlementEvent {
    private Long tradeId;
    private String symbol;
    private int quantity;
    private BigDecimal price;
    private Long buyOrderId;
    private String buyerId;
    private Long sellOrderId;
    private String sellerId;
}
