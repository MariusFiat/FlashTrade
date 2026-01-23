package com.example.trading_service.repository;

import com.example.trading_service.entities.order.Order;
import com.example.trading_service.entities.order.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findBySymbolOrderByCreatedAtDesc(String symbol);

    @Query("SELECT o FROM Order o WHERE o.userId = :userId AND o.status IN ('OPEN', 'PARTIALLY_FILLED', 'PENDING_WALLET')")
    List<Order> findActiveOrdersByUserId(@Param("userId") String userId);
}
