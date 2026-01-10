package com.example.user_service.dto;

public record WalletDTO(
        Double balance,
        Double totalDeposit,
        Double totalWithdrawal,
        Double pandingBalance,
        String currency,
        Double totalInvested
) {}