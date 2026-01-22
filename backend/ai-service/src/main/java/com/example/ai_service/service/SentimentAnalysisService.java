package com.example.ai_service.service;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;

@Service
public class SentimentAnalysisService {

    private final NewsRepository newsRepository;
    private final SentimentRepository sentimentRepository;

    public SentimentAnalysisService(NewsRepository newsRepository, SentimentRepository sentimentRepository) {
        this.newsRepository = newsRepository;
        this.sentimentRepository = sentimentRepository;
    }

    public SentimentResponse analyzeSentiment(String symbol) {
        // STEP 1: Fetch latest news articles
        List<NewsArticle> articles = fetchNewsArticles(symbol);

        // STEP 2: Analyze each article with FinBERT
        List<Double> sentimentScores = new ArrayList<>();
        for (NewsArticle article : articles) {
            double score = analyzeArticle(article.getContent());
            sentimentScores.add(score);
        }

        // STEP 3: Calculate average sentiment
        double avgSentiment = sentimentScores.stream()
                .mapToDouble(score -> score.doubleValue())
                .average()
                .orElse(0.0);

        // STEP 4: Classify sentiment
        String classification;
        if (avgSentiment > 0.2) classification = "POSITIVE";
        else if (avgSentiment < -0.2) classification = "NEGATIVE";
        else classification = "NEUTRAL";

        // STEP 5: Save to database
        SentimentScore entity = new SentimentScore();
        entity.setSymbol(symbol);
        entity.setScore(avgSentiment);
        entity.setClassification(classification);
        entity.setArticleCount(articles.size());
        entity.setCreatedAt(Instant.now());
        sentimentRepository.save(entity);

        // STEP 6: Return response
        return new SentimentResponse(symbol, avgSentiment, classification, articles.size());
    }

    private List<NewsArticle> fetchNewsArticles(String symbol) {
        // Call NewsAPI or Alpha Vantage
        // Free tier: 100 requests/day
        String apiUrl = "https://newsapi.org/v2/everything?q=" + symbol + "&apiKey=YOUR_KEY";

        // Make HTTP request, parse JSON, return articles
        // (Implementation details omitted for brevity)
    }

    private double analyzeArticle(String text) {
        try {
            // Call Python script with FinBERT model
            ProcessBuilder pb = new ProcessBuilder(
                    "python3",
                    "src/main/python/sentiment_analysis/finbert_sentiment.py",
                    text
            );

            Process process = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String result = reader.readLine(); // Returns score between -1 and 1
            process.waitFor();

            return Double.parseDouble(result);
        } catch (Exception e) {
            return 0.0; // Neutral if analysis fails
        }
    }
}
