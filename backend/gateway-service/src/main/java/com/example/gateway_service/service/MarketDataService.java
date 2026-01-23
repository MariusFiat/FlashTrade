package com.example.gateway_service.service;

import com.example.gateway_service.messaging.RabbitMQConfig;
import com.example.gateway_service.messaging.dto.MarketDataRequest;
import com.example.gateway_service.messaging.dto.MarketDataResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class MarketDataService {
    private static final Logger log = LoggerFactory.getLogger(MarketDataService.class);
    private static final long REPLY_TIMEOUT_MS = 15000; // 15 seconds
    private final RabbitTemplate rabbitTemplate;

    public MarketDataService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
        this.rabbitTemplate.setReplyTimeout(REPLY_TIMEOUT_MS);
    }

    public MarketDataResponse getAllStocks() {
        String correlationId = UUID.randomUUID().toString();
        MarketDataRequest request = new MarketDataRequest(correlationId);

        log.info("Requesting market data, correlationId={}", correlationId);

        MarketDataResponse response = rabbitTemplate.convertSendAndReceiveAsType(
                RabbitMQConfig.TRADING_COMMANDS_EXCHANGE,
                RabbitMQConfig.MARKET_DATA_KEY,
                request,
                new ParameterizedTypeReference<MarketDataResponse>() {}
        );

        if (response == null) {
            log.error("Timeout or null response for market data request: {}", correlationId);
            throw new RuntimeException("Failed to fetch market data (timeout)");
        }

        return response;
    }
}
