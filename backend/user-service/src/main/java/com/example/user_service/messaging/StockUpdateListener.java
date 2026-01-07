package com.example.user_service.messaging;

import com.example.user_service.config.RabbitStockConfig; // Importă config-ul
import com.example.user_service.dto.StockMessage;
import com.example.user_service.entities.Portfolio;
import com.example.user_service.repository.PortfolioRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service; // Import necesar
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StockUpdateListener {

    private final PortfolioRepository portfolioRepository;

    @Autowired
    public StockUpdateListener(PortfolioRepository portfolioRepository) {
        this.portfolioRepository = portfolioRepository; // CORECTAT
    }

    @RabbitListener(queues = RabbitStockConfig.STOCK_DATA_QUEUE)
    @Transactional
    public void handleStockUpdate(StockMessage message) {
        System.out.println("Processing update for: " + message.getSymbol());

        List<Portfolio> itemsToUpdate = portfolioRepository.findByStockIgnoreCase(message.getSymbol());

        if (itemsToUpdate.isEmpty()) {
            System.out.println("No portfolios found for symbol: " + message.getSymbol());
            return;
        }

        for (Portfolio item : itemsToUpdate) {
            if (item.getShares() != null && message.getPrice() != null) {
                double newValue = item.getShares() * message.getPrice();
                item.setPortfolioValue(newValue);
            }
        }

        portfolioRepository.saveAll(itemsToUpdate);

        System.out.println("Market Update | Symbol: " + message.getSymbol() +
                " | New Price: " + message.getPrice() +
                " | Portfolios Updated: " + itemsToUpdate.size());
    }
}