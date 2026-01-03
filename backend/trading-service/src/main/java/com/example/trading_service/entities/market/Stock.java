package com.example.trading_service.entities.market;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity(name = "stocks")
public class Stock {
    @Setter
    @Getter
    @Id
    private String symbol;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false)
    private boolean is_active;

    public boolean isIs_active() {
        return is_active;
    }
    public void setIs_active(boolean is_active) {
        this.is_active = is_active;
    }
}
