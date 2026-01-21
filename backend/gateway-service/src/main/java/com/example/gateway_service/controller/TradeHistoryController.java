package com.example.gateway_service.controller;

import com.example.gateway_service.messaging.dto.TradeHistoryResponse;
import com.example.gateway_service.service.TradeHistoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/trades")
public class TradeHistoryController {

    private final TradeHistoryService tradeHistoryService;

    public TradeHistoryController(TradeHistoryService tradeHistoryService) {
        this.tradeHistoryService = tradeHistoryService;
    }

    @GetMapping("/history/{userId}")
    public ResponseEntity<TradeHistoryResponse> getTradeHistory(@PathVariable String userId) {
        try {
            TradeHistoryResponse history = tradeHistoryService.getUserTradeHistory(userId);
            return ResponseEntity.ok(history);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
