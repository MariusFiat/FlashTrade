package com.example.ai_service.service;

import com.example.ai_service.dto.NewsArticle;
import com.example.ai_service.dto.SentimentResponse;
import com.example.ai_service.entity.SentimentScore;
import com.example.ai_service.repository.SentimentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
public class SentimentAnalysisService {
    private static final Logger log = LoggerFactory.getLogger(SentimentAnalysisService.class);
    
    private final DataFetchService dataFetchService;
    private final SentimentRepository sentimentRepository;
    
    @Value("${python.executable:python3}")
    private String pythonExecutable;
    
    @Value("${python.scripts.path:src/main/java/ML}")
    private String pythonScriptsPath;
    
    public SentimentAnalysisService(DataFetchService dataFetchService,
                                    SentimentRepository sentimentRepository) {
        this.dataFetchService = dataFetchService;
        this.sentimentRepository = sentimentRepository;
    }
    
    public SentimentResponse analyzeSentiment(String symbol) {
        log.info("Analyzing sentiment for symbol: {}", symbol);
        
        // STEP 1: Fetch latest news articles
        List<NewsArticle> articles = new ArrayList<>(); // dataFetchService.fetchNews(symbol, 10);
        
        if (articles.isEmpty()) {
            return createNeutralResponse(symbol);
        }
        
        // STEP 2: Analyze each article
        List<Double> sentimentScores = new ArrayList<>();
        for (NewsArticle article : articles) {
            double score = analyzeArticle(article.getContent());
            sentimentScores.add(score);
        }
        
        // STEP 3: Calculate average sentiment
        double avgSentiment = sentimentScores.stream()
            .mapToDouble(Double::doubleValue)
            .average()
            .orElse(0.0);
        
        // STEP 4: Classify sentiment
        String classification = classifySentiment(avgSentiment);
        
        // STEP 5: Save to database
        SentimentScore entity = new SentimentScore();
        entity.setSymbol(symbol);
        entity.setScore(avgSentiment);
        entity.setClassification(classification);
        entity.setArticleCount(articles.size());
        entity.setCreatedAt(Instant.now());
        sentimentRepository.save(entity);
        
        // STEP 6: Return response
        return SentimentResponse.builder()
            .symbol(symbol)
            .score(avgSentiment)
            .classification(classification)
            .articleCount(articles.size())
            .timestamp(Instant.now().toEpochMilli())
            .build();
    }
    
    public List<SentimentScore> getSentimentHistory(String symbol, int days) {
        return sentimentRepository.findTop10BySymbolOrderByCreatedAtDesc(symbol);
    }
    
    private double analyzeArticle(String text) {
        try {
            ProcessBuilder pb = new ProcessBuilder(
                pythonExecutable,
                pythonScriptsPath + "/sentiment_analysis/finbert_sentiment.py",
                text
            );
            
            pb.redirectErrorStream(true);
            Process process = pb.start();
            
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream())
            );
            
            String result = reader.readLine();
            int exitCode = process.waitFor();
            
            if (exitCode != 0 || result == null) {
                return calculateFallbackSentiment(text);
            }
            
            return Double.parseDouble(result.trim());
            
        } catch (Exception e) {
            log.error("Error analyzing article sentiment", e);
            return calculateFallbackSentiment(text);
        }
    }
    
    private double calculateFallbackSentiment(String text) {
        // Simple keyword-based sentiment
        String lowerText = text.toLowerCase();
        int positiveCount = 0;
        int negativeCount = 0;
        
        String[] positiveWords = {"growth", "profit", "strong", "increase", "gain", "success", "positive", "bullish"};
        String[] negativeWords = {"loss", "decline", "weak", "decrease", "fall", "failure", "negative", "bearish"};
        
        for (String word : positiveWords) {
            if (lowerText.contains(word)) positiveCount++;
        }
        for (String word : negativeWords) {
            if (lowerText.contains(word)) negativeCount++;
        }
        
        if (positiveCount == 0 && negativeCount == 0) return 0.0;
        return (double) (positiveCount - negativeCount) / (positiveCount + negativeCount);
    }

    public SentimentScore getLatestSentiment(String symbol) {
        return sentimentRepository.findTopBySymbolOrderByCreatedAtDesc(symbol);
    }
    
    private String classifySentiment(double score) {
        if (score > 0.2) return "POSITIVE";
        if (score < -0.2) return "NEGATIVE";
        return "NEUTRAL";
    }
    
    private SentimentResponse createNeutralResponse(String symbol) {
        return SentimentResponse.builder()
            .symbol(symbol)
            .score(0.0)
            .classification("NEUTRAL")
            .articleCount(0)
            .timestamp(Instant.now().toEpochMilli())
            .build();
    }
}