package com.example.ai_service.repository;

import com.example.ai_service.entity.SentimentScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SentimentRepository extends JpaRepository<SentimentScore, Long> {
    
    // Find all sentiment scores for a symbol, ordered by creation date (newest first)
    List<SentimentScore> findBySymbolOrderByCreatedAtDesc(String symbol);
    
    // Find latest sentiment for a symbol
    SentimentScore findTopBySymbolOrderByCreatedAtDesc(String symbol);
    
    // Find sentiment scores for a symbol with limit
    List<SentimentScore> findTop10BySymbolOrderByCreatedAtDesc(String symbol);
}