package com.example.ai_service.repository;

import com.example.ai_service.entity.SentimentScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface SentimentRepository extends JpaRepository<SentimentScore, Long> {
    List<SentimentScore> findBySymbolOrderByCreatedAtDesc(String symbol);
    Optional<SentimentScore> findFirstBySymbolOrderByCreatedAtDesc(String symbol);
    List<SentimentScore> findBySymbolAndCreatedAtAfter(String symbol, Instant timestamp);
    SentimentScore findTopBySymbolOrderByCreatedAtDesc(String symbol);
}