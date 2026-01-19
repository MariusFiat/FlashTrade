package com.example.gateway_service.service;

import com.example.gateway_service.messaging.dto.StockPerformanceRequest;
import com.example.gateway_service.messaging.dto.StockPerformanceResponse;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
public class StockPerformanceGatewayService {

    private final RabbitTemplate rabbitTemplate;
    private final ConcurrentHashMap<String, CompletableFuture<StockPerformanceResponse>> pendingRequests = new ConcurrentHashMap<>();

    public StockPerformanceGatewayService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public CompletableFuture<StockPerformanceResponse> fetchStockPerformance(String symbol, String range) {
        String correlationId = UUID.randomUUID().toString();
        StockPerformanceRequest request = new StockPerformanceRequest(symbol, range, correlationId);

        CompletableFuture<StockPerformanceResponse> future = new CompletableFuture<>();
        pendingRequests.put(correlationId, future);

        rabbitTemplate.convertAndSend("trading_data_exchange", "stock.performance.request", request);

        return future.orTimeout(10, TimeUnit.SECONDS)
                .whenComplete((res, ex) -> pendingRequests.remove(correlationId));
    }

    @RabbitListener(queues = "stock.performance.response.queue")
    public void onPerformanceResponse(StockPerformanceResponse response) {
        CompletableFuture<StockPerformanceResponse> future = pendingRequests.get(response.getCorrelationId());
        if (future != null) {
            future.complete(response);
        }
    }
}