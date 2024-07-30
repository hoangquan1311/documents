package com.example.demo;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class MessageSender {

    private final RedisTemplate<String, String> redisTemplate;

    public MessageSender(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void sendMessage(String message) {
        redisTemplate.convertAndSend("xinchao", message);
    }
}
