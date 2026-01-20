package com.example.gateway_service.messaging.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionUpdate {
    private String orderId;
    private String userId;
    private String symbol;
    private String side;
    private Double quantity;
    private Double price;
    private String status;
    private LocalDateTime timestamp;
}
