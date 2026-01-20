package com.example.trading_service.messaging;

import com.example.trading_service.config.RabbitMQConfig;
import com.example.trading_service.messaging.dto.CancelOrderCommand;
import com.example.trading_service.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class OrderCancelListener {
    private static final Logger log = LoggerFactory.getLogger(OrderCancelListener.class);

    private final OrderService orderService;

    public OrderCancelListener(OrderService orderService) {
        this.orderService = orderService;
    }

    @RabbitListener(
            queues = RabbitMQConfig.ORDER_CANCEL_QUEUE,
            containerFactory = "singleListenerFactory"
    )
    public void onCancelOrder(CancelOrderCommand command) {
        if (command.getOrderId() == null) {
            log.error("Received cancel command with NULL orderId: {}", command);
            return; // ⛔ DO NOT TOUCH DB
        }

        log.info(
                "Cancel request received: orderId={}, userId={}, correlationId={}",
                command.getOrderId(),
                command.getUserId(),
                command.getCorrelationId()
        );
        orderService.cancelOrder(
                command.getOrderId(),
                command.getUserId(),
                command.getCorrelationId()
        );
    }
}
