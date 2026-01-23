package com.example.gateway_service.messaging.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardUpdate {
    private Double totalValue;
    private Double totalGain;
    private Double percentageGain;
    private Map<String, Double> holdings;
}
