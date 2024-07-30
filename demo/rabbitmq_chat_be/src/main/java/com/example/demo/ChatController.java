package com.example.demo;

import com.example.demo.MessageSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:3000/")
public class ChatController {

    @Autowired
    private MessageSender messageSender;

    @MessageMapping("/sendMessage")
    public void sendMessage(String message) {
        messageSender.sendMessage(message);
    }
    @MessageMapping("/chatNewUser")
    public WebSocketChatMessage addUser(@Payload WebSocketChatMessage webSocketChatMessage,
                                        SimpMessageHeaderAccessor headerAccessor) {
        headerAccessor.getSessionAttributes().put("username", webSocketChatMessage.getSender());
        return webSocketChatMessage;
    }
}
