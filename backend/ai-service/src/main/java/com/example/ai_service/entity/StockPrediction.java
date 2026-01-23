package com.example.ai_service.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;

@Entity
@Table(name = "stock_predictions")
@Data
public class StockPrediction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String symbol;

    @Column(nullable = false)
    private Double predictedPrice;

    @Column(nullable = false)
    private Double currentPrice;

    @Column(nullable = false)
    private Double confidence;  // 0.0 to 1.0

    @Column(nullable = false)
    private Instant createdAt;

    @Column
    private Double actualPrice;

    @Column
    private Double error;  // |predicted - actual| / actual
}
