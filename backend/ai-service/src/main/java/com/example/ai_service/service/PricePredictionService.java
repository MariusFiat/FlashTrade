package com.example.ai_service.service;

import com.example.ai_service.dto.PredictionResponse;
import com.example.ai_service.entity.StockPrediction;
import com.example.ai_service.exception.DataFetchException;
import com.example.ai_service.exception.PredictionFailedException;
import com.example.ai_service.repository.PredictionRepository;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PricePredictionService {
    private static final Logger log = LoggerFactory.getLogger(PricePredictionService.class);
    
    private final DataFetchService dataFetchService;
    private final PredictionRepository predictionRepository;
    
    @Value("${python.executable:python3}")
    private String pythonExecutable;
    
    @Value("${python.scripts.path:src/main/java/ML}")
    private String pythonScriptsPath;
    
    public PricePredictionService(DataFetchService dataFetchService, 
                                  PredictionRepository predictionRepository) {
        this.dataFetchService = dataFetchService;
        this.predictionRepository = predictionRepository;
    }
    
    public PredictionResponse predictPrice(String symbol) {
        log.info("Predicting price for symbol: {}", symbol);
        
        try {
            // STEP 1: Get historical price data
            List<Double> historicalPrices = dataFetchService.getHistoricalPrices(symbol, 60);
            
            if (historicalPrices.isEmpty()) {
                throw new DataFetchException("No historical data available for " + symbol);
            }

            double currentPrice = historicalPrices.get(historicalPrices.size() - 1);

            // STEP 2: Call Python ML model
            double predictedPrice = callPythonModel(symbol, historicalPrices);
            
            // STEP 3: Calculate confidence
            double confidence = calculateConfidence(historicalPrices, predictedPrice);
            
            // STEP 4: Save prediction to database
            StockPrediction entity = new StockPrediction();
            entity.setSymbol(symbol);
            entity.setPredictedPrice(predictedPrice);
            entity.setCurrentPrice(currentPrice);
            entity.setConfidence(confidence);
            entity.setCreatedAt(Instant.now());
            predictionRepository.save(entity);
            
            // STEP 5: Return response
            return PredictionResponse.builder()
                .symbol(symbol)
                .predictedPrice(predictedPrice)
                .currentPrice(currentPrice)
                .confidence(confidence)
                .timeframe("1D")
                .timestamp(Instant.now().toEpochMilli())
                .build();
                
        } catch (Exception e) {
            log.error("Failed to predict price for {}", symbol, e);
            throw new PredictionFailedException("Failed to predict price for " + symbol, e);
        }
    }
    
    public List<PredictionResponse> predictMultiple(List<String> symbols) {
        return symbols.stream()
            .map(this::predictPrice)
            .collect(Collectors.toList());
    }
    
    private double callPythonModel(String symbol, List<Double> prices) {
        try {
            // Convert prices list to JSON
            Gson gson = new Gson();
            String pricesJson = gson.toJson(prices);
            
            // Build Python command
            ProcessBuilder pb = new ProcessBuilder(
                pythonExecutable,
                pythonScriptsPath + "/price_prediction/predict.py",
                symbol,
                pricesJson
            );
            
            pb.redirectErrorStream(true);
            Process process = pb.start();
            
            // Read output
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream())
            );
            
            String result = reader.readLine();
            int exitCode = process.waitFor();
            
            if (exitCode != 0 || result == null) {
                log.warn("Python script failed, using fallback prediction");
                return calculateFallbackPrediction(prices);
            }
            
            return Double.parseDouble(result.trim());
            
        } catch (Exception e) {
            log.error("Error calling Python model", e);
            return calculateFallbackPrediction(prices);
        }
    }
    
    private double calculateFallbackPrediction(List<Double> prices) {
        // Simple moving average prediction
        if (prices.isEmpty()) return 100.0;
        
        int window = Math.min(5, prices.size());
        double sum = 0;
        for (int i = prices.size() - window; i < prices.size(); i++) {
            sum += prices.get(i);
        }
        return sum / window;
    }
    
    private double calculateConfidence(List<Double> historical, double predicted) {
        if (historical.isEmpty()) return 0.5;
        
        // Calculate volatility
        double mean = historical.stream().mapToDouble(Double::doubleValue).average().orElse(0);
        double variance = historical.stream()
            .mapToDouble(price -> Math.pow(price - mean, 2))
            .average()
            .orElse(0);
        double stdDev = Math.sqrt(variance);
        double volatility = stdDev / mean;
        
        // Lower volatility = higher confidence
        return Math.max(0.3, Math.min(0.95, 1.0 - volatility));
    }
}