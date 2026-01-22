package com.example.ai_service.controller;

import com.example.ai_service.service.PricePredictionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ai/predictions")
public class PredictionController {

    private final PricePredictionService predictionService;

    public PredictionController(PricePredictionService predictionService) {
        this.predictionService = predictionService;
    }

    // price prediction for one stock at a time
    @GetMapping("/{symbol}")
    public ResponseEntity<PredictionResponse> getPrediction(@PathVariable String symbol) {

        PredictionResponse prediction = predictionService.predictPrice(symbol);
        return ResponseEntity.ok(prediction);
    }

    // price prediction for multiple stocks (user portfolio optimization)
    @PostMapping("/batch")
    public ResponseEntity<List<PredictionResponse>> batchPredict(@RequestBody List<String> symbols) {

        List<PredictionResponse> predictions = predictionService.predictMultiple(symbols);
        return ResponseEntity.ok(predictions);
    }
}