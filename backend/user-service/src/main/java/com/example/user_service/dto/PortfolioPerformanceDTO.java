package com.example.user_service.dto;

import java.time.LocalDateTime;
import java.util.List;

public record PortfolioPerformanceDTO(
        List<HistoryPoint> history,
        Double currentTotalValue
) {
    public record HistoryPoint(Double value, LocalDateTime date) {}
}