package com.example.trading_service.repository;

import com.example.trading_service.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findBySymbolOrderByCreatedAtDesc(String symbol);
}
