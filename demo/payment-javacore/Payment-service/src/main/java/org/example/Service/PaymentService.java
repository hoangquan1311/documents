package org.example.Service;

import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import org.example.Config.RabbitMQConfig;
import org.example.Entity.PaymentMessage;
import org.example.Request.PaymentRequest;
import org.example.Response.ResponseFromCore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;

public class PaymentService {
    private final Logger logger = LoggerFactory.getLogger(PaymentService.class);
    private ResponseFromCore responseFromCore;
    public void logRequest(PaymentRequest request, String clientIp) {
        logger.info("Request received from IP: " + clientIp + " with data: " + request.toString());
    }
    public void storeResponse(ResponseFromCore response) {
        this.responseFromCore = response;
    }

    public void sendToRabbitMQ(String token, PaymentRequest paymentRequest) {
        RabbitMQConfig rabbitMQConfig = new RabbitMQConfig();
        Gson gson = new Gson();
        PaymentMessage paymentMessage = new PaymentMessage(token, paymentRequest);
        String message = gson.toJson(paymentMessage);

        try {
            Channel channel = rabbitMQConfig.createChannel();
            rabbitMQConfig.declareQueue(channel);
            channel.basicPublish(
                    rabbitMQConfig.getExchange(),
                    rabbitMQConfig.getRoutingKey(),
                    null,
                    message.getBytes(StandardCharsets.UTF_8)
            );

            logger.info("Sent message to RabbitMQ: " + message);

            channel.close();
            channel.getConnection().close();
        } catch (Exception e) {
            logger.error("Failed to send message to RabbitMQ: " + e.getMessage());
        }
    }

    public CompletableFuture<String> startListeningForResponses(Long startTime) {
        RabbitMQConfig rabbitMQConfig = new RabbitMQConfig();
        Gson gson = new Gson();
        CompletableFuture<String> futureResponse = new CompletableFuture<>();
        try {
            Channel channel = rabbitMQConfig.createChannel();
            rabbitMQConfig.declareQueue(channel);

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String responseMessage = new String(delivery.getBody(), StandardCharsets.UTF_8);
                logger.info("Received response from RabbitMQ: " + responseMessage);
                Long endTime = System.currentTimeMillis();
                Long rangeTime = endTime - startTime;
                logger.info("Response received with data " + rangeTime + "ms");
                ResponseFromCore responseFromCore1 = gson.fromJson(responseMessage, ResponseFromCore.class);
                storeResponse(responseFromCore1);
                futureResponse.complete(responseMessage);
            };

            channel.basicConsume(rabbitMQConfig.getResponseQueue(), true, deliverCallback, consumerTag -> {});
            futureResponse.whenComplete((result, ex) -> {
                try {
                    if (channel != null && channel.isOpen()) {
                        channel.close();
                    }
                } catch (IOException | TimeoutException e) {
                    logger.error("Failed to close channel: " + e.getMessage());
                }
            });

        } catch (Exception e) {
            Long endTime = System.currentTimeMillis();
            Long rangeTime = endTime - startTime;
            logger.error("Failed to start listening for responses: " + e.getMessage());
            logger.warn("Response received but responseFromCore is null." + " in " + rangeTime + "ms");
            futureResponse.completeExceptionally(e);
        }
        return futureResponse;
    }
    public ResponseFromCore waitForCoreResponse(String token, long timeout) throws TimeoutException {
        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTime < timeout) {
            if (responseFromCore != null && token.equals(responseFromCore.getData())) {
                ResponseFromCore response = responseFromCore;
                responseFromCore = null;
                return response;
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        throw new TimeoutException("Timeout waiting for response from Core");
    }
}
