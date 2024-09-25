package com.example.demo.Controller;

import com.example.demo.Entity.ChatRoom;
import com.example.demo.Entity.Message;
import com.example.demo.Entity.MessageElasticsearch;
import com.example.demo.Repository.MessageElasticsearchRepository;
import com.example.demo.Repository.MessageRepository;
import com.example.demo.Service.MessageSender;
import com.example.demo.Repository.ChatRoomRepository;
import com.example.demo.Entity.WebSocketChatMessage;
import com.example.demo.Service.MessageService;
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

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "http://localhost:3000/")
public class ChatController {

    @Autowired
    private MessageSender messageSender; //checkout xóa
    @Autowired
    private ChatRoomRepository chatRoomRepository;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private MessageElasticsearchRepository messageElasticsearchRepository;
    @Autowired
    private MessageService messageService;

    @MessageMapping("/sendMessage/{roomId}")
    public void sendMessage(@DestinationVariable String roomId, @Payload WebSocketChatMessage message) {
        messageSender.sendMessage(roomId, message);
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
            messageRepository.deleteByRoomId(new String(String.valueOf(roomId)));
            messagingTemplate.convertAndSend("/topic/rooms", roomId);
            return ResponseEntity.ok("Delete success " + roomId);
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/api/rooms/{roomId}/messages")
    public List<WebSocketChatMessage> getMessages(@PathVariable String roomId) {
        List<Message> messages = messageRepository.findByRoomId(roomId);
        return messages.stream()
                .map(message -> {
                    WebSocketChatMessage chatMessage = new WebSocketChatMessage();
                    chatMessage.setType(message.getType());
                    chatMessage.setContent(message.getContent());
                    chatMessage.setSender(message.getSender());
                    chatMessage.setFile(message.getFile());
                    return chatMessage;
                })
                .collect(Collectors.toList());
    }

    @GetMapping("/api/rooms/{roomId}/search-messages")
    public List<WebSocketChatMessage> searchMessage(@PathVariable String roomId, @RequestParam String keyword) {
        List<MessageElasticsearch> elasticsearchMessages = messageElasticsearchRepository.findByRoomIdAndContentContaining(roomId, keyword);

        return elasticsearchMessages.stream()
                .map(message -> {
                    WebSocketChatMessage chatMessage = new WebSocketChatMessage();
                    chatMessage.setType(message.getType());
                    chatMessage.setContent(message.getContent());
                    chatMessage.setSender(message.getSender());
                    chatMessage.setFile(message.getFile());
                    return chatMessage;
                })
                .collect(Collectors.toList());
    }


}