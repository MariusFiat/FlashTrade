package com.example.ai_service.config;

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
    public static final String AI_EXCHANGE = "ai.exchange";
    public static final String TRADING_DATA_EXCHANGE = "trading_data_exchange";
    public static final String TRADING_EVENTS_EXCHANGE = "trading.events.exchange";
    
    // Queue names
    public static final String AI_ANALYSIS_REQUEST_QUEUE = "ai.analysis.request.queue";
    public static final String AI_PREDICTION_REQUEST_QUEUE = "ai.prediction.request.queue";
    public static final String AI_SENTIMENT_REQUEST_QUEUE = "ai.sentiment.request.queue";
    public static final String AI_TRADE_EVENTS_QUEUE = "ai.trade.events.queue";
    public static final String STOCK_DATA_QUEUE = "stock_info_queue";
    
    // Routing keys
    public static final String AI_ANALYSIS_KEY = "ai.analysis";
    public static final String AI_PREDICTION_KEY = "ai.prediction";
    public static final String AI_SENTIMENT_KEY = "ai.sentiment";
    public static final String TRADE_EVENT_KEY = "trade.executed";
    
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
    
    // Exchanges
    @Bean
    public TopicExchange aiExchange() {
        return new TopicExchange(AI_EXCHANGE);
    }
    
    @Bean
    public TopicExchange tradingDataExchange() {
        return new TopicExchange(TRADING_DATA_EXCHANGE);
    }
    
    @Bean
    public TopicExchange tradingEventsExchange() {
        return new TopicExchange(TRADING_EVENTS_EXCHANGE);
    }
    
    // Queues
    @Bean
    public Queue aiAnalysisRequestQueue() {
        return new Queue(AI_ANALYSIS_REQUEST_QUEUE, true);
    }
    
    @Bean
    public Queue aiPredictionRequestQueue() {
        return new Queue(AI_PREDICTION_REQUEST_QUEUE, true);
    }
    
    @Bean
    public Queue aiSentimentRequestQueue() {
        return new Queue(AI_SENTIMENT_REQUEST_QUEUE, true);
    }
    
    @Bean
    public Queue aiTradeEventsQueue() {
        return new Queue(AI_TRADE_EVENTS_QUEUE, true);
    }
    
    @Bean
    public Queue stockDataQueue() {
        return new Queue(STOCK_DATA_QUEUE, false);
    }
    
    // Bindings
    @Bean
    public Binding aiAnalysisBinding() {
        return BindingBuilder
            .bind(aiAnalysisRequestQueue())
            .to(aiExchange())
            .with(AI_ANALYSIS_KEY);
    }
    
    @Bean
    public Binding aiPredictionBinding() {
        return BindingBuilder
            .bind(aiPredictionRequestQueue())
            .to(aiExchange())
            .with(AI_PREDICTION_KEY);
    }
    
    @Bean
    public Binding aiSentimentBinding() {
        return BindingBuilder
            .bind(aiSentimentRequestQueue())
            .to(aiExchange())
            .with(AI_SENTIMENT_KEY);
    }
    
    @Bean
    public Binding stockDataBinding() {
        return BindingBuilder
            .bind(stockDataQueue())
            .to(tradingDataExchange())
            .with("stock.info.#");
    }
    
    @Bean
    public Binding tradeEventsBinding() {
        return BindingBuilder
            .bind(aiTradeEventsQueue())
            .to(tradingEventsExchange())
            .with(TRADE_EVENT_KEY);
    }
}