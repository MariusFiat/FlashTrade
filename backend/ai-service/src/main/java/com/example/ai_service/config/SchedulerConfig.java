package com.example.ai_service.config;

import com.example.ai_service.service.PricePredictionService;
import com.example.ai_service.service.SentimentAnalysisService;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableScheduling
public class SchedulerConfig {

    private PricePredictionService predictionService;
    private SentimentAnalysisService sentimentService;

    public SchedulerConfig(
             PricePredictionService predictionService,
             SentimentAnalysisService sentimentService
    ) {
        this.predictionService = predictionService;
        this.sentimentService = sentimentService;
    }

    // Run predictions every day at 6 AM
    @Scheduled(cron = "0 0 6 * * *")
    public void generateDailyPredictions() {
        List<String> popularStocks = Arrays.asList("AAPL", "GOOGL", "MSFT", "TSLA");

        for (String symbol : popularStocks) {
            predictionService.predictPrice(symbol);
            sentimentService.analyzeSentiment(symbol);
        }
    }

    // Update sentiment every 4 hours
    @Scheduled(fixedRate = 14400000) // 4 hours in milliseconds
    public void updateSentiment() {
        // Fetch latest news and update sentiment
    }
}
