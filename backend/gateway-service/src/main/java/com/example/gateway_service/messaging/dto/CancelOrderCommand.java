package com.example.gateway_service.messaging.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CancelOrderCommand {
    private Long orderId;
    private String userId;
    private String correlationId;
}
