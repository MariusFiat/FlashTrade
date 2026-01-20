package com.example.user_service.messaging;

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
public class RabbitStockConfig {
    public static final String STOCK_DATA_QUEUE = "stock_info_queue";
    public static final String STOCK_EXCHANGE = "trading_data_exchange";

    //Trading_service will send a message when a user tries to publish an order.
    //The user_service will check the current wallet balance and send back to the trading_service true if [...]
    //There are sufficient funds + reserve that funds. False otherwise.
    public static final String BUY_ORDER_QUEUE = "buy_order_request_queue";
    public static final String ORDER_RESPONSE_EXCHANGE = "buy_order_response_exchange";
    public static final String ORDER_RESPONSE_ROUTING_KEY = "buy_order.response.key";

    //Trading_service will send a message to the user_service after a buy order is closed, whether closed or matched.
    //If the status sent via message is "closed," then the reserved funds will be moved into the current balance.
    //If the status sent via message is "matched," then verify the amount spent and update the pending funds accordingly.
    public static final String BUY_ORDER_CLOSE_QUEUE = "buy_order_close_queue";

    //Trading_service will send a message to the user_service to check if the stock exists in user's portfolio + quantity
    //User_service will send a message to the trading_service with response: true or false if the stock exists in the portfolio with the desire quantity
    public static final String SELL_ORDER_QUEUE = "sell_order_request_queue";
    public static final String SELL_RESPONSE_EXCHANGE = "sell_order_response_exchange";
    public static final String SELL_RESPONSE_ROUTING_KEY = "sell_order.response.key";

    //Trading_service will send a message to the user_service after a sell order is closed successfully.
    //User_service will read the amount of money received and update the current balance.
    public static final String SELL_ORDER_CLOSE_QUEUE = "sell_order_close_queue";

    @Bean
    public Queue stockQueue() { return new Queue(STOCK_DATA_QUEUE); }

    @Bean
    public TopicExchange stockExchange() { return new TopicExchange(STOCK_EXCHANGE); }

    @Bean
    public Binding stockBinding(Queue stockQueue, TopicExchange stockExchange) {
        return BindingBuilder.bind(stockQueue).to(stockExchange).with("stock.info.#");
    }

    @Bean
    public Queue buyOrderQueue() {
        return new Queue(BUY_ORDER_QUEUE);
    }

    @Bean
    public TopicExchange orderResponseExchange() {
        return new TopicExchange(ORDER_RESPONSE_EXCHANGE);
    }

    @Bean
    public Queue buyOrderCloseQueue() {
        return new Queue(BUY_ORDER_CLOSE_QUEUE);
    }


    @Bean
    public Queue sellOrderQueue() {
        return new Queue(SELL_ORDER_QUEUE);
    }

    @Bean
    public TopicExchange sellorderResponseExchange() {
        return new TopicExchange(SELL_RESPONSE_EXCHANGE);
    }

    @Bean
    public Queue sellOrderCloseQueue() {
        return new Queue(SELL_ORDER_CLOSE_QUEUE);
    }

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
}