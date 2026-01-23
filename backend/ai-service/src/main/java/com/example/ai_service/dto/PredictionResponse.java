package com.example.ai_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PredictionResponse {
    private String symbol;
    private Double predictedPrice;
    private Double confidence;
    private String timeframe;
    private Long timestamp;
}