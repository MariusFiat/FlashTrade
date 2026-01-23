package com.example.trading_service.messaging;

import com.example.trading_service.config.RabbitMQConfig;
import com.example.trading_service.entities.trade.Trade;
import com.example.trading_service.messaging.dto.TradeHistoryRequest;
import com.example.trading_service.messaging.dto.TradeHistoryResponse;
import com.example.trading_service.messaging.dto.TradeRecord;
import com.example.trading_service.repository.TradeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class TradeHistoryListener {
    private static final Logger log = LoggerFactory.getLogger(TradeHistoryListener.class);
    private final TradeRepository tradeRepository;

    public TradeHistoryListener(TradeRepository tradeRepository) {
        this.tradeRepository = tradeRepository;
    }

    @RabbitListener(
            queues = RabbitMQConfig.TRADE_HISTORY_REQUEST_QUEUE,
            containerFactory = "singleListenerFactory" // <--- Use the non-batch factory
    )
    @SendTo // Automatically sends the return value to the reply-to queue
    public TradeHistoryResponse handleTradeHistoryRequest(TradeHistoryRequest request) {
        log.info("Received trade history request for userId={}", request.getUserId());

        List<Trade> trades = tradeRepository.findByBuyerIdOrSellerId(request.getUserId(), request.getUserId());

        List<TradeRecord> records = trades.stream()
                .map(trade -> {
                    String side = trade.getBuyerId().equals(request.getUserId()) ? "BUY" : "SELL";
                    return new TradeRecord(
                            trade.getId(),
                            trade.getSymbol(),
                            trade.getQuantity(),
                            trade.getPrice(),
                            side,
                            trade.getExecutedAt()
                    );
                })
                .collect(Collectors.toList());

        log.info("Returning {} trades for userId={}", records.size(), request.getUserId());

        return new TradeHistoryResponse(request.getUserId(), records);
    }
}
