package com.example.ai_service.controller;

import com.example.ai_service.dto.PredictionResponse;
import com.example.ai_service.entity.StockPrediction;
import com.example.ai_service.repository.PredictionRepository;
import com.example.ai_service.service.PricePredictionService;
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
public class PredictionController {

    private static final Logger logger = LoggerFactory.getLogger(PredictionController.class);

    @Autowired
    private PricePredictionService pricePredictionService;

    @Autowired
    private PredictionRepository predictionRepository;

    /**
     * Get price prediction for a stock symbol
     */
    @GetMapping("/predictions/{symbol}")
    public ResponseEntity<?> getPrediction(@PathVariable String symbol) {
        try {
            logger.info("Received prediction request for symbol: {}", symbol);
            PredictionResponse prediction = pricePredictionService.predictPrice(symbol);
            return ResponseEntity.ok(prediction);
        } catch (Exception e) {
            logger.error("Error getting prediction for {}: {}", symbol, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to get prediction for " + symbol));
        }
    }

    /**
     * Get prediction history for a stock symbol
     */
    @GetMapping("/predictions/history/{symbol}")
    public ResponseEntity<?> getPredictionHistory(
            @PathVariable String symbol,
            @RequestParam(defaultValue = "10") int limit) {
        try {
            logger.info("Received prediction history request for symbol: {} with limit: {}", symbol, limit);
            
            // Fetch predictions from database
            List<StockPrediction> predictions = predictionRepository.findBySymbolOrderByCreatedAtDesc(symbol);
            
            // Limit results
            if (limit > 0 && predictions.size() > limit) {
                predictions = predictions.subList(0, limit);
            }
            
            // Map to response format
            List<Map<String, Object>> history = predictions.stream()
                    .map(p -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("id", p.getId());
                        map.put("symbol", p.getSymbol());
                        map.put("currentPrice", p.getCurrentPrice());
                        map.put("predictedPrice", p.getPredictedPrice());
                        map.put("confidence", p.getConfidence());
                        map.put("createdAt", p.getCreatedAt().toString());
                        return map;
                    })
                    .collect(Collectors.toList());
            
            logger.info("Returning {} prediction history records for {}", history.size(), symbol);
            return ResponseEntity.ok(history);
            
        } catch (Exception e) {
            logger.error("Error fetching prediction history for {}: {}", symbol, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch prediction history for " + symbol));
        }
    }

    /**
     * Get all predictions (for admin/debugging)
     */
    @GetMapping("/predictions/all")
    public ResponseEntity<?> getAllPredictions(@RequestParam(defaultValue = "50") int limit) {
        try {
            logger.info("Received request for all predictions with limit: {}", limit);
            
            List<StockPrediction> predictions = predictionRepository.findAll();
            
            // Sort by creation date descending
            predictions.sort((p1, p2) -> p2.getCreatedAt().compareTo(p1.getCreatedAt()));
            
            // Limit results
            if (limit > 0 && predictions.size() > limit) {
                predictions = predictions.subList(0, limit);
            }
            
            // Map to response format
            List<Map<String, Object>> history = predictions.stream()
                    .map(p -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("id", p.getId());
                        map.put("symbol", p.getSymbol());
                        map.put("currentPrice", p.getCurrentPrice());
                        map.put("predictedPrice", p.getPredictedPrice());
                        map.put("confidence", p.getConfidence());
                        map.put("createdAt", p.getCreatedAt().toString());
                        return map;
                    })
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(history);
            
        } catch (Exception e) {
            logger.error("Error fetching all predictions: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch predictions"));
        }
    }
}