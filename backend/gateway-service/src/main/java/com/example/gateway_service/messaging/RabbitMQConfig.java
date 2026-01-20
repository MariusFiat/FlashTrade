package com.example.gateway_service.messaging;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    
    // Exchange names
    public static final String TRADING_COMMANDS_EXCHANGE = "trading.commands.exchange";
    public static final String TRADING_EVENTS_EXCHANGE = "trading.events.exchange";
    
    // Queue names for Gateway to receive events
    public static final String GATEWAY_ORDER_EVENT_QUEUE = "gateway.order.event.queue";
    
    // Routing keys
    public static final String ORDER_PLACE_KEY = "order.place";
    public static final String ORDER_CANCEL_KEY = "order.cancel";
    public static final String ORDER_UPDATE_KEY = "order.update";
    public static final String ORDER_CREATED_KEY = "order.created";
    public static final String ORDER_EXECUTED_KEY = "order.executed";
    public static final String ORDER_FAILED_KEY = "order.failed";
    
    // Message converter for JSON serialization
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
    
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jsonMessageConverter());
        return factory;
    }
    
    // Commands Exchange (Gateway -> Trading Service)
    @Bean
    public TopicExchange tradingCommandsExchange() {
        return new TopicExchange(TRADING_COMMANDS_EXCHANGE);
    }
    
    // Events Exchange (Trading Service -> Gateway)
    @Bean
    public TopicExchange tradingEventsExchange() {
        return new TopicExchange(TRADING_EVENTS_EXCHANGE);
    }
    
    // Queue for Gateway to receive order events
    @Bean
    public Queue gatewayOrderEventQueue() {
        return new Queue(GATEWAY_ORDER_EVENT_QUEUE, true);
    }
    
    // Binding: Events Exchange -> Gateway Event Queue
    @Bean
    public Binding orderCreatedEventBinding() {
        return BindingBuilder
                .bind(gatewayOrderEventQueue())
                .to(tradingEventsExchange())
                .with(ORDER_CREATED_KEY);
    }
    
    @Bean
    public Binding orderFailedEventBinding() {
        return BindingBuilder
                .bind(gatewayOrderEventQueue())
                .to(tradingEventsExchange())
                .with(ORDER_FAILED_KEY);
    }

    @Bean
    public Binding orderUpdateEventBinding() {
        return BindingBuilder
                .bind(gatewayOrderEventQueue())
                .to(tradingEventsExchange())
                .with(ORDER_UPDATE_KEY);
    }
}
