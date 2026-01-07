package com.example.user_service.messaging;

import com.example.user_service.dto.StockMessage;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class StockUpdateListener {

    @RabbitListener(queues = "stock_info_queue")
    public void handleStockUpdate(StockMessage message) {
        System.out.println("Updated get for: " + message.getSymbol());
    }
}