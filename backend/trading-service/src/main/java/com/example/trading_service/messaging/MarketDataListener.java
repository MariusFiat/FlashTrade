package com.example.trading_service.messaging;

import com.example.trading_service.config.RabbitMQConfig;
import com.example.trading_service.entities.market.Stock;
import com.example.trading_service.entities.market.StockHistory;
import com.example.trading_service.messaging.dto.MarketDataRequest;
import com.example.trading_service.messaging.dto.MarketDataResponse;
import com.example.trading_service.messaging.dto.StockDTO;
import com.example.trading_service.repository.StockRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class MarketDataListener {
    private static final Logger log = LoggerFactory.getLogger(MarketDataListener.class);
    private final StockRepository stockRepository;

    public MarketDataListener(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    @RabbitListener(
            queues = RabbitMQConfig.MARKET_DATA_REQUEST_QUEUE,
            containerFactory = "singleListenerFactory"
    )
    @SendTo
    @Transactional(readOnly = true) // <--- Added Transactional annotation
    public MarketDataResponse handleMarketDataRequest(MarketDataRequest request) {
        log.info("Received market data request, correlationId={}", request.getCorrelationId());

        List<Stock> stocks = stockRepository.findAll();

        List<StockDTO> stockDTOs = stocks.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        log.info("Returning {} stocks", stockDTOs.size());

        return new MarketDataResponse(stockDTOs);
    }

    private StockDTO convertToDTO(Stock stock) {
        double change = 0.0;
        
        // Calculate change based on history if available
        if (stock.getHistory() != null && !stock.getHistory().isEmpty()) {
            // Sort history by timestamp descending to get the latest entries
            List<StockHistory> sortedHistory = stock.getHistory().stream()
                    .sorted(Comparator.comparing(StockHistory::getTimestamp).reversed())
                    .collect(Collectors.toList());
            
            // If we have at least one history point, compare current price with it
            // Or if we want 24h change, we'd need to find the entry closest to 24h ago
            // For simplicity, let's compare with the most recent history point (assuming it represents "previous close" or similar)
            if (!sortedHistory.isEmpty()) {
                double previousPrice = sortedHistory.get(0).getPrice();
                if (previousPrice > 0) {
                    change = ((stock.getPrice() - previousPrice) / previousPrice) * 100;
                }
            }
        }

        return new StockDTO(
                stock.getSymbol(),
                stock.getName(),
                stock.getPrice(),
                change
        );
    }
}
