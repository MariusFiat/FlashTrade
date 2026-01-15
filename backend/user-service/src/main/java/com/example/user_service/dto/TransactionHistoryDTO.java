package com.example.user_service.dto;

import java.time.LocalDateTime;

public record TransactionHistoryDTO(
        Long id,
        String actionType,
        Double amount,
        String status,
        LocalDateTime timestamp
) {}