package com.example.user_service.dto;

import java.util.List;

public record PortfolioSummaryDTO(
        List<PortfolioItemDTO> items,
        Double totalInvested,
        Double totalValue,
        Double totalReturn
) {}