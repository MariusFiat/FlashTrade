package com.example.trading_service.repository;

import com.example.trading_service.entities.market.StockHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StockHistoryRepository extends JpaRepository<StockHistory, Long> {
    List<StockHistory> findByStockSymbolOrderByTimestampAsc(String symbol);
    List<StockHistory> findByStockSymbolAndTimestampBetweenOrderByTimestampAsc(
            String symbol,
            LocalDateTime start,
            LocalDateTime end
    );
    void deleteByTimestampBefore(LocalDateTime expiryDate);
}