package com.example.gateway_service.messaging;

import java.util.List;
import java.util.UUID;

import com.example.gateway_service.messaging.dto.CancelOrderCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import com.example.gateway_service.messaging.dto.PlaceOrderCommand;

@Component
public class OrderCommandPublisher {
    private static final Logger log = LoggerFactory.getLogger(OrderCommandPublisher.class);
    private final RabbitTemplate rabbitTemplate;

    public OrderCommandPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public String publishPlaceOrderCommand(PlaceOrderCommand command) {
        // Generate correlation ID for tracking
        String correlationId = UUID.randomUUID().toString();
        command.setCorrelationId(correlationId);

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.TRADING_COMMANDS_EXCHANGE,
                RabbitMQConfig.ORDER_PLACE_KEY,
                command
        );
        
        log.info("Published place order command: symbol={}, quantity={}, correlationId={}",
                command.getSymbol(), command.getQuantity(), correlationId);
        
        return correlationId;
    }
    
    public String publishCancelOrderCommand(Long orderId, String userId) {
        String correlationId = UUID.randomUUID().toString();

        CancelOrderCommand cmd = new CancelOrderCommand(
                orderId,
                userId,
                correlationId
        );

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.TRADING_COMMANDS_EXCHANGE,
                RabbitMQConfig.ORDER_CANCEL_KEY,
                cmd
        );
        log.info("Published cancel order command: orderId={}, correlationId={}",
                orderId, correlationId);
        
        return correlationId;
    }
}
