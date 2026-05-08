package com.football.Football_back.Domain.community.entity;

import com.football.Football_back.Domain.match.entity.Match;
import com.football.Football_back.Global.common.BaseEntity;
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

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_id", nullable = false, unique = true)
    private Match match;

    @Column(name = "is_active")
    private Boolean isActive = false;  // TRUE: 경기중 / FALSE: 종료

    // 채팅방 활성화 (경기 시작)
    public void activate() {
        this.isActive = true;
    }

    // 채팅방 비활성화 (경기 종료)
    public void deactivate() {
        this.isActive = false;
    }
}