package com.example.trading_service.repository;

import com.example.trading_service.entities.market.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockRepository extends JpaRepository<Stock, String> {

}
