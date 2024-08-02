package com.example.demo.Serivce;

import com.example.demo.Dto.MessageDto;
import com.example.demo.Config.RabbitMQConfig;
import com.example.demo.Entity.WebSocketChatMessage;
import com.google.gson.Gson;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MessageSender {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendMessage(String roomId, WebSocketChatMessage webSocketChatMessage) {
        MessageDto messageDto = new MessageDto();
        messageDto.setRoomId(roomId);
        messageDto.setWebSocketChatMessage(webSocketChatMessage);
        Gson gson = new Gson();
        String message = gson.toJson(messageDto);
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, RabbitMQConfig.ROUTING_KEY, message);
    }
}
