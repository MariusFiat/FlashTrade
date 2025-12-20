package com.example.gateway_service.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.gateway_service.messaging.OrderCommandPublisher;
import com.example.gateway_service.messaging.dto.PlaceOrderCommand;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Slf4j
public class OrderController {
    
    private final OrderCommandPublisher orderCommandPublisher;
    
    @PostMapping
    public ResponseEntity<Map<String, String>> placeOrder(@RequestBody PlaceOrderCommand command) {
        log.info("Received place order request: symbol={}, quantity={}", 
                command.getSymbol(), command.getQuantity());
        
        // Publish command to trading service via RabbitMQ
        String correlationId = orderCommandPublisher.publishPlaceOrderCommand(command);
        
        // Return correlation ID to track the request
        return ResponseEntity.accepted()
                .body(Map.of(
                        "message", "Order command sent to trading service",
                        "correlationId", correlationId
                ));
    }
    
    @DeleteMapping("/{orderId}")
    public ResponseEntity<Map<String, String>> cancelOrder(
            @PathVariable Long orderId,
            @RequestParam String userId) {
        log.info("Received cancel order request: orderId={}, userId={}", orderId, userId);
        
        String correlationId = orderCommandPublisher.publishCancelOrderCommand(orderId, userId);
        
        return ResponseEntity.accepted()
                .body(Map.of(
                        "message", "Cancel order command sent",
                        "correlationId", correlationId
                ));
    }
}
