package com.example.trading_service.messaging.dto;

import lombok.Data;

@Data
public class SellOrderCloseRequest {
    private String orderId;
    private String userId;
    private String symbol;
    private Double quantity;
    private Double amountReceived;
    private String status;
}
