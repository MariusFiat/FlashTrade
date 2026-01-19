package com.example.trading_service.messaging;

import com.example.trading_service.config.RabbitMQConfig;
import com.example.trading_service.messaging.dto.TradeSettlementEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class TradeSettlementPublisher {
    private static final Logger log = LoggerFactory.getLogger(TradeSettlementPublisher.class);

    private final RabbitTemplate rabbitTemplate;

    public TradeSettlementPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publish(TradeSettlementEvent event) {
        log.info(
                "Publishing trade settlement: tradeId={}, buyerId={}, sellerId={}",
                event.getTradeId(),
                event.getBuyerId(),
                event.getSellerId()
        );

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.TRADE_SETTLEMENT_QUEUE,
                event
        );
    }
}
