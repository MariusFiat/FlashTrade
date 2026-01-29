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
public class MLPredictionRequest {
    
    private String symbol;
    private List<String> dates;
    private List<Double> prices;
    private Integer predictionDays;
    private String modelType; // "prophet", "lstm", etc.
}