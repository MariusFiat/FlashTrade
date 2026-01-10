package com.example.user_service.repository;

import com.example.user_service.entities.TransactionHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionHistoryRepository extends JpaRepository<TransactionHistory, Long> {
    List<TransactionHistory> findTransactionsByWalletId(Long walletId);
    List<TransactionHistory> findByWalletIdOrderByTimestampDesc(Long walletId);
}