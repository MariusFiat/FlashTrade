package com.example.trading_service.messaging.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WalletVerificationResponse {
    private String userId;
    private Long orderId;
    private boolean approved;
    private String message;
    private String correlationId;
}
