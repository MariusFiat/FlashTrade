package com.example.trading_service.engine;

import com.example.trading_service.entities.order.Order;
import com.example.trading_service.entities.order.OrderSide;
import com.example.trading_service.entities.order.OrderStatus;
import com.example.trading_service.entities.trade.Trade;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class MatchingEngine {
    public List<Trade> matchMarket(Order incoming, OrderBook book) {

        List<Trade> trades = new ArrayList<>();

        while (incoming.remainingQty() > 0) {
            Order matchedOrder;

            if(incoming.getSide() == OrderSide.BUY){
                matchedOrder = book.bestAsk();
            }else {
                matchedOrder = book.bestBid();
            }

            if (matchedOrder == null) break;

            int qty = Math.min(incoming.remainingQty(), matchedOrder.remainingQty());

            incoming.setFilledQty(incoming.getFilledQty() + qty);
            matchedOrder.setFilledQty(matchedOrder.getFilledQty() + qty);

            Trade trade = new Trade();
            trade.setSymbol(incoming.getSymbol());
            trade.setQuantity(qty);
            trade.setPrice(matchedOrder.getPrice());
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

            if (matchedOrder.remainingQty() == 0) {
                book.remove(matchedOrder);
                matchedOrder.setStatus(OrderStatus.FILLED);
            } else {
                matchedOrder.setStatus(OrderStatus.PARTIALLY_FILLED);
            }
        }

        return trades;
    }
}
