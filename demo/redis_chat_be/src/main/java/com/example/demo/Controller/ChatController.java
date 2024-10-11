package com.example.demo.Controller;

import com.example.demo.Entity.*;
import com.example.demo.Service.MessageSender;
import com.example.demo.Repository.ChatRoomRepository;
import com.example.demo.Service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
    @Autowired
    private MessageService messageService;

    @MessageMapping("/sendMessage/{roomId}")
    public void sendMessage(@DestinationVariable String roomId, @Payload WebSocketChatMessage message) {
        messageSender.sendMessage(roomId, message);
    }
    @MessageMapping("/signal/{roomId}")
    @SendTo("/topic/{roomId}/signal")
    public SignalMessage signaling(@DestinationVariable String roomId, @Payload SignalMessage signalMessage) {
        return signalMessage;
    }

    @MessageMapping("/chatNewUser/{roomId}")
    @SendTo("/topic/{roomId}/messages")
    public WebSocketChatMessage addUser(@DestinationVariable String roomId, @Payload WebSocketChatMessage webSocketChatMessage,
                                        SimpMessageHeaderAccessor headerAccessor) {
        return messageService.addUser(roomId, webSocketChatMessage, headerAccessor);
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
        return messageService.deleteRoom(roomId);
    }

    @GetMapping("/api/rooms/{roomId}/messages")
    public List<WebSocketChatMessage> getMessages(@PathVariable String roomId) {
        return messageService.getMessages(roomId);
    }

    @GetMapping("/api/rooms/{roomId}/search-messages")
    public List<WebSocketChatMessage> searchMessage(@PathVariable String roomId, @RequestParam String keyword) {
        return messageService.searchMessage(roomId, keyword);
    }
}