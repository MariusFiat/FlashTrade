package com.example.user_service.dto;

public record PortfolioItemDTO(
        Long id,
        String stock,
        Double shares,
        Double portfolioValue,
        Double allocation
) {}