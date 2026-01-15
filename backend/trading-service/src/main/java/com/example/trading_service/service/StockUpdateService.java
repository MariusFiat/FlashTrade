package com.example.trading_service.service;

import com.example.trading_service.entities.market.Stock;
import com.example.trading_service.repository.StockRepository;
import com.example.trading_service.messaging.dto.StockMessage;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
@EnableScheduling
public class StockUpdateService {

    private final StockRepository stockRepository;
    private final RabbitTemplate rabbitTemplate;
    private final WebClient webClient;

    @Value("${finnhub.api.key}")
    private String apiKey;

    public static final String STOCK_EXCHANGE = "trading_data_exchange";

    @Autowired
    public StockUpdateService(StockRepository stockRepository,
                              RabbitTemplate rabbitTemplate,
                              WebClient finnhubWebClient) {
        this.stockRepository = stockRepository;
        this.rabbitTemplate = rabbitTemplate;
        this.webClient = finnhubWebClient;
    }

    @Scheduled(fixedRate = 30000)
    @Transactional
    public void broadcastMarketPrices() {
        List<Stock> activeStocks = stockRepository.findAll().stream()
                .filter(Stock::isIs_active)
                .toList();

        for (Stock stock : activeStocks) {
            try {
                Double realPrice = fetchRealPrice(stock.getSymbol());

                if (realPrice != null && realPrice > 0 && realPrice != stock.getPrice()) {
                    stock.setPrice(realPrice);
                    stockRepository.save(stock);

                    StockMessage message = new StockMessage();
                    message.setSymbol(stock.getSymbol());
                    message.setPrice(realPrice);
                    message.setCompanyName(stock.getSymbol());

                    String routingKey = "stock.info." + stock.getSymbol().toLowerCase();
                    rabbitTemplate.convertAndSend(STOCK_EXCHANGE, routingKey, message);

                    System.out.println("Market Update (Finnhub) | " + stock.getSymbol() + ": " + realPrice);
                }
                else{
                    System.out.println("Market price is the same for  " + stock.getSymbol() + ": " + realPrice);
                }
            } catch (Exception e) {
                System.err.println("Error at stock update stage for: " + stock.getSymbol() + ": " + e.getMessage());
            }
        }
    }

    private Double fetchRealPrice(String symbol) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/quote")
                        .queryParam("symbol", symbol)
                        .queryParam("token", apiKey)
                        .build())
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(json -> json.get("c").asDouble())
                .block();
    }
}