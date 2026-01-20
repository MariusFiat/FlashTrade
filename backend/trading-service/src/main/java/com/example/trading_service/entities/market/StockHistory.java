package com.example.trading_service.entities.market;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "stock_history")
@Data
@NoArgsConstructor
public class StockHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private double price;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_symbol", nullable = false)
    private Stock stock;

    public StockHistory(double price, LocalDateTime timestamp, Stock stock) {
        this.price = price;
        this.timestamp = timestamp;
        this.stock = stock;
    }
}