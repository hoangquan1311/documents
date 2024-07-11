package org.example;

import com.google.gson.Gson;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import org.example.Config.RabbitMQConfig;
import org.example.Entity.PaymentMessage;
import org.example.Request.PaymentRequest;
import org.example.Token.generateToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

public class PaymentAPIService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentAPIService.class);

    public static void main(String[] args) {
        // Example JSON input representing PaymentRequest
        String jsonInput = "{\n" +
                "    \"userName\": \"NGUY T T HA\",\n" +
                "    \"customerName\": \"NGUY T T HA\",\n" +
                "    \"tranxId\": \"700056569\",\n" +
                "    \"mobileNo\": \"0336371711\",\n" +
                "    \"accountNo\": \"4129770000000139\",\n" +
                "    \"cusName\": \"NGUY T T HA\",\n" +
                "    \"invoiceNo\": \"INV-E3R487X8CYUQVFB4\",\n" +
                "    \"amount\": 200000,\n" +
                "    \"status\": \"3\",\n" +
                "    \"rescode\": \"00\",\n" +
                "    \"bankCode\": \"970436\",\n" +
                "    \"tranxNote\": \"Tru tien thanh cong, so trace 028977\",\n" +
                "    \"tranxDate\": \"20211012171434\",\n" +
                "    \"tipAndFee\": \"0\",\n" +
                "    \"type\": \"2\",\n" +
                "    \"item\": [\n" +
                "        {\n" +
                "            \"note\": \"\",\n" +
                "            \"quantity\": \"1\",\n" +
                "            \"qrInfor\": \"0002010102110216412977000000013952045812530370458037046005HANOI6304CFD9\"\n" +
                "        }\n" +
                "    ],\n" +
                "    \"qrInfo\": \"{\\\"version\\\":\\\"5.2.3\\\",\\\"PM\\\":\\\"Pixel 2 XL\\\",\\\"OV\\\":\\\"11\\\",\\\"PS\\\":\\\"IS_SAFE\\\",\\\"DT\\\":\\\"ANDROID\\\",\\\"IMEI\\\":\\\"###77fb26dca19ada4b###ffffffff-f15a-3d94-ffff-ffffef05ac4a\\\"}\",\n" +
                "    \"orderCode\": \"700056569\",\n" +
                "    \"quantity\": \"1\",\n" +
                "    \"checkSum\": \"018bdd0b131b6f623bbff42c436b23dd\",\n" +
                "    \"qrVersion\": \"2\",\n" +
                "    \"mobile\": \"0336371711\",\n" +
                "    \"respCode\": \"00\",\n" +
                "    \"respDesc\": \"Tru tien thanh cong, so trace 028977\",\n" +
                "    \"traceTransfer\": \"028977\",\n" +
                "    \"messageType\": \"1\",\n" +
                "    \"debitAmount\": \"200000.0\",\n" +
                "    \"payDate\": \"20211012171434\",\n" +
                "    \"realAmount\": \"200000.0\",\n" +
                "    \"promotionCode\": \"\",\n" +
                "    \"Url\": \"http://10.20.27.18:8080/QRCodePaymentAPIRest/rest/QrcodePayment/paymentServiceCard\",\n" +
                "    \"mobileId\": \"111001655\",\n" +
                "    \"clientId\": \"210012299\",\n" +
                "    \"device\": \"Pixel 2 XL\",\n" +
                "    \"ipAddress\": \"10.22.7.11\",\n" +
                "    \"imei\": \"###77fb26dca19ada4b###ffffffff-f15a-3d94-ffff-ffffef05ac4a\",\n" +
                "    \"totalAmount\": \"200000.0\",\n" +
                "    \"feeAmount\": \"0\",\n" +
                "    \"pcTime\": \"171432\",\n" +
                "    \"tellerId\": \"5078\",\n" +
                "    \"tellerBranch\": \"06800\",\n" +
                "    \"typeSource\": \"02\",\n" +
                "    \"bankCard\": \"02\"\n" +
                "}";
        long startTime = System.currentTimeMillis();
        Gson gson = new Gson();
        PaymentRequest paymentRequest = gson.fromJson(jsonInput, PaymentRequest.class);

        logRequest(paymentRequest);

        String token = generateToken.generateToken();
        sendToRabbitMQ(token, paymentRequest);
        startListeningForResponses(startTime);
    }

    private static void logRequest(PaymentRequest request) {
        logger.info("Payment Request: " + request.toString());
    }

    private static void sendToRabbitMQ(String token, PaymentRequest paymentRequest) {
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

    private static void startListeningForResponses(Long startTime) {
        RabbitMQConfig rabbitMQConfig = new RabbitMQConfig();

        try {
            Channel channel = rabbitMQConfig.createChannel();
            rabbitMQConfig.declareQueue(channel);

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String responseMessage = new String(delivery.getBody(), StandardCharsets.UTF_8);

                logger.info("Received response from RabbitMQ: " + responseMessage);
                Long endTime = System.currentTimeMillis();
                Long rangeTime = endTime - startTime;
                logger.info("Response received with data " + rangeTime + "ms");
            };

            channel.basicConsume(rabbitMQConfig.getResponseQueue(), true, deliverCallback, consumerTag -> {});
        } catch (Exception e) {
            Long endTime = System.currentTimeMillis();
            Long rangeTime = endTime - startTime;
            logger.error("Failed to start listening for responses: " + e.getMessage());
            logger.warn("Response received but responseFromCore is null." + " in " + rangeTime + "ms");
        }
    }
}
