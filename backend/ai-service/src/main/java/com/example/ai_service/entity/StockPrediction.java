package com.example.ai_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "stock_predictions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockPrediction {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String symbol;
    
    @Column(name = "current_price")
    private Double currentPrice;
    
    @Column(name = "predicted_price", nullable = false)
    private Double predictedPrice;
    
    @Column(name = "prediction_day")
    private Integer predictionDay; // 1 for next day, 7 for week ahead, etc.
    
    @Column(name = "lower_bound")
    private Double lowerBound;
    
    @Column(name = "upper_bound")
    private Double upperBound;
    
    @Column(nullable = false)
    private Double confidence;
    
    @Column(name = "model_used")
    private String modelUsed; // "prophet", "lstm", etc.
    
    @Column(name = "training_data_points")
    private Long trainingDataPoints;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}