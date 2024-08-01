package com.example.demo;

import com.example.demo.Dto.MessageDto;
import com.google.gson.Gson;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.TimeoutUtils;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class MessageReceiver {

    private final SimpMessagingTemplate template;
    private final RedisTemplate<String, String> redisTemplate;
    public MessageReceiver(SimpMessagingTemplate template, RedisTemplate<String, String> redisTemplate) {
        this.template = template;
        this.redisTemplate = redisTemplate;
    }

    public void receiveMessage(String message) {
        Gson gson = new Gson();
        MessageDto messageDto = gson.fromJson(message, MessageDto.class);
        String roomId = messageDto.getRoomId();
        WebSocketChatMessage webSocketChatMessage = messageDto.getWebSocketChatMessage();

        System.out.println("Received message for room: " + roomId);
        System.out.println("Message: " + webSocketChatMessage);
        redisTemplate.opsForList().rightPush(roomId, gson.toJson(webSocketChatMessage));
        template.convertAndSend("/topic/" + roomId + "/messages", webSocketChatMessage);
    }
}
