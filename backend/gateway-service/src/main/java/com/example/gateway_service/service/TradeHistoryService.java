package com.example.gateway_service.service;

import com.example.gateway_service.messaging.RabbitMQConfig;
import com.example.gateway_service.messaging.dto.TradeHistoryRequest;
import com.example.gateway_service.messaging.dto.TradeHistoryResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TradeHistoryService {
    private static final Logger log = LoggerFactory.getLogger(TradeHistoryService.class);
    private final RabbitTemplate rabbitTemplate;

    public TradeHistoryService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
        // Set a timeout for RPC calls (e.g., 5 seconds)
        this.rabbitTemplate.setReplyTimeout(5000);
    }

    public TradeHistoryResponse getUserTradeHistory(String userId) {
        String correlationId = UUID.randomUUID().toString();
        TradeHistoryRequest request = new TradeHistoryRequest(userId, correlationId);

        log.info("Requesting trade history for userId={}, correlationId={}", userId, correlationId);

        // Perform RPC call
        TradeHistoryResponse response = rabbitTemplate.convertSendAndReceiveAsType(
                RabbitMQConfig.TRADING_COMMANDS_EXCHANGE,
                RabbitMQConfig.TRADE_HISTORY_KEY,
                request,
                new ParameterizedTypeReference<TradeHistoryResponse>() {}
        );

        if (response == null) {
            log.error("Timeout or null response for trade history request: {}", correlationId);
            throw new RuntimeException("Failed to fetch trade history (timeout)");
        }

        return response;
    }
}
