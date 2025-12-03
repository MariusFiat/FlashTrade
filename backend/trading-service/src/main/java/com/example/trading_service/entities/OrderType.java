package com.example.trading_service.entities;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

public enum OrderType {
    BUY, SELL;
}