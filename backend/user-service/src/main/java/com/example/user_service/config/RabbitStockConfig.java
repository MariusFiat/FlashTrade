package com.example.user_service.config;

import ch.qos.logback.classic.pattern.MessageConverter;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitStockConfig {
    public static final String STOCK_DATA_QUEUE = "stock_info_queue";
    public static final String STOCK_EXCHANGE = "trading_data_exchange";

    @Bean
    public Queue stockQueue() { return new Queue(STOCK_DATA_QUEUE); }

    @Bean
    public TopicExchange stockExchange() { return new TopicExchange(STOCK_EXCHANGE); }

    @Bean
    public Binding stockBinding(Queue stockQueue, TopicExchange stockExchange) {
        return BindingBuilder.bind(stockQueue).to(stockExchange).with("stock.info.#");
    }

    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
