package com.example.demo.ServiceTest;

import com.example.demo.Dto.MessageDto;
import com.example.demo.Entity.Message;
import com.example.demo.Entity.WebSocketChatMessage;
import com.example.demo.Repository.MessageRepository;
import com.example.demo.Service.MessageReceiver;
import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class MessageReceiverTest {

    @Mock
    private SimpMessagingTemplate template;

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private MessageRepository messageRepository;

    @InjectMocks
    private MessageReceiver messageReceiver;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testReceiveMessage() {
        Gson gson = new Gson();
        String jsonMessage = "{\"roomId\":\"123\",\"webSocketChatMessage\":{\"content\":\"Hello\",\"type\":\"Chat\",\"sender\":\"User\",\"file\":\"fileData\"}}";
        MessageDto messageDto = gson.fromJson(jsonMessage, MessageDto.class);
        String roomId = messageDto.getRoomId();
        WebSocketChatMessage webSocketChatMessage = messageDto.getWebSocketChatMessage();

        Message message = new Message();
        message.setContent(webSocketChatMessage.getContent());
        message.setRoomId(roomId);
        message.setType(webSocketChatMessage.getType());
        message.setFile(webSocketChatMessage.getFile());
        message.setSender(webSocketChatMessage.getSender());

        when(messageRepository.save(any(Message.class))).thenReturn(message);

        messageReceiver.receiveMessage(jsonMessage);

        verify(messageRepository, times(1)).save(any(Message.class));
        verify(template, times(1)).convertAndSend(eq("/topic/" + roomId + "/messages"), any(WebSocketChatMessage.class));
    }
}
