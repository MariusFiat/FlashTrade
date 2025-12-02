package com.example.trading_service.service;

import com.example.trading_service.dto.OrderRequest;
import com.example.trading_service.dto.OrderResponse;
import com.example.trading_service.entities.Order;
import com.example.trading_service.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {
    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public OrderResponse placeOrder(OrderRequest orderRequest) {
        Order order = new Order();
        order.setSymbol(orderRequest.getSymbol());
        order.setQuantity(orderRequest.getQuantity());
        order.setPrice(orderRequest.getPrice());
        order.setType(orderRequest.getOrderType());

        // TODO: I will need to check the wallet and update the balance accordingly. Also I would have to send it to a matching engine

        orderRepository.save(order);

        return new OrderResponse(
                order.getId(),
                order.getSymbol(),
                order.getQuantity(),
                order.getPrice(),
                order.getType(),
                order.getCreatedAt()
        );
    }

    public List<OrderResponse> getAll() {
        return orderRepository.findAll()
                .stream()
                .map(order -> new OrderResponse(
                        order.getId(),
                        order.getSymbol(),
                        order.getQuantity(),
                        order.getPrice(),
                        order.getType(),
                        order.getCreatedAt()
                ))
                .toList();
    }

    public OrderResponse getById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found" + id));
        return new OrderResponse(
                order.getId(),
                order.getSymbol(),
                order.getQuantity(),
                order.getPrice(),
                order.getType(),
                order.getCreatedAt()
        );
    }

    public List<OrderResponse> getBySymbol(String symbol) {
        return orderRepository.findBySymbolOrderByCreatedAtDesc(symbol)
                .stream()
                .map(order -> new OrderResponse(
                        order.getId(),
                        order.getSymbol(),
                        order.getQuantity(),
                        order.getPrice(),
                        order.getType(),
                        order.getCreatedAt()
                ))
                .toList();
    }
}
