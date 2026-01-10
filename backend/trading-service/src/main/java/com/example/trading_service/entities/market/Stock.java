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

}
