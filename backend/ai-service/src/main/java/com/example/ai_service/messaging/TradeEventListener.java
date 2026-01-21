package com.example.ai_service.messaging;

import com.example.ai_service.entity.SentimentScore;
import com.example.ai_service.service.SentimentAnalysisService;
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
        // When a trade happens, check if sentiment predicted it
        String symbol = event.getSymbol();

        SentimentScore recentSentiment = sentimentService.getLatestSentiment(symbol);

        if (recentSentiment.getClassification().equals("POSITIVE") && event.getPriceChange() > 0) {
            // Sentiment was correct! Log this for model improvement
            log.info("Sentiment prediction was accurate for {}", symbol);
        }
    }
}
