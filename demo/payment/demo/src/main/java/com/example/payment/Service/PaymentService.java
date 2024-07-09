package com.example.payment.Service;

import com.example.payment.Entity.PaymentMessage;
import com.example.payment.Request.PaymentRequest;
import com.example.payment.Response.ResponseFromCore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeoutException;

@Service
public class PaymentService {
    private final RabbitTemplate rabbitTemplate;
    private final RedisTemplate redisTemplate;

    private ResponseFromCore responseFromCore;
    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);

    @Value("${javainuse.rabbitmq.exchange}")
    private String exchange;

    @Value("${javainuse.rabbitmq.routingkey}")
    private String routingkey;

    @Value("${javainuse.rabbitmq.queue.response}")
    private String queue;

    public PaymentService(RabbitTemplate rabbitTemplate, RedisTemplate redisTemplate) {
        this.rabbitTemplate = rabbitTemplate;
        this.redisTemplate = redisTemplate;
    }

    public void logRequest(PaymentRequest request, String clientIp) {
//        System.out.println("Request received from IP: " + clientIp + " with data: " + request.toString());
        logger.info("Request received from IP: " + clientIp + " with data: " + request.toString());
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

    public void sendToRabbitMQ(PaymentRequest paymentRequest, String token) {
        PaymentMessage paymentMessage = new PaymentMessage(paymentRequest, token);
        rabbitTemplate.convertAndSend(exchange, routingkey, paymentMessage);
    }

    public void logResponse(ResponseFromCore responseFromCore, Long rangeTime) {
        if (responseFromCore.getCode() != null) {
            logger.info("Response received with data " + responseFromCore.toString() + " in " + rangeTime + "ms");
//            System.out.println("Response received with data " + responseFromCore.toString() + " in " + rangeTime + "ms");
        } else {
            logger.warn("Response received but responseFromCore is null." + " in " + rangeTime + "ms");
//            System.out.println("Response received but responseFromCore is null.");
        }
    }

    public void storeResponse(ResponseFromCore response) {
        this.responseFromCore = response;
    }

    @RabbitListener(queues = "${javainuse.rabbitmq.queue.response}")
    public void receiveResponseFromCore(ResponseFromCore response) {
        storeResponse(response);
    }
}
