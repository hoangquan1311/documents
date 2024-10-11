package com.example.demo.Service;

import com.example.demo.Entity.Message;
import com.example.demo.Entity.MessageElasticsearch;
import com.example.demo.Entity.WebSocketChatMessage;
import com.example.demo.Repository.ChatRoomRepository;
import com.example.demo.Repository.MessageElasticsearchRepository;
import com.example.demo.Repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MessageService {

    @Autowired
    private MessageElasticsearchRepository messageElasticsearchRepository;
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private ChatRoomRepository chatRoomRepository;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public void syncMessagesToElasticsearch() {
        List<Message> messages = messageRepository.findAll();
        List<MessageElasticsearch> esMessages = messages.stream()
                .map(message -> {
                    MessageElasticsearch esMessage = new MessageElasticsearch();
                    esMessage.setId(message.getId());
                    esMessage.setRoomId(message.getRoomId());
                    esMessage.setType(message.getType());
                    esMessage.setContent(message.getContent());
                    esMessage.setSender(message.getSender());
                    esMessage.setFile(message.getFile());
                    return esMessage;
                })
                .collect(Collectors.toList());
        messageElasticsearchRepository.saveAll(esMessages);

    }

    public ResponseEntity<String> deleteRoom(Long roomId) {
        return chatRoomRepository.findById(roomId).map(room -> {
            chatRoomRepository.delete(room);
            messageRepository.deleteByRoomId(new String(String.valueOf(roomId)));
            messagingTemplate.convertAndSend("/topic/rooms", roomId);
            return ResponseEntity.ok("Delete success " + roomId);
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }


    public List<WebSocketChatMessage> getMessages(String roomId) {
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

    public List<WebSocketChatMessage> searchMessage(String roomId, String keyword) {
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
    public WebSocketChatMessage addUser(String roomId,WebSocketChatMessage webSocketChatMessage,
                                        SimpMessageHeaderAccessor headerAccessor) {
        headerAccessor.getSessionAttributes().put("username", webSocketChatMessage.getSender());
        headerAccessor.getSessionAttributes().put("roomId", roomId);
        return webSocketChatMessage;
    }
}