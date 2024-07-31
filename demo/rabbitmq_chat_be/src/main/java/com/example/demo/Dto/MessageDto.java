package com.example.demo.Dto;

import com.example.demo.WebSocketChatMessage;

public class MessageDto {
    private String roomId;
    private WebSocketChatMessage webSocketChatMessage;

    public MessageDto() {
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public WebSocketChatMessage getWebSocketChatMessage() {
        return webSocketChatMessage;
    }

    public void setWebSocketChatMessage(WebSocketChatMessage webSocketChatMessage) {
        this.webSocketChatMessage = webSocketChatMessage;
    }
}
