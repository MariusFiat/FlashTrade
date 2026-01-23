package com.example.gateway_service.service;

import com.example.gateway_service.messaging.RabbitMQConfig;
import com.example.gateway_service.messaging.dto.ActiveOrdersRequest;
import com.example.gateway_service.messaging.dto.ActiveOrdersResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ActiveOrdersService {
    private static final Logger log = LoggerFactory.getLogger(ActiveOrdersService.class);
    private static final long REPLY_TIMEOUT_MS = 15000; // 15 seconds
    private final RabbitTemplate rabbitTemplate;

    public ActiveOrdersService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
        // Set reply timeout for RPC calls
        this.rabbitTemplate.setReplyTimeout(REPLY_TIMEOUT_MS);
    }

    public ActiveOrdersResponse getUserActiveOrders(String userId) {
        String correlationId = UUID.randomUUID().toString();
        ActiveOrdersRequest request = new ActiveOrdersRequest(userId, correlationId);

        log.info("Requesting active orders for userId={}, correlationId={}", userId, correlationId);

        ActiveOrdersResponse response = rabbitTemplate.convertSendAndReceiveAsType(
                RabbitMQConfig.TRADING_COMMANDS_EXCHANGE,
                RabbitMQConfig.ACTIVE_ORDERS_KEY,
                request,
                new ParameterizedTypeReference<ActiveOrdersResponse>() {}
        );

        if (response == null) {
            log.error("Timeout or null response for active orders request: {}", correlationId);
            throw new RuntimeException("Failed to fetch active orders (timeout)");
        }

        return response;
    }
}
