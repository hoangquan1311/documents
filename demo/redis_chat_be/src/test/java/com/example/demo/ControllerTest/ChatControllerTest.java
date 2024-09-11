package com.example.demo.ControllerTest;

import com.example.demo.Controller.ChatController;
import com.example.demo.Entity.ChatRoom;
import com.example.demo.Entity.WebSocketChatMessage;
import com.example.demo.Repository.ChatRoomRepository;
import com.example.demo.Repository.MessageElasticsearchRepository;
import com.example.demo.Repository.MessageRepository;
import com.example.demo.Service.MessageSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static ch.qos.logback.core.encoder.ByteArrayUtil.hexStringToByteArray;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ChatControllerTest {
    private MockMvc mockMvc;

    @Mock
    private MessageSender messageSender;

    @Mock
    private ChatRoomRepository chatRoomRepository;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private MessageElasticsearchRepository messageElasticsearchRepository;

    @InjectMocks
    private ChatController chatController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(chatController).build();
    }
    @Test
    void getAllRooms_Test() throws Exception {
        List<ChatRoom> chatRooms = new ArrayList<>();
        ChatRoom chatRoom1 = new ChatRoom();
        chatRoom1.setId(1L);
        chatRoom1.setName("Room 1");

        ChatRoom chatRoom2 = new ChatRoom();
        chatRoom2.setId(2L);
        chatRoom2.setName("Room 2");

        chatRooms.add(chatRoom1);
        chatRooms.add(chatRoom2);

        when(chatRoomRepository.findAll()).thenReturn(chatRooms);

        mockMvc.perform(get("/api/rooms"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Room 1"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].name").value("Room 2"));

        verify(chatRoomRepository, times(1)).findAll();
    }
    @Test
    void sendMessage_Test() throws Exception {
        String roomId = "room1";
        String byteData1 = "FFD8FFE000104A";
        byte[] fileBytes1 = hexStringToByteArray(byteData1);
        WebSocketChatMessage message = new WebSocketChatMessage();
        message.setSender("User1");
        message.setContent("Hello World");
        message.setFile(fileBytes1);
        message.setType("Chat");
        chatController.sendMessage(roomId, message);
        verify(messageSender, times(1)).sendMessage(roomId, message);
    }
    @Test
    void createRooms_Test() {
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setName("Room1");
        chatRoom.setId(1L);
        when(chatRoomRepository.save(chatRoom)).thenReturn(chatRoom);
        chatController.createRoom(chatRoom);
        verify(chatRoomRepository, times(1)).save(chatRoom);
    }
    @Test
    void deleteRooms_Test() throws Exception {
        Long id = 1L;
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setId(id);
        chatRoom.setName("Room 1");
        when(chatRoomRepository.findById(id)).thenReturn(Optional.of(chatRoom));
        ResponseEntity<String> response = chatController.deleteRoom(id);

        assertEquals("Delete success " + id, response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(messagingTemplate, times(1)).convertAndSend("/topic/rooms", id);
        mockMvc.perform(delete("/api/rooms/{roomId}", id))
                .andExpect(status().isOk())
                .andExpect(content().string("Delete success " + id));
    }
}
