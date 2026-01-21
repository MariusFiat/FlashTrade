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
    public static final String ORDER_CANCEL_QUEUE  = "order.cancel.queue";
    public static final String TRADE_HISTORY_REQUEST_QUEUE = "trade.history.request.queue";
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

    // Wallet Verification Queues & Exchanges
    public static final String BUY_ORDER_QUEUE = "buy_order_request_queue";
    public static final String ORDER_RESPONSE_EXCHANGE = "buy_order_response_exchange";
    public static final String ORDER_RESPONSE_ROUTING_KEY = "buy_order.response.key";

    public static final String SELL_ORDER_QUEUE = "sell_order_request_queue";
    public static final String SELL_RESPONSE_EXCHANGE = "sell_order_response_exchange";
    public static final String SELL_RESPONSE_ROUTING_KEY = "sell_order.response.key";

    public static final String ORDER_RESPONSE_QUEUE = "trading_order_response_queue";
    public static final String SELL_RESPONSE_QUEUE = "trading_sell_response_queue";

    public static final String BUY_ORDER_CLOSE_QUEUE = "buy_order_close_queue";
    public static final String SELL_ORDER_CLOSE_QUEUE = "sell_order_close_queue";

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
    public Queue buyOrderCloseQueue() {
        return new Queue(BUY_ORDER_CLOSE_QUEUE, true);
    }

    @Bean
    public Queue sellOrderCloseQueue() {
        return new Queue(SELL_ORDER_CLOSE_QUEUE, true);
    }

    //Stock price update -> user_service end

    //Stock price update -> user_service
    public static final String STOCK_DATA_QUEUE = "stock_info_queue";
    public static final String STOCK_EXCHANGE = "trading_data_exchange";

    //Stock performance request from api gateway
    public static final String STOCK_PERFORMANCE_REQUEST_QUEUE = "stock.performance.request.queue";
    public static final String STOCK_PERFORMANCE_RESPONSE_QUEUE = "stock.performance.response.queue";

    public static final String STOCK_PERFORMANCE_REQUEST_KEY = "stock.performance.request";
    public static final String STOCK_PERFORMANCE_RESPONSE_KEY = "stock.performance.response";

    @Bean
    public Queue performanceRequestQueue() {
        return new Queue(STOCK_PERFORMANCE_REQUEST_QUEUE, true);
    }

    @Bean
    public Queue performanceResponseQueue() {
        return new Queue(STOCK_PERFORMANCE_RESPONSE_QUEUE, true);
    }

    @Bean
    public Binding performanceRequestBinding() {
        return BindingBuilder
                .bind(performanceRequestQueue())
                .to(stockExchange())
                .with(STOCK_PERFORMANCE_REQUEST_KEY);
    }

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
        factory.setDefaultRequeueRejected(false);

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

    @Bean
    public Queue orderCancelQueue() {
        return new Queue(ORDER_CANCEL_QUEUE, true);
    }
    
    @Bean
    public Queue tradeHistoryRequestQueue() {
        return new Queue(TRADE_HISTORY_REQUEST_QUEUE);
    }

    @Bean
    public Queue activeOrdersRequestQueue() {
        return new Queue(ACTIVE_ORDERS_REQUEST_QUEUE);
    }

    @Bean
    public Queue marketDataRequestQueue() {
        return new Queue(MARKET_DATA_REQUEST_QUEUE);
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
                .bind(orderCancelQueue())
                .to(tradingCommandsExchange())
                .with(ORDER_CANCEL_KEY);
    }
    
    @Bean
    public Binding tradeHistoryBinding() {
        return BindingBuilder
                .bind(tradeHistoryRequestQueue())
                .to(tradingCommandsExchange())
                .with(TRADE_HISTORY_KEY);
    }

    @Bean
    public Binding activeOrdersBinding() {
        return BindingBuilder
                .bind(activeOrdersRequestQueue())
                .to(tradingCommandsExchange())
                .with(ACTIVE_ORDERS_KEY);
    }

    @Bean
    public Binding marketDataBinding() {
        return BindingBuilder
                .bind(marketDataRequestQueue())
                .to(tradingCommandsExchange())
                .with(MARKET_DATA_KEY);
    }

}
