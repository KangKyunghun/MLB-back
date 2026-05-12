package com.mlb.mlb_back.Domain.stat.entity;

import com.mlb.mlb_back.Domain.game.entity.Game;
import com.mlb.mlb_back.Domain.player.entity.Player;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "pitch_data")
public class PitchData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 경기
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id")
    private Game game;

    // 투수
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pitcher_id")
    private Player pitcher;

    // 타자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "batter_id")
    private Player batter;

    // 구종 (FF, SL, CH 등)
    @Column(nullable = false)
    private String pitchType;

    // 구속 (mph)
    private Double velocity;

    // 회전수 (rpm)
    private Integer spinRate;

    // 스트라이크존 위치
    private Double plateX;
    private Double plateZ;

    // 결과
    private String result;

    // 타구 속도
    private Double exitVelocity;

    // 발사각
    private Double launchAngle;

    // 비거리
    private Double distance;

    // 이닝
    private Integer inning;

    // 볼카운트
    private Integer balls;
    private Integer strikes;

    // 아웃카운트
    private Integer outs;

    // 득점 여부
    private Boolean scoringPlay;
}