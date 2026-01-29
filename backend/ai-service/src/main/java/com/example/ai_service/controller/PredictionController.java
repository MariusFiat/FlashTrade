package com.example.ai_service.controller;

import com.example.ai_service.dto.PredictionRequest;
import com.example.ai_service.dto.PredictionResponse;
import com.example.ai_service.entity.StockPrediction;
import com.example.ai_service.repository.PredictionRepository;
import com.example.ai_service.service.PricePredictionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ai/predictions")
@RequiredArgsConstructor
@Slf4j
public class PredictionController {
    
    private final PricePredictionService predictionService;
    private final PredictionRepository predictionRepository;
    
    /**
     * GET endpoint for simple prediction requests.
     * 
     * @param symbol Stock ticker symbol
     * @param days Number of days to predict (optional, default 7)
     * @return PredictionResponse with multi-day predictions
     */
    @GetMapping("/{symbol}")
    public ResponseEntity<PredictionResponse> getPrediction(
            @PathVariable String symbol,
            @RequestParam(required = false, defaultValue = "7") Integer days) {
        
        log.info("GET prediction request for {} with {} days", symbol, days);
        
        PredictionResponse response = predictionService.predictPrice(symbol, days);
        return ResponseEntity.ok(response);
    }
    
    /**
     * POST endpoint for predictions with user-provided data.
     * 
     * @param request PredictionRequest with symbol, days, and optional user data
     * @return PredictionResponse with multi-day predictions
     */
    @PostMapping("/predict")
    public ResponseEntity<PredictionResponse> predictWithUserData(
        @RequestBody PredictionRequest request) {
        
        log.info("POST prediction request for {} with {} days and user data: {}", 
                request.getSymbol(), request.getPredictionDays(), 
                request.getUserData() != null ? "provided" : "not provided");
        
        PredictionResponse response = predictionService.predictWithUserData(request);
        return ResponseEntity.ok(response);
    }
    
    /**
     * GET endpoint for historical predictions.
     * 
     * @param symbol Stock ticker symbol
     * @param limit Number of historical predictions to return
     * @return List of historical predictions
     */
    @GetMapping("/history/{symbol}")
    public ResponseEntity<List<StockPrediction>> getPredictionHistory(
            @PathVariable String symbol,
            @RequestParam(required = false, defaultValue = "10") Integer limit) {
        
        log.info("GET prediction history for {} with limit {}", symbol, limit);
        
        List<StockPrediction> history = predictionRepository
                .findBySymbolOrderByCreatedAtDesc(symbol)
                .stream()
                .limit(limit)
                .toList();
        
        return ResponseEntity.ok(history);
    }
    
    /**
     * GET endpoint for all predictions (admin/debugging).
     * 
     * @param limit Number of predictions to return
     * @return List of all predictions
     */
    @GetMapping("/all")
    public ResponseEntity<List<StockPrediction>> getAllPredictions(
            @RequestParam(required = false, defaultValue = "50") Integer limit) {
        
        log.info("GET all predictions with limit {}", limit);
        
        List<StockPrediction> predictions = predictionRepository
                .findAll()
                .stream()
                .limit(limit)
                .toList();
        
        return ResponseEntity.ok(predictions);
    }
}