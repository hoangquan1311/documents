package com.example.demo.Service;

import com.example.demo.Entity.Message;
import com.example.demo.Entity.MessageElasticsearch;
import com.example.demo.Repository.MessageElasticsearchRepository;
import com.example.demo.Repository.MessageRepository;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.data.jpa.repository.Query;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MessageService {
    private final MessageElasticsearchRepository messageElasticsearchRepository;
    private final MessageRepository messageRepository;

    public MessageService(MessageElasticsearchRepository messageElasticsearchRepository, MessageRepository messageRepository) {
        this.messageElasticsearchRepository = messageElasticsearchRepository;
        this.messageRepository = messageRepository;
    }
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
}
