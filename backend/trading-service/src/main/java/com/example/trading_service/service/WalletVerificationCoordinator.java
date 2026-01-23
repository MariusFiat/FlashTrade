package com.example.trading_service.service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeoutException;

@Service
public class WalletVerificationCoordinator {
    private static final Logger log = LoggerFactory.getLogger(WalletVerificationCoordinator.class);

    private final Map<String, CompletableFuture<Boolean>> pending =
            new ConcurrentHashMap<>();

    public CompletableFuture<Boolean> register(String orderId) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        pending.put(orderId, future);
        return future;
    }

    public void complete(String orderId, boolean approved, String message) {
        CompletableFuture<Boolean> future =
                pending.remove(orderId);

        if (future != null) {
            log.info(
                    "Completing wallet future: orderId={}",
                   orderId
            );

            future.complete(approved);
        }

        log.warn(
                "No pending wallet future for orderId={}",
                orderId
        );
    }

    public void timeout(String correlationId) {
        CompletableFuture<Boolean> future =
                pending.remove(correlationId);
        if (future != null) {
            future.completeExceptionally(
                    new TimeoutException("Wallet verification timeout")
            );
        }
    }

    public void cancel(String orderId) {
        CompletableFuture<Boolean> future = pending.remove(orderId);

        if (future != null) {
            log.info("Canceled wallet verification future: orderId={}", orderId);
            future.completeExceptionally(
                    new CancellationException("Wallet verification canceled")
            );
        } else {
            log.warn("No pending wallet future to cancel for orderId={}", orderId);
        }
    }
}
