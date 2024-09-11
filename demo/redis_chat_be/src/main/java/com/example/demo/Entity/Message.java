package com.example.demo.Entity;

import jakarta.persistence.*;

import java.util.Arrays;

@Entity
@Table(name = "message")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "message_seq")
    @SequenceGenerator(name = "message_seq", sequenceName = "message_seq", allocationSize = 1)
    private Long id;
    @Column(name = "roomId")
    private String roomId;
    @Column(name = "type")
    private String type;
    @Column(name = "content")
    private String content;
    @Column(name = "sender")
    private String sender;
    @Column(name = "file_send")
    @Lob
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
        return "Message{" +
                "id=" + id +
                ", roomId='" + roomId + '\'' +
                ", type='" + type + '\'' +
                ", content='" + content + '\'' +
                ", sender='" + sender + '\'' +
                ", file_send=" + Arrays.toString(file_send) +
                '}';
    }
}
