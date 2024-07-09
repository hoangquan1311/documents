package com.example.core.Serivce;

import com.example.core.Convert.ConvertToTimestamp;
import com.example.core.Entity.PaymentMessage;
import com.example.core.Request.PaymentRequest;
import com.example.core.Response.ResponseFromCore1;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.concurrent.TimeUnit;

@Service
public class CoreService {

    private final RedisTemplate redisTemplate;
    private final JdbcTemplate jdbcTemplate;
    private final RabbitTemplate rabbitTemplate;
    private static final Logger logger = LoggerFactory.getLogger(CoreService.class);

    @Value("${javainuse.rabbitmq.exchange}")
    private String exchange;

    @Value("${javainuse.rabbitmq.routingkey}")
    private String routingkey;

    @Value("${javainuse.rabbitmq.routingkey.response}")
    private String routingkey2;

    @Value("${core.service.default.timeout}")
    private Long defaultTimeout;

    public CoreService(RedisTemplate redisTemplate, JdbcTemplate jdbcTemplate, RabbitTemplate rabbitTemplate) {
        this.redisTemplate = redisTemplate;
        this.jdbcTemplate = jdbcTemplate;
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(queues = "${javainuse.rabbitmq.queue}")
    public void receiveMessage(PaymentMessage message) {
        String token = message.getToken();
        if (token == null) {
            logger.warn("Received message with null token.");
            return;
        }
        redisTemplate.opsForValue().set(token, new Gson().toJson(message.getPaymentRequest()), 2, TimeUnit.MINUTES);

        try {
            long startTime = System.currentTimeMillis();
            insertIntoDB(message.getPaymentRequest());
            sendResponseToAPI(new ResponseFromCore1("00", "success", token));
            long endTime = System.currentTimeMillis();
            long processingTime = endTime - startTime;
            if(processingTime > 60000) {
                logger.warn("Processing time exceeded 1 minutes: ", processingTime + " ms");
            }
            Thread.sleep(defaultTimeout);
        } catch (Exception e) {
            sendResponseToAPI(new ResponseFromCore1("01", e.getMessage(), token));
        }
    }

    private void insertIntoDB(PaymentRequest request) {
        String sql = "INSERT INTO payment (Customer_Name, Res_Code, Data, amount, debit_Amount, real_Amount, Pay_date, Local_date) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)";
        Timestamp payDateTimestamp = ConvertToTimestamp.convertToTimestamp(request.getPayDate());
        jdbcTemplate.update(sql, request.getCustomerName(), request.getRescode(), new Gson().toJson(request),
                request.getAmount(), request.getDebitAmount(), request.getRealAmount(), payDateTimestamp);
    }

    private void sendResponseToAPI(ResponseFromCore1 response) {
        rabbitTemplate.convertAndSend(exchange, routingkey2, response);
    }
}
