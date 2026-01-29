package com.example.ai_service.service;

import com.example.ai_service.dto.UserStockData;
import com.example.ai_service.exception.DataFetchException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DataFetchService {
    
    private static final String YAHOO_FINANCE_URL = "https://query1.finance.yahoo.com/v8/finance/chart/";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    private final WebClient webClient;
    
    public DataFetchService() {
        this.webClient = WebClient.builder().build();
    }
    
    /**
     * Fetches historical stock prices from Yahoo Finance.
     * 
     * @param symbol Stock ticker symbol (e.g., "AAPL")
     * @param days Number of days of historical data to fetch
     * @return Map with "dates" and "prices" lists
     * @throws DataFetchException if data cannot be fetched
     */
    @SuppressWarnings("null")
    public Map<String, List<?>> fetchHistoricalPrices(String symbol, int days) {
        log.info("Fetching {} days of historical data for {}", days, symbol);
        
        try {
            long endTime = System.currentTimeMillis() / 1000;
            long startTime = endTime - (days * 24 * 60 * 60);
            
            String url = String.format("%s%s?period1=%d&period2=%d&interval=1d",
                    YAHOO_FINANCE_URL, symbol, startTime, endTime);
            
            Map<String, Object> response = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
            
            if (response == null || !response.containsKey("chart")) {
                throw new DataFetchException("Invalid response from Yahoo Finance");
            }
            
            Map<String, Object> chart = (Map<String, Object>) response.get("chart");
            List<Map<String, Object>> result = (List<Map<String, Object>>) chart.get("result");
            
            if (result == null || result.isEmpty()) {
                throw new DataFetchException("No data returned for symbol: " + symbol);
            }
            
            Map<String, Object> data = result.get(0);
            List<Long> timestamps = (List<Long>) ((Map<String, Object>) data.get("timestamp")).get("timestamp");
            Map<String, Object> indicators = (Map<String, Object>) data.get("indicators");
            List<Map<String, Object>> quote = (List<Map<String, Object>>) indicators.get("quote");
            List<Double> closePrices = (List<Double>) quote.get(0).get("close");
            
            List<String> dates = timestamps.stream()
                    .map(ts -> LocalDate.ofEpochDay(ts / 86400).format(DATE_FORMATTER))
                    .collect(Collectors.toList());
            
            // Remove null values
            List<String> cleanDates = new ArrayList<>();
            List<Double> cleanPrices = new ArrayList<>();
            
            for (int i = 0; i < dates.size(); i++) {
                if (closePrices.get(i) != null) {
                    cleanDates.add(dates.get(i));
                    cleanPrices.add(closePrices.get(i));
                }
            }
            
            log.info("Successfully fetched {} data points for {}", cleanDates.size(), symbol);
            
            Map<String, List<?>> results = new HashMap<>();
            results.put("dates", cleanDates);
            results.put("prices", cleanPrices);
            
            return results;
            
        } catch (Exception e) {
            log.error("Error fetching data for {}: {}", symbol, e.getMessage());
            throw new DataFetchException("Failed to fetch historical data: " + e.getMessage());
        }
    }
    
    /**
     * Merges Yahoo Finance data with user-provided data.
     * User data takes precedence for overlapping dates.
     * 
     * @param yahooData Data from Yahoo Finance
     * @param userData User-provided data
     * @return Merged and sorted data
     */
    public Map<String, List<?>> mergeUserData(Map<String, List<?>> yahooData, UserStockData userData) {
        if (userData == null || userData.getDates() == null || userData.getDates().isEmpty()) {
            return yahooData;
        }
        
        log.info("Merging user data: {} points", userData.getDates().size());
        
        userData.validate();
        
        // Create map for deduplication (user data takes precedence)
        Map<String, Double> priceMap = new TreeMap<>();
        
        // Add Yahoo data first
        List<String> yahooDates = (List<String>) yahooData.get("dates");
        List<Double> yahooPrices = (List<Double>) yahooData.get("prices");
        
        for (int i = 0; i < yahooDates.size(); i++) {
            priceMap.put(yahooDates.get(i), yahooPrices.get(i));
        }
        
        // Override with user data
        for (int i = 0; i < userData.getDates().size(); i++) {
            priceMap.put(userData.getDates().get(i), userData.getPrices().get(i));
        }
        
        // Convert back to lists
        List<String> mergedDates = new ArrayList<>(priceMap.keySet());
        List<Double> mergedPrices = new ArrayList<>(priceMap.values());
        
        log.info("Merged data: {} total points", mergedDates.size());
        
        Map<String, List<?>> result = new HashMap<>();
        result.put("dates", mergedDates);
        result.put("prices", mergedPrices);
        
        return result;
    }
    
    /**
     * Validates that data meets minimum requirements for prediction.
     * 
     * @param data Historical data to validate
     * @throws DataFetchException if data is insufficient
     */
    public void validateData(Map<String, List<?>> data) {
        List<String> dates = (List<String>) data.get("dates");
        List<Double> prices = (List<Double>) data.get("prices");
        
        if (dates == null || dates.isEmpty()) {
            throw new DataFetchException("No dates in historical data");
        }
        
        if (prices == null || prices.isEmpty()) {
            throw new DataFetchException("No prices in historical data");
        }
        
        if (dates.size() < 30) {
            throw new DataFetchException("Insufficient data: minimum 30 days required, got " + dates.size());
        }
        
        if (dates.size() != prices.size()) {
            throw new DataFetchException("Data mismatch: dates and prices have different lengths");
        }
        
        log.info("Data validation passed: {} data points", dates.size());
    }
}