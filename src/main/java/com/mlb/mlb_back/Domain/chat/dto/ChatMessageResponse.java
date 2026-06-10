package com.mlb.mlb_back.Domain.chat.dto;

import com.mlb.mlb_back.Domain.chat.entity.ChatMessage;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ChatMessageResponse {

    private Long id;
    private Long chatRoomId;
    private Long userId;
    private String nickname;
    private String content;
    private String messageType;     // CHAT, SYSTEM
    private LocalDateTime createdAt;

    public static ChatMessageResponse from(ChatMessage message) {
        return ChatMessageResponse.builder()
                .id(message.getId())
                .chatRoomId(message.getChatRoom().getId())
                .userId(message.getUser() != null ? message.getUser().getId() : null)
                .nickname(message.getUser() != null ? message.getUser().getNickname() : "시스템")
                .content(message.getContent())
                .messageType(message.getMessageType())
                .createdAt(message.getCreatedAt())
                .build();
    }
}