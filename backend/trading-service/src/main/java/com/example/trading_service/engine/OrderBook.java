package com.example.trading_service.engine;

import com.example.trading_service.entities.order.Order;
import com.example.trading_service.entities.order.OrderSide;

import java.util.PriorityQueue;

public class OrderBook {
    private final PriorityQueue<Order> bids = new PriorityQueue<>(PriceTimeComparator.buy());
    private final PriorityQueue<Order> asks = new PriorityQueue<>(PriceTimeComparator.sell());

    public void add(Order order) {
        if (order.getSide() == OrderSide.BUY) {
            bids.add(order);
        } else {
            asks.add(order);
        }
    }

    public Order bestBid() {
        return bids.peek();
    }

    public Order bestAsk() {
        return asks.peek();
    }

    public void remove(Order order) {
        bids.remove(order);
        asks.remove(order);
    }

    @Override
    public String toString() {
        return "OrderBook{" +
                "bids(size=" + bids.size() +
                ", bestBid=" + bids.peek() +
                "), asks(size=" + asks.size() +
                ", bestAsk=" + asks.peek() +
                ")" +
                '}';
    }
}
