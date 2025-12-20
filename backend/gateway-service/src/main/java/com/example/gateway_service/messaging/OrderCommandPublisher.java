package com.example.gateway_service.messaging;

import com.example.gateway_service.messaging.dto.PlaceOrderCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderCommandPublisher {
    private final RabbitTemplate rabbitTemplate;

    public String publishPlaceOrderCommand(PlaceOrderCommand command) {
        // Generate correlation ID for tracking
        String correlationId = UUID.randomUUID().toString();
        command.setCorrelationId(correlationId);
        
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.TRADING_COMMANDS_EXCHANGE,
                RabbitMQConfig.ORDER_PLACE_KEY,
                command
        );
        
        log.info("📤 Published place order command: symbol={}, quantity={}, correlationId={}", 
                command.getSymbol(), command.getQuantity(), correlationId);
        
        return correlationId;
    }
    
    public String publishCancelOrderCommand(Long orderId, String userId) {
        String correlationId = UUID.randomUUID().toString();
        
        // TODO: Create CancelOrderCommand DTO
        log.info("📤 Published cancel order command: orderId={}, correlationId={}", 
                orderId, correlationId);
        
        return correlationId;
    }
}
