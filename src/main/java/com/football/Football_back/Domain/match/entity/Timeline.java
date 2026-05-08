package com.football.Football_back.Domain.match.entity;

import com.football.Football_back.Domain.player.entity.Player;
import com.football.Football_back.Domain.team.entity.Team;
import com.football.Football_back.Global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "timeline")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder

public class Timeline extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_id", nullable = false)
    private Match match;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id")
    private Player player;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assist_player_id")
    private Player assistPlayer;   // 어시스트 선수 (골 이벤트)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sub_player_id")
    private Player subPlayer;      // 교체 투입 선수 (교체 이벤트)

    @Column(nullable = false)
    private Integer minute;        // 발생 분

    @Column(name = "extra_minute")
    private Integer extraMinute;   // 추가시간

    @Column(name = "event_type", nullable = false, length = 20)
    private String eventType;      // GOAL / YELLOW_CARD / RED_CARD / SUBSTITUTION

}
