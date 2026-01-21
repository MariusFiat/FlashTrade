package com.example.trading_service.engine;

import com.example.trading_service.entities.order.Order;
import com.example.trading_service.entities.order.OrderSide;
import com.example.trading_service.entities.order.OrderStatus;
import com.example.trading_service.entities.trade.Trade;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class MatchingEngine {
    public List<Trade> matchMarket(Order incoming, OrderBook book, BigDecimal currentMarketPrice) {
        List<Trade> trades = new ArrayList<>();

        log.info("matchMarket(): incoming={}, book={}", incoming, book);

        while (incoming.remainingQty() > 0) {

            Order matchedOrder;

            if(incoming.getSide() == OrderSide.BUY){
                matchedOrder = book.bestAsk();
            }else {
                matchedOrder = book.bestBid();
            }

            System.out.println(matchedOrder);

            if (matchedOrder == null) break;

            // Prevent Self-Trading
            if (matchedOrder.getUserId().equals(incoming.getUserId())) {
                break;
            }

            int qty = Math.min(incoming.remainingQty(), matchedOrder.remainingQty());

            System.out.println(qty);

            incoming.setFilledQty(incoming.getFilledQty() + qty);
            matchedOrder.setFilledQty(matchedOrder.getFilledQty() + qty);

            log.info("matchMarket(): matchedOrder={}", matchedOrder);
            System.out.println(matchedOrder.getFilledQty());

            Trade trade = new Trade();
            trade.setSymbol(incoming.getSymbol());
            trade.setQuantity(qty);
            trade.setPrice(currentMarketPrice);
            trade.setExecutedAt(Instant.now());

            if (incoming.getSide() == OrderSide.BUY) {
                trade.setBuyOrderId(incoming.getId());
                trade.setSellOrderId(matchedOrder.getId());
                trade.setBuyerId(incoming.getUserId());
                trade.setSellerId(matchedOrder.getUserId());
            } else {
                trade.setBuyOrderId(matchedOrder.getId());
                trade.setSellOrderId(incoming.getId());
                trade.setBuyerId(matchedOrder.getUserId());
                trade.setSellerId(incoming.getUserId());
            }

            trades.add(trade);

            log.warn("TRADE UPDATED: {}", trade);

            System.out.println("remainingQty: " + matchedOrder.remainingQty());

            if (matchedOrder.remainingQty() == 0) {
                book.remove(matchedOrder);
                matchedOrder.setStatus(OrderStatus.FILLED);
            } else {
                matchedOrder.setStatus(OrderStatus.PARTIALLY_FILLED);
            }

            System.out.println(matchedOrder);
        }

        return trades;
    }
}
