package com.example.ai_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PredictionResponse {
    
    private String symbol;
    private Double currentPrice;
    private List<DailyPrediction> predictions;
    private Double confidence;
    private String timeframe;
    private Long timestamp;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DailyPrediction {
        private String date;
        private Double predictedPrice;
        private Double lowerBound;
        private Double upperBound;
        private Double changePercent;
    }
}