package com.example.trading_service.messaging;

import com.example.trading_service.config.RabbitMQConfig;
import com.example.trading_service.dto.StockPerformanceDTO;
import com.example.trading_service.messaging.dto.StockPerformanceRequest;
import com.example.trading_service.messaging.dto.StockPerformanceResponse;
import com.example.trading_service.service.StockPerformanceService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StockPerformanceListener {

    @Autowired
    private StockPerformanceService stockPerformanceService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = RabbitMQConfig.STOCK_PERFORMANCE_REQUEST_QUEUE, containerFactory = "singleListenerFactory")
    public void handlePerformanceRequest(StockPerformanceRequest request) {
        try {
            StockPerformanceDTO performanceData = stockPerformanceService.getStockPerformance(
                    request.getSymbol(),
                    request.getRange()
            );

            StockPerformanceResponse response = new StockPerformanceResponse();
            response.setSymbol(performanceData.getSymbol());
            response.setHistory(performanceData.getHistory());
            response.setCurrentPrice(performanceData.getCurrentPrice());
            response.setCorrelationId(request.getCorrelationId());

            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.STOCK_EXCHANGE,
                    RabbitMQConfig.STOCK_PERFORMANCE_RESPONSE_KEY,
                    response
            );

        } catch (Exception e) {
            System.err.println("Error at performance request handling: " + e.getMessage());
        }
    }
}