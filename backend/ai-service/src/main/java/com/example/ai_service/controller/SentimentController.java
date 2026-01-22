package com.example.ai_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai/sentiment")
public class SentimentController {

    private final SentimentAnalysisService sentimentService;

    public SentimentController(SentimentAnalysisService sentimentService) {
        this.sentimentService = sentimentService;
    }

    @GetMapping("/{symbol}")
    public ResponseEntity<SentimentResponse> getSentiment(@PathVariable String symbol) {
        // Gets current news + sentiment for a stock

        SentimentResponse sentiment = sentimentService.analyzeSentiment(symbol);
        return ResponseEntity.ok(sentiment);
    }

    @GetMapping("/{symbol}/history")
    public ResponseEntity<List<SentimentScore>> getSentimentHistory(
            @PathVariable String symbol,
            @RequestParam(defaultValue = "7") int days) {
        // Gets sentiment trend over time

        List<SentimentScore> history = sentimentService.getSentimentHistory(symbol, days);
        return ResponseEntity.ok(history);
    }
}