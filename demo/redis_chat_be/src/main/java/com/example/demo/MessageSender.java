package com.example.demo;

import com.example.demo.Dto.MessageDto;
import com.google.gson.Gson;
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
    public void sendMessage(String roomId, WebSocketChatMessage webSocketChatMessage) {
        MessageDto messageDto = new MessageDto();
        messageDto.setRoomId(roomId);
        messageDto.setWebSocketChatMessage(webSocketChatMessage);
        Gson gson = new Gson();
        String message = gson.toJson(messageDto);
        redisTemplate.convertAndSend("xinchao", message);
    }
}
