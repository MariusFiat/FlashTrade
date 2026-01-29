package com.example.ai_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MLPredictionResponse {
    
    private String symbol;
    private List<String> predictionDates;
    private List<Double> predictedPrices;
    private List<Double> lowerBounds;
    private List<Double> upperBounds;
    private Double confidence;
    private String modelUsed;
    private Long trainingDataPoints;
}