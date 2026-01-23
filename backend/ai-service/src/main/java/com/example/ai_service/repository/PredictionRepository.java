package com.example.ai_service.repository;

import com.example.ai_service.entity.StockPrediction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface PredictionRepository extends JpaRepository<StockPrediction, Long> {
    List<StockPrediction> findBySymbolOrderByCreatedAtDesc(String symbol);
    Optional<StockPrediction> findFirstBySymbolOrderByCreatedAtDesc(String symbol);
    List<StockPrediction> findByCreatedAtAfter(Instant timestamp);
}