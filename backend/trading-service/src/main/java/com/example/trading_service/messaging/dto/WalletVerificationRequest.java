package com.example.trading_service.messaging.dto;

import com.example.trading_service.entities.order.OrderSide;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WalletVerificationRequest {
    private String userId;
    private Long orderId;
    private OrderSide orderSide;
    private BigDecimal amount;
    private String correlationId;
    private String symbol;
    private int quantity;
}
