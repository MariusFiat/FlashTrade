package com.example.ai_service.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;

@Entity
@Table(name = "sentiment_scores")
@Data
public class SentimentScore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String symbol;

    @Column(nullable = false)
    private Double score;  // -1.0 to 1.0

    @Column(nullable = false)
    private String classification;  // "POSITIVE", "NEGATIVE", "NEUTRAL"

    @Column(nullable = false)
    private Integer articleCount;  // How many articles analyzed

    @Column(nullable = false)
    private Instant createdAt;
}
