package com.example.ai_service.service;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

@Service
public class PricePredictionService {

    private final DataFetchService dataFetchService;
    private final PredictionRepository predictionRepository;

    public PricePredictionService(DataFetchService dataFetchService, PredictionRepository predictionRepository) {
        this.dataFetchService = dataFetchService;
        this.predictionRepository = predictionRepository;
    }

    private ProcessBuilder pythonProcess; // To call Python scripts

    public PredictionResponse predictPrice(String symbol) {
        // STEP 1: Get historical price data
        List<Double> historicalPrices = dataFetchService.getHistoricalPrices(symbol, 60); // Last 60 days

        // STEP 2: Call Python ML model
        String prediction = callPythonModel(symbol, historicalPrices);

        // STEP 3: Parse result
        double predictedPrice = Double.parseDouble(prediction);

        // STEP 4: Calculate confidence (how sure the model is)
        double confidence = calculateConfidence(historicalPrices, predictedPrice);

        // STEP 5: Save prediction to database
        StockPrediction entity = new StockPrediction();
        entity.setSymbol(symbol);
        entity.setPredictedPrice(predictedPrice);
        entity.setConfidence(confidence);
        entity.setCreatedAt(Instant.now());
        predictionRepository.save(entity);

        // STEP 6: Return response
        return new PredictionResponse(symbol, predictedPrice, confidence);
    }

    private String callPythonModel(String symbol, List<Double> prices) {
        try {
            // This runs the Python script that contains the ML model
            ProcessBuilder pb = new ProcessBuilder(
                    "python3",
                    "src/main/python/price_prediction/predict.py",
                    symbol,
                    prices.toString()
            );

            Process process = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String result = reader.readLine(); // Read prediction from Python
            process.waitFor();

            return result;
        } catch (Exception e) {
            throw new PredictionFailedException("Failed to run prediction model", e);
        }
    }

    private double calculateConfidence(List<Double> historical, double predicted) {
        // Simple confidence calculation based on volatility
        // High volatility = low confidence
        double volatility = calculateVolatility(historical);
        return Math.max(0.5, 1.0 - volatility);
    }
}
