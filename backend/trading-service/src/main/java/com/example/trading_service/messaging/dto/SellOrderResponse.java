package com.example.trading_service.messaging.dto;

import lombok.Data;

@Data
public class SellOrderResponse {
    private String orderId;
    private boolean approved;
    private String message;
}
