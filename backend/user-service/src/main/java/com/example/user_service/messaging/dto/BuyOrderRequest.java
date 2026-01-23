package com.example.user_service.messaging.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BuyOrderRequest {
    private String orderId;
    private String userId;
    private String stockSymbol;
    private Double quantity;
    private Double priceAtOrder;
}