package com.example.gateway_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockPerformanceDTO {
    private String symbol;
    private List<StockHistoryPointDTO> history;
    private Double currentPrice;
    private String range; //1d, 1w ..
}