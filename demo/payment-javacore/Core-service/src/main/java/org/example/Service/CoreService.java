package org.example.Service;

import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import org.example.Config.OracleConnection;
import org.example.Config.RabbitMQConfig;
import org.example.Config.RedisConfig;
import org.example.Convert.ConvertToTimestamp;
import org.example.Entity.PaymentMessage;
import org.example.Response.ResponseFromCore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.concurrent.*;

public class CoreService {
    private static final Logger logger = LoggerFactory.getLogger(CoreService.class);

    public void startListening() {
        RabbitMQConfig rabbitMQConfig = new RabbitMQConfig();
        RedisConfig redisConfig = new RedisConfig();

        try {
            Jedis jedis = redisConfig.getJedis();

            Channel channel = rabbitMQConfig.createChannel();
            rabbitMQConfig.declareQueue(channel);

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
                processMessage(message, jedis);
            };
            channel.basicConsume(rabbitMQConfig.getQueueName(), true, deliverCallback, consumerTag -> {});

        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }

    public void processMessage(String message, Jedis jedis) {
        Gson gson = new Gson();
        PaymentMessage paymentMessage = gson.fromJson(message, PaymentMessage.class);
        logger.info("Payment Request: " + paymentMessage.getPaymentRequest());
        logger.info("Token: " + paymentMessage.getToken());

        String token = paymentMessage.getToken();
        String jsonMessage = gson.toJson(paymentMessage);
        jedis.setex(token, 120, jsonMessage);
        logger.info("Stored message in Redis with key: " + token);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<?> future = executor.submit(() -> {
            insertIntoOracle(paymentMessage);
        });

        try {
            future.get(2, TimeUnit.MINUTES);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            future.cancel(true);
            sendResponseToRabbitMQ("01", "timeout", token);
            logger.error("Timeout occurred while processing message: " + e.getMessage());
        } finally {
            executor.shutdownNow();
        }
    }

    public void insertIntoOracle(PaymentMessage paymentMessage) {
        long startTime = System.currentTimeMillis();
        String sql = "INSERT INTO payment (Customer_Name, Res_Code, Data, amount, debit_Amount, real_Amount, Pay_date, Local_date) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)";

        try (Connection connection = OracleConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, paymentMessage.getPaymentRequest().getCustomerName());
            preparedStatement.setString(2, paymentMessage.getPaymentRequest().getRescode());
            preparedStatement.setString(3, new Gson().toJson(paymentMessage.getPaymentRequest()));
            preparedStatement.setString(4, paymentMessage.getPaymentRequest().getAmount());
            preparedStatement.setString(5, paymentMessage.getPaymentRequest().getDebitAmount());
            preparedStatement.setString(6, paymentMessage.getPaymentRequest().getRealAmount());

            Timestamp payDateTimestamp = ConvertToTimestamp.convertToTimestamp(paymentMessage.getPaymentRequest().getPayDate());
            preparedStatement.setTimestamp(7, payDateTimestamp);

            int rowsInserted = preparedStatement.executeUpdate();
            if (rowsInserted > 0) {
                sendResponseToRabbitMQ("00", "success", paymentMessage.getToken());
                logger.info("Data inserted into Oracle database successfully.");
                long endTime = System.currentTimeMillis();
                long processingTime = endTime - startTime;
                if (processingTime > 60000) {
                    logger.warn("Processing time exceeded 1 minute: " + processingTime + " ms");
                }
                Thread.sleep(30000);
            } else {
                sendResponseToRabbitMQ("01", "fail", paymentMessage.getToken());
                logger.error("Failed to insert data into Oracle database.");
            }

        } catch (SQLException | InterruptedException e) {
            sendResponseToRabbitMQ("01", "fail", paymentMessage.getToken());
            logger.error("Exception while processing message: " + e.getMessage());
        }
    }

    public void sendResponseToRabbitMQ(String errorCode, String message, String token) {
        RabbitMQConfig rabbitMQConfig = new RabbitMQConfig();
        Gson gson = new Gson();
        try {
            Channel channel = rabbitMQConfig.createChannel();
            ResponseFromCore response = new ResponseFromCore(errorCode, message, token);
            String responseMessage = gson.toJson(response);
            channel.basicPublish(rabbitMQConfig.getExchange(), rabbitMQConfig.getResponseRoutingKey(), null,
                    responseMessage.getBytes(StandardCharsets.UTF_8));
            logger.info("Response sent to RabbitMQ: " + responseMessage);
            channel.close();
            channel.getConnection().close();
        } catch (IOException | TimeoutException e) {
            logger.error("Failed to send response to RabbitMQ: " + e.getMessage());
        }
    }
}
