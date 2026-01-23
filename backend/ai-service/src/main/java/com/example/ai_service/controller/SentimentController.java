package com.example.ai_service.controller;

import com.example.ai_service.dto.SentimentResponse;
import com.example.ai_service.entity.SentimentScore;
import com.example.ai_service.service.SentimentAnalysisService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ai/sentiment")
@CrossOrigin(origins = "*")
public class SentimentController {

    private final SentimentAnalysisService sentimentService;

    public SentimentController(SentimentAnalysisService sentimentService) {
        this.sentimentService = sentimentService;
    }

    @GetMapping("/{symbol}")
    public ResponseEntity<SentimentResponse> getSentiment(@PathVariable String symbol) {
        SentimentResponse sentiment = sentimentService.analyzeSentiment(symbol);
        return ResponseEntity.ok(sentiment);
    }

    @GetMapping("/{symbol}/history")
    public ResponseEntity<List<SentimentScore>> getSentimentHistory(
            @PathVariable String symbol,
            @RequestParam(defaultValue = "7") int days) {
        List<SentimentScore> history = sentimentService.getSentimentHistory(symbol, days);
        return ResponseEntity.ok(history);
    }
}