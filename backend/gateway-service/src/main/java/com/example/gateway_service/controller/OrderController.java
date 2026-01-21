package com.example.gateway_service.controller;

import java.util.Map;

import com.example.gateway_service.messaging.dto.ActiveOrdersResponse;
import com.example.gateway_service.service.ActiveOrdersService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.gateway_service.messaging.OrderCommandPublisher;
import com.example.gateway_service.messaging.dto.PlaceOrderCommand;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private static final Logger log = LoggerFactory.getLogger(OrderController.class);
    
    private final OrderCommandPublisher orderCommandPublisher;
    private final ActiveOrdersService activeOrdersService;

    public OrderController(OrderCommandPublisher orderCommandPublisher, ActiveOrdersService activeOrdersService) {
        this.orderCommandPublisher = orderCommandPublisher;
        this.activeOrdersService = activeOrdersService;
    }
    
    @PostMapping
    public ResponseEntity<Map<String, String>> placeOrder(@RequestBody PlaceOrderCommand command) {
        log.info("Received place order request: symbol={}, quantity={}, side={}",
                command.getSymbol(), command.getQuantity(), command.getSide());
        
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

    @GetMapping("/active/{userId}")
    public ResponseEntity<ActiveOrdersResponse> getActiveOrders(@PathVariable String userId) {
        try {
            ActiveOrdersResponse response = activeOrdersService.getUserActiveOrders(userId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching active orders", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
