package com.example.trading_service.engine;

import com.example.trading_service.entities.order.Order;

import java.util.Comparator;

public class PriceTimeComparator {
    public static Comparator<Order> buy(){
        return Comparator
                .comparing(Order::getPrice).reversed()
                .thenComparing(Order::getCreatedAt);
    }

    public static Comparator<Order> sell() {
        return Comparator
                .comparing(Order::getPrice)
                .thenComparing(Order::getCreatedAt);
    }
}
