package com.example.trading_service.service;

import org.springframework.stereotype.Service;

import com.example.trading_service.dto.OrderResponse;
import com.example.trading_service.entities.Order;
import com.example.trading_service.messaging.dto.PlaceOrderCommand;
import com.example.trading_service.repository.OrderRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OrderService {
    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public OrderResponse placeOrderFromCommand(PlaceOrderCommand command) {
        log.info("Processing order from command: symbol={}, quantity={}, price={}",
                command.getSymbol(), command.getQuantity(), command.getPrice());

        Order order = new Order();
        order.setSymbol(command.getSymbol());
        order.setQuantity(command.getQuantity());
        order.setPrice(command.getPrice());
        order.setType(command.getOrderType());

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
}
