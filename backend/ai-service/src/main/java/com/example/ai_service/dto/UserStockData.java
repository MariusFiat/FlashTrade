package com.example.ai_service.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserStockData {
    
    @NotEmpty(message = "Dates array cannot be empty")
    private List<String> dates;
    
    @NotEmpty(message = "Prices array cannot be empty")
    @Size(min = 1, message = "At least one price is required")
    private List<Double> prices;
    
    public void validate() {
        if (dates.size() != prices.size()) {
            throw new IllegalArgumentException("Dates and prices arrays must have the same length");
        }
    }
}