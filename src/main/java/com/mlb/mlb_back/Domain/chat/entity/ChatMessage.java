package com.mlb.mlb_back.Domain.chat.entity;

import com.mlb.mlb_back.Domain.user.entity.User;
import com.mlb.mlb_back.Global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "chat_message")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ChatMessage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id", nullable = false)
    private ChatRoom chatRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;         // null이면 시스템 메시지

    @Column(nullable = false, length = 500)
    private String content;

    @Column(name = "message_type", length = 20)
    private String messageType; // CHAT, SYSTEM (득점 알림 등)
}
