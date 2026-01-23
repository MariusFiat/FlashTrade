package com.example.trading_service.repository;

import com.example.trading_service.entities.trade.Trade;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TradeRepository extends JpaRepository<Trade, Long> {
    List<Trade> findByBuyerIdOrSellerId(String buyerId, String sellerId);
}
