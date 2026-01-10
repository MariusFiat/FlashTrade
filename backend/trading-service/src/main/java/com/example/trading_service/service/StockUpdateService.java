package com.example.trading_service.service;

import com.example.trading_service.entities.market.Stock;
import com.example.trading_service.repository.StockRepository;
import com.example.trading_service.messaging.dto.StockMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;

@Service
@EnableScheduling
public class StockUpdateService {

    private final StockRepository stockRepository;
    private final RabbitTemplate rabbitTemplate;
    private final Random random = new Random();

    public static final String STOCK_EXCHANGE = "trading_data_exchange";
    public static final String STOCK_DATA_QUEUE = "stock_info_queue";

    @Autowired
    public StockUpdateService(StockRepository stockRepository, RabbitTemplate rabbitTemplate) {
        this.stockRepository = stockRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Scheduled(fixedRate = 10000)
    @Transactional
    public void broadcastMarketPrices() {
        List<Stock> activeStocks = stockRepository.findAll().stream()
                .filter(Stock::isIs_active)
                .toList();

        for (Stock stock : activeStocks) {
            double currentPrice = stock.getPrice();
            double changePercent = 0.98 + (1.02 - 0.98) * random.nextDouble();
            double newPrice = Math.round((currentPrice * changePercent) * 100.0) / 100.0;

            stock.setPrice(newPrice);
            stockRepository.save(stock);

            StockMessage message = new StockMessage();
            message.setSymbol(stock.getSymbol());
            message.setPrice(newPrice);
            message.setCompanyName(stock.getSymbol());

            String routingKey = "stock.info." + stock.getSymbol().toLowerCase();

            rabbitTemplate.convertAndSend(STOCK_EXCHANGE, routingKey, message);

            System.out.println("Update: " + stock.getSymbol() + " new price: " + newPrice);
        }
    }
}