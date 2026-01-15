package com.example.trading_service.repository;

import com.example.trading_service.entities.trade.Trade;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TradeRepository extends JpaRepository<Trade, Long> {
}
