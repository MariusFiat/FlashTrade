package com.example.trading_service.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.example.trading_service.config.RabbitMQConfig;
import com.example.trading_service.messaging.dto.PlaceOrderCommand;
import com.example.trading_service.service.OrderService;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class OrderCommandListener {
    private static final Logger log = LoggerFactory.getLogger(OrderCommandListener.class);
    
    private final OrderService orderService;

    public OrderCommandListener(OrderService orderService) {
        this.orderService = orderService;
    }
    
    @RabbitListener(queues = RabbitMQConfig.ORDER_COMMAND_QUEUE)
    public void handleOrderCommand(List<PlaceOrderCommand> commands) {
        if (commands.isEmpty()) return;

        log.info("Received batch of {} orders.", commands.size());

        long startTime = System.currentTimeMillis();
        long timeoutMs = 100;

        try {
            commands.forEach(command ->
                    log.info(
                            "Processing order: userId={}, symbol={}, quantity={}, side={}, correlationId={}",
                            command.getUserId(),
                            command.getSymbol(),
                            command.getQuantity(),
                            command.getSide(),
                            command.getCorrelationId()
                    )
            );

            Map<String, List<PlaceOrderCommand>> ordersBySymbol = commands.stream()
                    .collect(Collectors.groupingBy(PlaceOrderCommand::getSymbol));

            ordersBySymbol.forEach(orderService::placeOrderBatch);
        }catch (Exception e){
            log.error("Error processing batch: {}", e.getMessage());
        }
    }
}
