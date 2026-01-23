package com.example.trading_service.messaging;

import com.example.trading_service.config.RabbitMQConfig;
import com.example.trading_service.entities.order.OrderSide;
import com.example.trading_service.messaging.dto.BuyOrderRequest;
import com.example.trading_service.messaging.dto.SellOrderRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component
public class WalletVerificationPublisher {
    private static final Logger log = LoggerFactory.getLogger(WalletVerificationPublisher.class);
    private final RabbitTemplate rabbitTemplate;

    public WalletVerificationPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendBuyOrderVerification(
            Long orderId,
            String userId,
            String symbol,
            double quantity,
            double priceAtOrder
    ) {
        BuyOrderRequest request = new BuyOrderRequest(
                orderId.toString(),
                userId,
                symbol,
                quantity,
                priceAtOrder
        );
        rabbitTemplate.convertAndSend(RabbitMQConfig.BUY_ORDER_QUEUE, request);
        log.info("Sent BUY order verification request: orderId={}, userId={}, symbol={}, quantity={}", orderId, userId, symbol, quantity);
    }

    public void sendSellOrderVerification(
            Long orderId,
            String userId,
            String symbol,
            double quantity
    ) {
        SellOrderRequest request = new SellOrderRequest();
        request.setOrderId(orderId.toString());
        request.setUserId(userId);
        request.setSymbol(symbol);
        request.setQuantity(quantity);

        rabbitTemplate.convertAndSend(RabbitMQConfig.SELL_ORDER_QUEUE, request);
        log.info("Sent SELL order verification request: orderId={}, userId={}, symbol={}, quantity={}", orderId, userId, symbol, quantity);
    }
}
