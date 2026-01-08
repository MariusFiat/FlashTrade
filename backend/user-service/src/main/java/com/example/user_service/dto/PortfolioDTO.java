package com.example.user_service.dto;

public record PortfolioDTO(
        Long id,
        String stock,
        Double shares,
        Double portfolioValue,
        Double allocation
) {}