package com.example.demo;

import com.example.demo.Entity.ChatRoom;
import com.example.demo.Repository.ChatRoomRepository;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "http://localhost:3000/")
public class ChatController {

    @Autowired
    private MessageSender messageSender;
    @Autowired
    private ChatRoomRepository chatRoomRepository;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

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
    @GetMapping("/api/rooms/{roomId}/messages")
    public List<WebSocketChatMessage> getMessages(@PathVariable String roomId) {
        Gson gson = new Gson();
        List<String> messagesJson = redisTemplate.opsForList().range(roomId, 0, -1);
        return messagesJson.stream().map(json -> gson.fromJson(json, WebSocketChatMessage.class)).collect(Collectors.toList());
    }
}
