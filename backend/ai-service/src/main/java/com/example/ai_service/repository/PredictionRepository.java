package com.example.ai_service.repository;

import com.example.ai_service.entity.StockPrediction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PredictionRepository extends JpaRepository<StockPrediction, Long> {
    
    // Find all predictions for a symbol, ordered by creation date (newest first)
    List<StockPrediction> findBySymbolOrderByCreatedAtDesc(String symbol);
    
    // Find latest prediction for a symbol
    StockPrediction findTopBySymbolOrderByCreatedAtDesc(String symbol);
    
    // Find predictions for a symbol with limit
    List<StockPrediction> findTop10BySymbolOrderByCreatedAtDesc(String symbol);
}