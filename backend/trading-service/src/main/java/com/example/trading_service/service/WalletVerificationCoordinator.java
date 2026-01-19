package com.example.trading_service.service;

import com.example.trading_service.messaging.dto.WalletVerificationResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeoutException;

@Service
public class WalletVerificationCoordinator {
    private static final Logger log = LoggerFactory.getLogger(WalletVerificationCoordinator.class);

    private final Map<String, CompletableFuture<WalletVerificationResponse>> pending =
            new ConcurrentHashMap<>();

    public CompletableFuture<WalletVerificationResponse> register(String correlationId) {
        CompletableFuture<WalletVerificationResponse> future = new CompletableFuture<>();
        pending.put(correlationId, future);
        return future;
    }

    public void complete(WalletVerificationResponse response) {
        CompletableFuture<WalletVerificationResponse> future =
                pending.remove(response.getCorrelationId());

        if (future != null) {
            log.info(
                    "Completing wallet future: correlationId={}",
                    response.getCorrelationId()
            );

            future.complete(response);
        }

        log.warn(
                "No pending wallet future for correlationId={}",
                response.getCorrelationId()
        );
    }

    public void timeout(String correlationId) {
        CompletableFuture<WalletVerificationResponse> future =
                pending.remove(correlationId);
        if (future != null) {
            future.completeExceptionally(
                    new TimeoutException("Wallet verification timeout")
            );
        }
    }
}
