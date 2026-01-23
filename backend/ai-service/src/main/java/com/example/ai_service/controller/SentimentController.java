package com.example.ai_service.controller;

import com.example.ai_service.dto.SentimentResponse;
import com.example.ai_service.entity.SentimentScore;
import com.example.ai_service.repository.SentimentRepository;
import com.example.ai_service.service.SentimentAnalysisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "*")
public class SentimentController {

    private static final Logger logger = LoggerFactory.getLogger(SentimentController.class);

    @Autowired
    private SentimentAnalysisService sentimentAnalysisService;

    @Autowired
    private SentimentRepository sentimentRepository;

    /**
     * Get sentiment analysis for a stock symbol
     */
    @GetMapping("/sentiment/{symbol}")
    public ResponseEntity<?> getSentiment(@PathVariable String symbol) {
        try {
            logger.info("Received sentiment request for symbol: {}", symbol);
            SentimentResponse sentiment = sentimentAnalysisService.analyzeSentiment(symbol);
            return ResponseEntity.ok(sentiment);
        } catch (Exception e) {
            logger.error("Error getting sentiment for {}: {}", symbol, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to get sentiment for " + symbol));
        }
    }

    /**
     * Get sentiment history for a stock symbol
     */
    @GetMapping("/sentiment/history/{symbol}")
    public ResponseEntity<?> getSentimentHistory(
            @PathVariable String symbol,
            @RequestParam(defaultValue = "10") int limit) {
        try {
            logger.info("Received sentiment history request for symbol: {} with limit: {}", symbol, limit);
            
            // Fetch sentiment scores from database
            List<SentimentScore> sentiments = sentimentRepository.findBySymbolOrderByCreatedAtDesc(symbol);
            
            // Limit results
            if (limit > 0 && sentiments.size() > limit) {
                sentiments = sentiments.subList(0, limit);
            }
            
            // Map to response format
            List<Map<String, Object>> history = sentiments.stream()
                    .map(s -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("id", s.getId());
                        map.put("symbol", s.getSymbol());
                        map.put("score", s.getScore());
                        map.put("sentiment", s.getClassification());
                        map.put("newsCount", s.getArticleCount());
                        map.put("createdAt", s.getCreatedAt().toString());
                        return map;
                    })
                    .collect(Collectors.toList());
            
            logger.info("Returning {} sentiment history records for {}", history.size(), symbol);
            return ResponseEntity.ok(history);
            
        } catch (Exception e) {
            logger.error("Error fetching sentiment history for {}: {}", symbol, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch sentiment history for " + symbol));
        }
    }

    /**
     * Get all sentiments (for admin/debugging)
     */
    @GetMapping("/sentiment/all")
    public ResponseEntity<?> getAllSentiments(@RequestParam(defaultValue = "50") int limit) {
        try {
            logger.info("Received request for all sentiments with limit: {}", limit);
            
            List<SentimentScore> sentiments = sentimentRepository.findAll();
            
            // Sort by creation date descending
            sentiments.sort((s1, s2) -> s2.getCreatedAt().compareTo(s1.getCreatedAt()));
            
            // Limit results
            if (limit > 0 && sentiments.size() > limit) {
                sentiments = sentiments.subList(0, limit);
            }
            
            // Map to response format
            List<Map<String, Object>> history = sentiments.stream()
                    .map(s -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("id", s.getId());
                        map.put("symbol", s.getSymbol());
                        map.put("score", s.getScore());
                        map.put("sentiment", s.getClassification());
                        map.put("newsCount", s.getArticleCount());
                        map.put("createdAt", s.getCreatedAt().toString());
                        return map;
                    })
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(history);
            
        } catch (Exception e) {
            logger.error("Error fetching all sentiments: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch sentiments"));
        }
    }
}