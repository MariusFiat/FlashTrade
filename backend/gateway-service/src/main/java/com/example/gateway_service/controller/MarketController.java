package com.example.gateway_service.controller;

import com.example.gateway_service.messaging.dto.MarketDataResponse;
import com.example.gateway_service.service.MarketDataService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/market")
public class MarketController {

    private final MarketDataService marketDataService;

    public MarketController(MarketDataService marketDataService) {
        this.marketDataService = marketDataService;
    }

    @GetMapping("/stocks")
    public ResponseEntity<MarketDataResponse> getAllStocks() {
        try {
            MarketDataResponse response = marketDataService.getAllStocks();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
