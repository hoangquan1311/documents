package com.example.demo.Repository;

import com.example.demo.Entity.MessageElasticsearch;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface MessageElasticsearchRepository extends ElasticsearchRepository<MessageElasticsearch, Long> {
    List<MessageElasticsearch> findByRoomIdAndContentContaining(String roomId, String keyword);

}
