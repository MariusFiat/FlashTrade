package com.example.gateway_service.controller;

import com.example.gateway_service.messaging.dto.StockPerformanceResponse;
import com.example.gateway_service.service.StockPerformanceGatewayService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/stocks")
public class StockPerformanceController {
    private static final Logger log = LoggerFactory.getLogger(StockPerformanceController.class);

    private final StockPerformanceGatewayService gatewayService;

    public StockPerformanceController(StockPerformanceGatewayService gatewayService) {
        this.gatewayService = gatewayService;
    }

    @GetMapping("/{symbol}/performance")
    public CompletableFuture<ResponseEntity<StockPerformanceResponse>> getPerformance(
            @PathVariable String symbol,
            @RequestParam(defaultValue = "1d") String range) {

        log.info("Requesting performance for symbol: {}, range: {}", symbol, range);

        return gatewayService.fetchStockPerformance(symbol, range)
                .thenApply(ResponseEntity::ok)
                .exceptionally(ex -> {
                    log.error("Timeout or error fetching performance for {}: {}", symbol, ex.getMessage());
                    return ResponseEntity.status(504).build(); // Gateway Timeout
                });
    }
}