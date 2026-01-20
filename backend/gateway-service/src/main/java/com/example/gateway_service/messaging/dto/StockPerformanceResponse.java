package com.example.gateway_service.messaging.dto;

import com.example.gateway_service.dto.StockHistoryPointDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockPerformanceResponse {
    private String symbol;
    private List<StockHistoryPointDTO> history;
    private Double currentPrice;
    private String correlationId;
}