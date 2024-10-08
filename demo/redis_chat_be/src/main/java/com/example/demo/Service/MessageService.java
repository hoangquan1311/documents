package com.example.demo.Service;

import co.elastic.clients.elasticsearch._types.aggregations.Aggregation;
import com.example.demo.Entity.Message;
import com.example.demo.Entity.MessageElasticsearch;
import com.example.demo.Repository.MessageElasticsearchRepository;
import com.example.demo.Repository.MessageRepository;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.ScrollableHitSource;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.core.query.SearchTemplateQuery;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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

//    public List<MessageElasticsearch> findByRoomIdAndContentContaining1(String roomId, String keyword) {
//    }
}