package com.example.demo;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String QUEUE_NAME = "chatQueue";
    public static final String EXCHANGE_NAME = "chatExchange";
    public static final String ROUTING_KEY = "chat.message.#";

    @Bean
    public Queue chatQueue() {
        return new Queue(QUEUE_NAME, false);
    }
    @Bean
    DirectExchange exchange() {
        return new DirectExchange(EXCHANGE_NAME);
    }


    @Bean
    public Binding binding(Queue chatQueue, DirectExchange chatExchange) {
        return BindingBuilder.bind(chatQueue).to(chatExchange).with(ROUTING_KEY);
    }
}
