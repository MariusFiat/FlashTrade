package com.example.trading_service.controller;

import com.example.trading_service.dto.OrderRequest;
import com.example.trading_service.dto.OrderResponse;
import com.example.trading_service.service.OrderService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public OrderResponse placeOrder(@RequestBody OrderRequest orderRequest) {
        return orderService.placeOrder(orderRequest);
    }

    @GetMapping
    public List<OrderResponse> getAll() {
        return orderService.getAll();
    }

    @GetMapping("/{id}")
    public OrderResponse getById(@PathVariable Long id) {
        return orderService.getById(id);
    }

    @GetMapping("/symbol/{symbol}")
    public List<OrderResponse> getBySymbol(@PathVariable String symbol) {
        return orderService.getBySymbol(symbol);
    }

}
