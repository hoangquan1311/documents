package com.example.demo.Entity;

import java.util.Arrays;

public class WebSocketChatMessage {
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

    @Override
    public String toString() {
        return "WebSocketChatMessage{" +
                "type='" + type + '\'' +
                ", content='" + content + '\'' +
                ", sender='" + sender + '\'' +
                ", file_send=" + Arrays.toString(file_send) +
                '}';
    }
}
