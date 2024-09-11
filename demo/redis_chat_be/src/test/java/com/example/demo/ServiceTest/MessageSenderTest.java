package com.example.demo.ServiceTest;

import com.example.demo.Dto.MessageDto;
import com.example.demo.Entity.WebSocketChatMessage;
import com.example.demo.Service.MessageSender;
import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.RedisTemplate;

import static ch.qos.logback.core.encoder.ByteArrayUtil.hexStringToByteArray;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class MessageSenderTest {
    @Mock
    private  RedisTemplate<String, String> redisTemplate;
    @InjectMocks
    private MessageSender messageSender;
    @BeforeEach
    private void MessageSenderTest() {
        MockitoAnnotations.openMocks(this);
    }
    @Test
    void sendMessage_Test() {
        String roomId = "123";
        WebSocketChatMessage webSocketChatMessage = new WebSocketChatMessage();
        String byteData1 = "FFD8FFE000104A";
        byte[] fileBytes1 = hexStringToByteArray(byteData1);
        webSocketChatMessage.setType("Chat");
        webSocketChatMessage.setSender("Quan");
        webSocketChatMessage.setContent("Xin chao");
        webSocketChatMessage.setFile(fileBytes1);
        MessageDto messageDto = new MessageDto();
        messageDto.setWebSocketChatMessage(webSocketChatMessage);
        messageDto.setRoomId(roomId);
        Gson gson = new Gson();
        String message = gson.toJson(messageDto);
        System.out.println("Messge >> " + message);
        messageSender.sendMessage(roomId, webSocketChatMessage);
        verify(redisTemplate, times(1)).convertAndSend("xinchao", message);
    }
}
