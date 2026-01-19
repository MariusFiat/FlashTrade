package com.example.trading_service.config;

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

    // Queue names
    public static final String ORDER_COMMAND_QUEUE = "order.command.queue";
    public static final String ORDER_EVENT_QUEUE = "order.event.queue";

    // Routing keys
    public static final String ORDER_PLACE_KEY = "order.place";
    public static final String ORDER_CANCEL_KEY = "order.cancel";
    public static final String ORDER_UPDATE_KEY = "order.update";
    public static final String ORDER_CREATED_KEY = "order.created";
    public static final String ORDER_EXECUTED_KEY = "order.executed";
    public static final String ORDER_FAILED_KEY = "order.failed";

    //Stock price update -> user_service
    public static final String STOCK_DATA_QUEUE = "stock_info_queue";
    public static final String STOCK_EXCHANGE = "trading_data_exchange";

    // Wallet Verification Queues & Exchanges
    public static final String BUY_ORDER_QUEUE = "buy_order_request_queue";
    public static final String ORDER_RESPONSE_EXCHANGE = "buy_order_response_exchange";
    public static final String ORDER_RESPONSE_ROUTING_KEY = "buy_order.response.key";

    public static final String SELL_ORDER_QUEUE = "sell_order_request_queue";
    public static final String SELL_RESPONSE_EXCHANGE = "sell_order_response_exchange";
    public static final String SELL_RESPONSE_ROUTING_KEY = "sell_order.response.key";

    public static final String ORDER_RESPONSE_QUEUE = "trading_order_response_queue";
    public static final String SELL_RESPONSE_QUEUE = "trading_sell_response_queue";

    public static final String TRADE_SETTLEMENT_QUEUE = "trade_settlement_queue";


    @Bean
    public TopicExchange stockExchange() {
        return new TopicExchange(STOCK_EXCHANGE);
    }

    @Bean
    public Queue stockQueue() {
        return new Queue(STOCK_DATA_QUEUE);
    }

    @Bean
    public Binding stockBinding(Queue stockQueue, TopicExchange stockExchange) {
        return BindingBuilder.bind(stockQueue).to(stockExchange).with("stock.info.#");
    }

    // Wallet Verification Beans
    @Bean
    public Queue buyOrderQueue() {
        return new Queue(BUY_ORDER_QUEUE);
    }

    @Bean
    public TopicExchange orderResponseExchange() {
        return new TopicExchange(ORDER_RESPONSE_EXCHANGE);
    }

    @Bean
    public Queue orderResponseQueue() {
        return new Queue(ORDER_RESPONSE_QUEUE);
    }

    @Bean
    public Binding orderResponseBinding() {
        return BindingBuilder.bind(orderResponseQueue())
                .to(orderResponseExchange())
                .with(ORDER_RESPONSE_ROUTING_KEY);
    }

    @Bean
    public Queue sellOrderQueue() {
        return new Queue(SELL_ORDER_QUEUE);
    }

    @Bean
    public TopicExchange sellResponseExchange() {
        return new TopicExchange(SELL_RESPONSE_EXCHANGE);
    }

    @Bean
    public Queue sellResponseQueue() {
        return new Queue(SELL_RESPONSE_QUEUE);
    }

    @Bean
    public Binding sellResponseBinding() {
        return BindingBuilder.bind(sellResponseQueue())
                .to(sellResponseExchange())
                .with(SELL_RESPONSE_ROUTING_KEY);
    }

    @Bean
    public Queue tradeSettlementQueue() {
        return new Queue(TRADE_SETTLEMENT_QUEUE, true);
    }

    //Stock price update -> user_service end
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
        factory.setBatchListener(true);
        factory.setConsumerBatchEnabled(true);
        return factory;
    }

    @Bean(name = "singleListenerFactory")
    public SimpleRabbitListenerContainerFactory singleListenerFactory(
            ConnectionFactory connectionFactory) {

        SimpleRabbitListenerContainerFactory factory =
                new SimpleRabbitListenerContainerFactory();

        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jsonMessageConverter());

        return factory;
    }

    // Commands Exchange (Gateway -> Trading Service)
    @Bean
    public TopicExchange tradingCommandsExchange() {
        return new TopicExchange(TRADING_COMMANDS_EXCHANGE);
    }

    // Events Exchange (Trading Service -> Gateway/Other Services)
    @Bean
    public TopicExchange tradingEventsExchange() {
        return new TopicExchange(TRADING_EVENTS_EXCHANGE);
    }

    // Command Queue for receiving orders from gateway
    @Bean
    public Queue orderCommandQueue() {
        return new Queue(ORDER_COMMAND_QUEUE, true);
    }

    // Binding: Commands Exchange -> Order Command Queue
    @Bean
    public Binding orderPlaceBinding() {
        return BindingBuilder
                .bind(orderCommandQueue())
                .to(tradingCommandsExchange())
                .with(ORDER_PLACE_KEY);
    }

    @Bean
    public Binding orderCancelBinding() {
        return BindingBuilder
                .bind(orderCommandQueue())
                .to(tradingCommandsExchange())
                .with(ORDER_CANCEL_KEY);
    }

    @Bean
    public Binding orderUpdateBinding() {
        return BindingBuilder
                .bind(orderCommandQueue())
                .to(tradingCommandsExchange())
                .with(ORDER_UPDATE_KEY);
    }
}