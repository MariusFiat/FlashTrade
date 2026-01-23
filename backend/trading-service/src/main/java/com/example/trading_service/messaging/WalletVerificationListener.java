package com.example.trading_service.messaging;

import com.example.trading_service.config.RabbitMQConfig;
import com.example.trading_service.messaging.dto.BuyOrderResponse;
import com.example.trading_service.messaging.dto.SellOrderResponse;
import com.example.trading_service.service.WalletVerificationCoordinator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class WalletVerificationListener {
    private static final Logger log = LoggerFactory.getLogger(WalletVerificationListener.class);
    private final WalletVerificationCoordinator coordinator;

    public WalletVerificationListener(WalletVerificationCoordinator coordinator) {
        this.coordinator = coordinator;
    }

    @RabbitListener(queues = RabbitMQConfig.ORDER_RESPONSE_QUEUE, containerFactory = "singleListenerFactory")
    public void onBuyResponse(BuyOrderResponse response) {
        log.info(
                "Wallet BUY response received: orderId={}, approved={}",
                response.getOrderId(),
                response.isApproved()
        );
        coordinator.complete(
                response.getOrderId(),
                response.isApproved(),
                response.getReason()
        );
    }

    @RabbitListener(queues = RabbitMQConfig.SELL_RESPONSE_QUEUE, containerFactory = "singleListenerFactory")
    public void onSellResponse(SellOrderResponse response) {
        log.info(
                "Wallet SELL response received: orderId={}, approved={}",
                response.getOrderId(),
                response.isApproved()
        );

        coordinator.complete(
                response.getOrderId(),
                response.isApproved(),
                response.getMessage()
        );
    }
}
