package com.example.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class MessageReceiver {

    private final SimpMessagingTemplate template;
    private final ObjectMapper mapper;

    public MessageReceiver(SimpMessagingTemplate template, ObjectMapper mapper) {
        this.template = template;
        this.mapper = mapper;
    }

    public void receiveMessage(String message) {
        System.out.println("Received: " + message);
        template.convertAndSend("/topic/messages", message);
    }
}
