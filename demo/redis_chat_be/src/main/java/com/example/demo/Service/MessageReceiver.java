package com.example.demo.Service;

import com.example.demo.Dto.MessageDto;
import com.example.demo.Entity.Message;
import com.example.demo.Entity.WebSocketChatMessage;
import com.example.demo.Repository.MessageRepository;
import com.google.gson.Gson;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class MessageReceiver {

    private final SimpMessagingTemplate template;
    private final RedisTemplate<String, String> redisTemplate;
    private final MessageRepository messageRepository;
    public MessageReceiver(SimpMessagingTemplate template, RedisTemplate<String, String> redisTemplate, MessageRepository messageRepository) {
        this.template = template;
        this.redisTemplate = redisTemplate;
        this.messageRepository = messageRepository;
    }

    public void receiveMessage(String message) {
        Gson gson = new Gson();
        MessageDto messageDto = gson.fromJson(message, MessageDto.class);
        String roomId = messageDto.getRoomId();
        WebSocketChatMessage webSocketChatMessage = messageDto.getWebSocketChatMessage();
        webSocketChatMessage.setType("Chat");
        Message message1 = new Message();
        message1.setContent(webSocketChatMessage.getContent());
        message1.setRoomId(roomId);
        message1.setType(webSocketChatMessage.getType());
        message1.setFile(webSocketChatMessage.getFile());
        message1.setSender(webSocketChatMessage.getSender());
        messageRepository.save(message1);
        System.out.println("Received message for room: " + roomId);
        System.out.println("Message file: " + webSocketChatMessage.getFile());
        template.convertAndSend("/topic/" + roomId + "/messages", webSocketChatMessage);
    }
}
