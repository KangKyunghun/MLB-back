package com.mlb.mlb_back.Domain.chat.dto;

import com.mlb.mlb_back.Domain.chat.entity.ChatRoom;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChatRoomResponse {

    private Long id;
    private Long gameId;
    private String roomName;
    private Boolean isActive;

    public static ChatRoomResponse from(ChatRoom chatRoom) {
        return ChatRoomResponse.builder()
                .id(chatRoom.getId())
                .gameId(chatRoom.getGame().getId())
                .roomName(chatRoom.getRoomName())
                .isActive(chatRoom.getIsActive())
                .build();
    }
}