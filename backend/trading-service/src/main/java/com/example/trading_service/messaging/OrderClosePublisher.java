package com.example.trading_service.messaging;

import com.example.trading_service.config.RabbitMQConfig;
import com.example.trading_service.messaging.dto.BuyOrderCloseRequest;
import com.example.trading_service.messaging.dto.SellOrderCloseRequest;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class OrderClosePublisher {
    private final RabbitTemplate rabbitTemplate;

    public OrderClosePublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishBuyClose(BuyOrderCloseRequest request) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.BUY_ORDER_CLOSE_QUEUE,
                request
        );
    }

    public void publishSellClose(SellOrderCloseRequest request) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.SELL_ORDER_CLOSE_QUEUE,
                request
        );
    }
}
