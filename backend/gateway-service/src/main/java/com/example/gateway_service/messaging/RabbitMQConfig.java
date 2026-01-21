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
    
    // RPC Queues
    public static final String TRADE_HISTORY_REQUEST_QUEUE = "trade.history.request.queue";
    public static final String TRADE_HISTORY_REPLY_QUEUE = "trade.history.reply.queue";
    public static final String ACTIVE_ORDERS_REQUEST_QUEUE = "active.orders.request.queue";
    public static final String MARKET_DATA_REQUEST_QUEUE = "market.data.request.queue";

    // Routing keys
    public static final String ORDER_PLACE_KEY = "order.place";
    public static final String ORDER_CANCEL_KEY = "order.cancel";
    public static final String ORDER_UPDATE_KEY = "order.update";
    public static final String ORDER_CREATED_KEY = "order.created";
    public static final String ORDER_EXECUTED_KEY = "order.executed";
    public static final String ORDER_FAILED_KEY = "order.failed";
    public static final String TRADE_HISTORY_KEY = "trade.history";
    public static final String ACTIVE_ORDERS_KEY = "active.orders";
    public static final String MARKET_DATA_KEY = "market.data";
    
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
    
    // RPC Bindings
    @Bean
    public Queue tradeHistoryRequestQueue() {
        return new Queue(TRADE_HISTORY_REQUEST_QUEUE);
    }
    
    @Bean
    public Binding tradeHistoryBinding() {
        return BindingBuilder
                .bind(tradeHistoryRequestQueue())
                .to(tradingCommandsExchange())
                .with(TRADE_HISTORY_KEY);
    }

    @Bean
    public Queue activeOrdersRequestQueue() {
        return new Queue(ACTIVE_ORDERS_REQUEST_QUEUE);
    }

    @Bean
    public Binding activeOrdersBinding() {
        return BindingBuilder
                .bind(activeOrdersRequestQueue())
                .to(tradingCommandsExchange())
                .with(ACTIVE_ORDERS_KEY);
    }

    @Bean
    public Queue marketDataRequestQueue() {
        return new Queue(MARKET_DATA_REQUEST_QUEUE);
    }

    @Bean
    public Binding marketDataBinding() {
        return BindingBuilder
                .bind(marketDataRequestQueue())
                .to(tradingCommandsExchange())
                .with(MARKET_DATA_KEY);
    }
}
