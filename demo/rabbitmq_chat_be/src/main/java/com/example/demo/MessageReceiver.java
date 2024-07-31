package com.example.demo;

import com.example.demo.Dto.MessageDto;
import com.google.gson.Gson;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class MessageReceiver {
    private final SimpMessagingTemplate messagingTemplate;

    public MessageReceiver(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void receiveMessage(String message) {
        Gson gson = new Gson();
        MessageDto messageDto = gson.fromJson(message, MessageDto.class);
        String roomId = messageDto.getRoomId();
        WebSocketChatMessage webSocketChatMessage = messageDto.getWebSocketChatMessage();

        System.out.println("Received message for room: " + roomId);
        System.out.println("Message: " + webSocketChatMessage);

        messagingTemplate.convertAndSend("/topic/" + roomId + "/messages", webSocketChatMessage);
    }

}
