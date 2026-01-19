package com.example.trading_service.messaging;

import com.example.trading_service.config.RabbitMQConfig;
import com.example.trading_service.entities.order.OrderSide;
import com.example.trading_service.messaging.dto.WalletVerificationRequest;
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

    public void sendBuyOrderVerification(String userId, Long orderId, BigDecimal amount, String correlationId) {
        WalletVerificationRequest request = new WalletVerificationRequest(userId, orderId, OrderSide.BUY ,amount, correlationId, null, 0);
        rabbitTemplate.convertAndSend(RabbitMQConfig.BUY_ORDER_QUEUE, request);
        log.info("Sent buy order verification request: userId={}, amount={}", userId, amount);
    }

    public void sendSellOrderVerification(String userId, Long orderId, String symbol, int quantity, String correlationId) {
        WalletVerificationRequest request = new WalletVerificationRequest(userId, orderId, OrderSide.SELL ,null, correlationId, symbol, quantity);
        rabbitTemplate.convertAndSend(RabbitMQConfig.SELL_ORDER_QUEUE, request);
        log.info("Sent sell order verification request: userId={}, symbol={}, quantity={}", userId, symbol, quantity);
    }
}
