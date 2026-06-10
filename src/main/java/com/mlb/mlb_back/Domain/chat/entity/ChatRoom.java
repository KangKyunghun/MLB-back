package com.mlb.mlb_back.Domain.chat.entity;

import com.mlb.mlb_back.Domain.game.entity.Game;
import com.mlb.mlb_back.Global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "chat_room")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ChatRoom extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 경기별 채팅방 1개 (game_id unique)
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", nullable = false, unique = true)
    private Game game;

    /**
     * Live 경기 여부 (Live 배지 표시용 - 채팅 가능 여부와 무관)
     * true  → 현재 진행중인 경기 (Live 배지 표시)
     * false → 예정/종료 경기 (채팅은 동일하게 가능)
     */
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = false;

    /**
     * 채팅방 이름
     * 예: "LAD vs NYY (2024-06-10)"
     * 날짜 포함으로 같은 팀 간 시즌 중 여러 경기 구분
     */
    @Column(name = "room_name", nullable = false, unique = true)
    private String roomName;

    public void activate() {
        this.isActive = true;
    }

    public void deactivate() {
        this.isActive = false;
    }
}
