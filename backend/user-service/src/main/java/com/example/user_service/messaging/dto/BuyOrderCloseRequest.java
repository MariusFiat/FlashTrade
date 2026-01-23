package com.example.user_service.messaging.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BuyOrderCloseRequest {
    private String orderId;
    private String userId;
    private String status; // "MATCHED" sau "CLOSED" (Canceled)
    private Double amountSpent; // The amount spent can be <= than I reserved initially because if I ordered 5 stock, I can buy 3 + 2 (2 different sellers)
    private String symbol;
    private Double quantity;
}
