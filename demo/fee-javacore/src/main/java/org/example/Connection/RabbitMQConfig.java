package org.example.Connection;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class RabbitMQConfig {

    private String queueName = "payment.queue";
    private String exchange = "payment.exchange";
    private String routingKey = "payment.routingkey";

    public String getQueueName() {
        return queueName;
    }

    public String getExchange() {
        return exchange;
    }

    public String getRoutingKey() {
        return routingKey;
    }


    public Channel createChannel() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        return connection.createChannel();
    }

    public void declareQueue(Channel channel) throws IOException {
        channel.queueDeclare(queueName, false, false, false, null);
        channel.exchangeDeclare(exchange, "direct");
        channel.queueBind(queueName, exchange, routingKey);
    }
}