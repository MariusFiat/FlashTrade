package com.example.trading_service.messaging;

import com.example.trading_service.config.RabbitMQConfig;
import com.example.trading_service.messaging.dto.BuyOrderCloseRequest;
import com.example.trading_service.messaging.dto.SellOrderCloseRequest;
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

    public void publishBuyClose(BuyOrderCloseRequest request){
        log.info("Publishing BUY close: orderId-{}, userId-{}, status-{}, amountSpent={}, qty={}, symbol={}",
                request.getOrderId(),
                request.getUserId(),
                request.getStatus(),
                request.getAmountSpent(),
                request.getQuantity(),
                request.getSymbol()
        );

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.BUY_ORDER_CLOSE_QUEUE,
                request
        );
    }

    public void publishSellClose(SellOrderCloseRequest request){
        log.info("Publishing SELL close: orderId-{}, userId-{}, status-{}, amountReceived={}, qty={}, symbol={}",
                request.getOrderId(),
                request.getUserId(),
                request.getStatus(),
                request.getAmountReceived(),
                request.getQuantity(),
                request.getSymbol()
        );

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.SELL_ORDER_CLOSE_QUEUE,
                request
        );
    }

}
