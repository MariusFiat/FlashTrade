package com.example.trading_service.messaging.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ActiveOrdersResponse {
    private String userId;
    private List<ActiveOrderRecord> activeOrders;
}
