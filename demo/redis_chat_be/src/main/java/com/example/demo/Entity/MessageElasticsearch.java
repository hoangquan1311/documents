package com.example.demo.Entity;

import jakarta.persistence.*;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Arrays;

@Document(indexName = "search_text")
public class MessageElasticsearch {
    @Id
    private Long id;
    private String roomId;
    private String type;
    private String content;
    private String sender;
    private byte[] file_send;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public byte[] getFile() {
        return file_send;
    }

    public void setFile(byte[] file) {
        this.file_send = file;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    @Override
    public String toString() {
        return "MessageElasticsearch{" +
                "id=" + id +
                ", roomId='" + roomId + '\'' +
                ", type='" + type + '\'' +
                ", content='" + content + '\'' +
                ", sender='" + sender + '\'' +
                ", file_send=" + Arrays.toString(file_send) +
                '}';
    }
}
