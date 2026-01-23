package com.example.ai_service.service;

import com.example.ai_service.dto.NewsArticle;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class DataFetchService {
    private static final Logger log = LoggerFactory.getLogger(DataFetchService.class);
    
    @Value("${news.api.key:demo}")
    private String newsApiKey;
    
    @Value("${alpha.vantage.api.key:demo}")
    private String alphaVantageKey;
    
    private final WebClient webClient;
    private final Random random = new Random();
    
    public DataFetchService() {
        this.webClient = WebClient.builder().build();
    }
    
    /**
     * Fetches historical stock prices
     * For demo purposes, generates mock data
     * In production, use Yahoo Finance API or Alpha Vantage
     */
    public List<Double> getHistoricalPrices(String symbol, int days) {
        log.info("Fetching historical prices for {} ({} days)", symbol, days);
        
        // TODO: Replace with real API call
        // For now, generate mock data
        List<Double> prices = new ArrayList<>();
        double basePrice = 150.0 + (random.nextDouble() * 50); // Random base price
        
        for (int i = 0; i < days; i++) {
            double change = (random.nextDouble() - 0.5) * 5; // Random daily change
            basePrice += change;
            prices.add(Math.max(10.0, basePrice)); // Ensure price stays positive
        }
        
        return prices;
    }
    
    /**
     * Fetches news articles for a stock symbol
     */
    public List<NewsArticle> fetchNews(String symbol, int maxArticles) {
        log.info("Fetching news for symbol: {}", symbol);
        
        if ("demo".equals(newsApiKey)) {
            return generateMockNews(symbol, maxArticles);
        }
        
        try {
            String url = String.format(
                "https://newsapi.org/v2/everything?q=%s&apiKey=%s&pageSize=%d&language=en",
                symbol, newsApiKey, maxArticles
            );
            
            String response = webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(String.class)
                .block();
            
            return parseNewsResponse(response);
            
        } catch (Exception e) {
            log.error("Failed to fetch news for {}", symbol, e);
            return generateMockNews(symbol, maxArticles);
        }
    }
    
    private List<NewsArticle> parseNewsResponse(String jsonResponse) {
        List<NewsArticle> articles = new ArrayList<>();
        
        try {
            JsonObject root = JsonParser.parseString(jsonResponse).getAsJsonObject();
            JsonArray articlesArray = root.getAsJsonArray("articles");
            
            for (JsonElement element : articlesArray) {
                JsonObject article = element.getAsJsonObject();
                
                articles.add(NewsArticle.builder()
                    .title(getStringOrDefault(article, "title", "No title"))
                    .content(getStringOrDefault(article, "description", "No content"))
                    .source(getStringOrDefault(article.getAsJsonObject("source"), "name", "Unknown"))
                    .url(getStringOrDefault(article, "url", ""))
                    .publishedAt(Instant.now().toEpochMilli())
                    .build());
            }
        } catch (Exception e) {
            log.error("Failed to parse news response", e);
        }
        
        return articles;
    }
    
    private String getStringOrDefault(JsonObject obj, String key, String defaultValue) {
        try {
            return obj.has(key) && !obj.get(key).isJsonNull() 
                ? obj.get(key).getAsString() 
                : defaultValue;
        } catch (Exception e) {
            return defaultValue;
        }
    }
    
    /**
     * Generates mock news for testing
     */
    private List<NewsArticle> generateMockNews(String symbol, int count) {
        List<NewsArticle> articles = new ArrayList<>();
        String[] sentiments = {"positive", "negative", "neutral"};
        String[] templates = {
            "%s shows strong performance in Q4 earnings",
            "%s faces challenges in competitive market",
            "%s announces new product line expansion",
            "Analysts remain %s on %s stock outlook",
            "%s stock experiences volatility amid market uncertainty"
        };
        
        for (int i = 0; i < count; i++) {
            String sentiment = sentiments[random.nextInt(sentiments.length)];
            String template = templates[random.nextInt(templates.length)];
            String title = String.format(template, symbol, sentiment);
            
            articles.add(NewsArticle.builder()
                .title(title)
                .content(title + ". " + generateMockContent(sentiment))
                .source("Mock News Source")
                .url("https://example.com/news/" + i)
                .publishedAt(Instant.now().minusSeconds(i * 3600).toEpochMilli())
                .build());
        }
        
        return articles;
    }
    
    private String generateMockContent(String sentiment) {
        switch (sentiment) {
            case "positive":
                return "Market analysts are optimistic about future growth prospects and strong fundamentals.";
            case "negative":
                return "Concerns arise over declining market share and increased competition in the sector.";
            default:
                return "Market observers maintain a cautious stance pending further developments.";
        }
    }
}