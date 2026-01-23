package com.example.ai_service.messaging;

import com.example.ai_service.dto.TradeEvent;
import com.example.ai_service.entity.SentimentScore;
import com.example.ai_service.service.SentimentAnalysisService;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class TradeEventListener {
    private static final Logger log = LoggerFactory.getLogger(TradeEventListener.class);
    private SentimentAnalysisService sentimentService;

    public TradeEventListener(SentimentAnalysisService sentimentService) {
        this.sentimentService = sentimentService;
    }

    @RabbitListener(queues = "ai.trade.events.queue")
    public void onTradeExecuted(TradeEvent event) {
        try {
            String symbol = event.getSymbol();
            log.info("Received trade event for symbol: {}", symbol);

            SentimentScore recentSentiment = sentimentService.getLatestSentiment(symbol);

            if (recentSentiment != null && 
                recentSentiment.getClassification().equals("POSITIVE") && 
                event.getPriceChange() != null &&
                event.getPriceChange().compareTo(BigDecimal.ZERO) > 0) {
                log.info("✓ Sentiment prediction was accurate for {}", symbol);
            } else if (recentSentiment != null) {
                log.info("Sentiment for {}: {}, Price change: {}", 
                    symbol, recentSentiment.getClassification(), event.getPriceChange());
            }
        } catch (Exception e) {
            log.error("Error processing trade event: {}", e.getMessage());
        }
    }
}
