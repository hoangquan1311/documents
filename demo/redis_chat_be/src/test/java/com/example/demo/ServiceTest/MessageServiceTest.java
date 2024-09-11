package com.example.demo.ServiceTest;

import com.example.demo.Entity.Message;
import com.example.demo.Entity.MessageElasticsearch;
import com.example.demo.Repository.MessageElasticsearchRepository;
import com.example.demo.Repository.MessageRepository;
import com.example.demo.Service.MessageService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static ch.qos.logback.core.encoder.ByteArrayUtil.hexStringToByteArray;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

public class MessageServiceTest {

    @InjectMocks
    private MessageService messageService;

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private MessageElasticsearchRepository messageElasticsearchRepository;

    public MessageServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void syncMessagesToElasticsearch_Test() {
        List<Message> messageList = new ArrayList<>();
        String byteData1 = "FFD8FFE000104A";
        byte[] fileBytes1 = hexStringToByteArray(byteData1);
        String byteData2 = "FFD8FFE0001102";
        byte[] fileBytes2 = hexStringToByteArray(byteData2);

        Message message1 = new Message();
        message1.setRoomId("12345");
        message1.setType("Chat");
        message1.setContent("Đang gửi tệp ảnh 1");
        message1.setSender("Người dùng 1");
        message1.setFile(fileBytes1);

        Message message2 = new Message();
        message2.setRoomId("1234567");
        message2.setType("Chat");
        message2.setContent("Đang gửi tệp ảnh 2");
        message2.setSender("Người dùng 2");
        message2.setFile(fileBytes2);

        messageList.add(message1);
        messageList.add(message2);
        messageList.forEach(message -> System.out.println(messageList));
        when(messageRepository.findAll()).thenReturn(messageList);

        List<MessageElasticsearch> esMessages = new ArrayList<>();
        MessageElasticsearch esMessage1 = new MessageElasticsearch();
        esMessage1.setId(message1.getId());
        esMessage1.setRoomId(message1.getRoomId());
        esMessage1.setType(message1.getType());
        esMessage1.setContent(message1.getContent());
        esMessage1.setSender(message1.getSender());
        esMessage1.setFile(message1.getFile());

        MessageElasticsearch esMessage2 = new MessageElasticsearch();
        esMessage2.setId(message2.getId());
        esMessage2.setRoomId(message2.getRoomId());
        esMessage2.setType(message2.getType());
        esMessage2.setContent(message2.getContent());
        esMessage2.setSender(message2.getSender());
        esMessage2.setFile(message2.getFile());

        esMessages.add(esMessage1);
        esMessages.add(esMessage2);
        esMessages.forEach(messageElasticsearch -> System.out.println(messageElasticsearch));
        when(messageElasticsearchRepository.saveAll(anyList())).thenReturn(esMessages);

        messageService.syncMessagesToElasticsearch();

        verify(messageRepository, times(1)).findAll();
        verify(messageElasticsearchRepository, times(1)).saveAll(anyList());
    }

}
