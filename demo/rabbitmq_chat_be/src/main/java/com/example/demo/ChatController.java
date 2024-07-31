package com.example.demo;

import com.example.demo.Entity.ChatRoom;
import com.example.demo.MessageSender;
import com.example.demo.Repository.ChatRoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000/")
public class ChatController {

    @Autowired
    private MessageSender messageSender;
    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @MessageMapping("/sendMessage/{roomId}")
    public void sendMessage(@DestinationVariable String roomId ,@Payload WebSocketChatMessage message) {
        messageSender.sendMessage(roomId ,message);
    }
    @MessageMapping("/chatNewUser")
    @SendTo("/topic/messages")
    public WebSocketChatMessage addUser(@Payload WebSocketChatMessage webSocketChatMessage,
                                        SimpMessageHeaderAccessor headerAccessor) {
        headerAccessor.getSessionAttributes().put("username", webSocketChatMessage.getSender());
        return webSocketChatMessage;
    }
    @MessageMapping("/createRoom")
    @SendTo("/topic/rooms")
    public ChatRoom createRoom(@Payload ChatRoom chatRoom) {
        return chatRoomRepository.save(chatRoom);
    }
    @GetMapping("/api/rooms")
    public List<ChatRoom> getAllRooms() {
        return chatRoomRepository.findAll();
    }

}
