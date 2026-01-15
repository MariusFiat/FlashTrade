package com.example.trading_service.entities.market;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

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
