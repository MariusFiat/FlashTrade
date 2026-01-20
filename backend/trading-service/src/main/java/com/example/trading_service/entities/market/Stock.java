package com.example.trading_service.entities.market;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "stocks")
@Data
public class Stock {
    @Setter
    @Getter
    @Id
    private String symbol;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false)
    private boolean is_active;

    @Column(nullable = false)
    private double price;

    @OneToMany(mappedBy = "stock", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StockHistory> history = new ArrayList<>();

    public boolean isIs_active() {
        return is_active;
    }
    public void setIs_active(boolean is_active) {
        this.is_active = is_active;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double newPrice) {
        this.price = newPrice;
    }
}
