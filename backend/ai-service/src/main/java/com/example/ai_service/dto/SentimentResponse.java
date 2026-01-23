package com.example.ai_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SentimentResponse {
    private String symbol;
    private Double score;
    private String classification;
    private Integer articleCount;
    private Long timestamp;
}