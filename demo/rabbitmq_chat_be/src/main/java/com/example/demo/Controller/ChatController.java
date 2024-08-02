package com.example.demo.Controller;

import com.example.demo.Entity.ChatRoom;
import com.example.demo.Serivce.MessageSender;
import com.example.demo.Repository.ChatRoomRepository;
import com.example.demo.Entity.WebSocketChatMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000/")
public class ChatController {

    @Autowired
    private MessageSender messageSender;
    @Autowired
    private ChatRoomRepository chatRoomRepository;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/sendMessage/{roomId}")
    public void sendMessage(@DestinationVariable String roomId ,@Payload WebSocketChatMessage message) {
        messageSender.sendMessage(roomId ,message);
    }
    @MessageMapping("/chatNewUser/{roomId}")
    @SendTo("/topic/{roomId}/messages")
    public WebSocketChatMessage addUser(@DestinationVariable String roomId, @Payload WebSocketChatMessage webSocketChatMessage,
                                        SimpMessageHeaderAccessor headerAccessor) {
        headerAccessor.getSessionAttributes().put("username", webSocketChatMessage.getSender());
        headerAccessor.getSessionAttributes().put("roomId", roomId);
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
    @DeleteMapping("/api/rooms/{roomId}")
    public ResponseEntity<String> deleteRoom(@PathVariable Long roomId) {
        return chatRoomRepository.findById(roomId).map(room -> {
            chatRoomRepository.delete(room);
            messagingTemplate.convertAndSend("/topic/rooms", roomId);
            return ResponseEntity.ok("Delete success " + roomId);
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
