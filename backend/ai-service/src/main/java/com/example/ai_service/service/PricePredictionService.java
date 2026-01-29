package com.example.ai_service.service;

import com.example.ai_service.dto.*;
import com.example.ai_service.entity.StockPrediction;
import com.example.ai_service.exception.PredictionFailedException;
import com.example.ai_service.repository.PredictionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class PricePredictionService {
    
    private final DataFetchService dataFetchService;
    private final PredictionRepository predictionRepository;
    private final WebClient mlServiceWebClient;
    
    private static final int DEFAULT_HISTORICAL_DAYS = 90;
    private static final int DEFAULT_PREDICTION_DAYS = 7;
    private static final String DEFAULT_MODEL = "prophet";
    
    /**
     * Generates price prediction for a stock symbol using only public data.
     * 
     * @param symbol Stock ticker symbol
     * @param predictionDays Number of days to predict (default 7)
     * @return PredictionResponse with multi-day predictions
     */
    public PredictionResponse predictPrice(String symbol, Integer predictionDays) {
        return predictWithUserData(PredictionRequest.builder()
                .symbol(symbol)
                .predictionDays(predictionDays != null ? predictionDays : DEFAULT_PREDICTION_DAYS)
                .userData(null)
                .build());
    }
    
    /**
     * Generates price prediction combining public data with user-provided data.
     * 
     * @param request PredictionRequest with symbol, prediction days, and optional user data
     * @return PredictionResponse with multi-day predictions
     * @throws PredictionFailedException if prediction fails
     */
    public PredictionResponse predictWithUserData(PredictionRequest request) {
        log.info("Starting prediction for {} with {} days forecast", 
                request.getSymbol(), request.getPredictionDays());
        
        try {
            // Step 1: Fetch historical data from Yahoo Finance
            Map<String, List<?>> historicalData = dataFetchService.fetchHistoricalPrices(
                    request.getSymbol(), DEFAULT_HISTORICAL_DAYS);
            
            // Step 2: Merge with user data if provided
            if (request.getUserData() != null) {
                historicalData = dataFetchService.mergeUserData(historicalData, request.getUserData());
            }
            
            // Step 3: Validate data quality
            dataFetchService.validateData(historicalData);
            
            // Step 4: Extract current price
            List<Double> prices = (List<Double>) historicalData.get("prices");
            Double currentPrice = prices.get(prices.size() - 1);
            
            // Step 5: Call Python ML service
            MLPredictionRequest mlRequest = MLPredictionRequest.builder()
                    .symbol(request.getSymbol())
                    .dates((List<String>) historicalData.get("dates"))
                    .prices(prices)
                    .predictionDays(request.getPredictionDays())
                    .modelType(DEFAULT_MODEL)
                    .build();
            
            MLPredictionResponse mlResponse = callMLService(mlRequest);
            
            // Step 6: Calculate confidence score
            Double confidence = calculateConfidence(prices, mlResponse.getConfidence());
            
            // Step 7: Persist predictions to database
            savePredictions(request.getSymbol(), currentPrice, mlResponse, confidence);
            
            // Step 8: Build response
            return buildPredictionResponse(request.getSymbol(), currentPrice, mlResponse, confidence);
            
        } catch (Exception e) {
            log.error("Prediction failed for {}: {}", request.getSymbol(), e.getMessage(), e);
            throw new PredictionFailedException("Failed to generate prediction: " + e.getMessage());
        }
    }
    
    /**
     * Calls the Python ML service to generate predictions.
     * 
     * @param request MLPredictionRequest with historical data
     * @return MLPredictionResponse with predictions
     */
    private MLPredictionResponse callMLService(MLPredictionRequest request) {
        log.info("Calling ML service for {} with {} data points", 
                request.getSymbol(), request.getPrices().size());
        
        try {
            MLPredictionResponse response = mlServiceWebClient.post()
                    .uri("/api/predict")
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(MLPredictionResponse.class)
                    .block();
            
            if (response == null) {
                throw new PredictionFailedException("ML service returned null response");
            }
            
            log.info("ML service returned {} predictions", response.getPredictedPrices().size());
            return response;
            
        } catch (Exception e) {
            log.error("ML service call failed: {}", e.getMessage());
            throw new PredictionFailedException("ML service unavailable: " + e.getMessage());
        }
    }
    
    /**
     * Calculates confidence score based on historical volatility and model confidence.
     * 
     * @param prices Historical prices
     * @param modelConfidence Confidence from ML model
     * @return Confidence score (0-100)
     */
    private Double calculateConfidence(List<Double> prices, Double modelConfidence) {
        // Calculate volatility (standard deviation / mean)
        double mean = prices.stream().mapToDouble(Double::doubleValue).average().orElse(0);
        double variance = prices.stream()
                .mapToDouble(p -> Math.pow(p - mean, 2))
                .average()
                .orElse(0);
        double stdDev = Math.sqrt(variance);
        double volatilityRatio = stdDev / mean;
        
        // Lower volatility = higher confidence
        double volatilityConfidence = 100 - (volatilityRatio * 100);
        
        // Combine with model confidence (weighted average)
        double combinedConfidence = (volatilityConfidence * 0.4) + (modelConfidence * 0.6);
        
        // Clamp between 50 and 95
        return Math.max(50.0, Math.min(95.0, combinedConfidence));
    }
    
    /**
     * Saves predictions to database for historical tracking.
     * 
     * @param symbol Stock symbol
     * @param currentPrice Current stock price
     * @param mlResponse ML service response
     * @param confidence Calculated confidence score
     */
    private void savePredictions(String symbol, Double currentPrice, 
                                 MLPredictionResponse mlResponse, Double confidence) {
        List<StockPrediction> predictions = new ArrayList<>();
        
        for (int i = 0; i < mlResponse.getPredictedPrices().size(); i++) {
            StockPrediction prediction = StockPrediction.builder()
                    .symbol(symbol)
                    .currentPrice(currentPrice)
                    .predictedPrice(mlResponse.getPredictedPrices().get(i))
                    .predictionDay(i + 1)
                    .lowerBound(mlResponse.getLowerBounds().get(i))
                    .upperBound(mlResponse.getUpperBounds().get(i))
                    .confidence(confidence)
                    .modelUsed(mlResponse.getModelUsed())
                    .trainingDataPoints(mlResponse.getTrainingDataPoints())
                    .build();
            
            predictions.add(prediction);
        }
        
        predictionRepository.saveAll(predictions);
        log.info("Saved {} predictions to database", predictions.size());
    }
    
    /**
     * Builds the final prediction response DTO.
     * 
     * @param symbol Stock symbol
     * @param currentPrice Current stock price
     * @param mlResponse ML service response
     * @param confidence Calculated confidence score
     * @return PredictionResponse
     */
    private PredictionResponse buildPredictionResponse(String symbol, Double currentPrice,
                                                       MLPredictionResponse mlResponse, Double confidence) {
        List<PredictionResponse.DailyPrediction> dailyPredictions = new ArrayList<>();
        
        for (int i = 0; i < mlResponse.getPredictedPrices().size(); i++) {
            Double predictedPrice = mlResponse.getPredictedPrices().get(i);
            Double changePercent = ((predictedPrice - currentPrice) / currentPrice) * 100;
            
            PredictionResponse.DailyPrediction daily = PredictionResponse.DailyPrediction.builder()
                    .date(mlResponse.getPredictionDates().get(i))
                    .predictedPrice(predictedPrice)
                    .lowerBound(mlResponse.getLowerBounds().get(i))
                    .upperBound(mlResponse.getUpperBounds().get(i))
                    .changePercent(changePercent)
                    .build();
            
            dailyPredictions.add(daily);
        }
        
        return PredictionResponse.builder()
                .symbol(symbol)
                .currentPrice(currentPrice)
                .predictions(dailyPredictions)
                .confidence(confidence)
                .timeframe(mlResponse.getPredictedPrices().size() + " days")
                .timestamp(System.currentTimeMillis())
                .build();
    }
}