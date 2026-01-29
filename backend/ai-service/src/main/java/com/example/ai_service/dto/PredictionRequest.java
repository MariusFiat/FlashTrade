package com.example.ai_service.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PredictionRequest {
    
    @NotBlank(message = "Stock symbol is required")
    private String symbol;
    
    @NotNull(message = "Prediction days is required")
    @Min(value = 1, message = "Prediction days must be at least 1")
    private Integer predictionDays;
    
    private UserStockData userData;
}