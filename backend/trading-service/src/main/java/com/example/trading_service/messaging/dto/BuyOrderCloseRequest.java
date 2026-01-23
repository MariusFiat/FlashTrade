package com.example.trading_service.messaging.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BuyOrderCloseRequest {
    private String orderId;
    private String userId;
    private String status;     // MATCHED / CLOSED
    private Double amountSpent;
    private String symbol;
    private Double quantity;
}
