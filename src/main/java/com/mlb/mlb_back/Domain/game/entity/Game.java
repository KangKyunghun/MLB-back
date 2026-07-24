package com.mlb.mlb_back.Domain.game.entity;

import com.mlb.mlb_back.Domain.team.entity.Team;
import com.mlb.mlb_back.Global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "game")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Game extends BaseEntity {

    @Id
    @Column(name = "id")
    private Long id;           // MLB Stats API 경기 ID (gamePk)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "home_team_id", nullable = false)
    private Team homeTeam;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "away_team_id", nullable = false)
    private Team awayTeam;

    @Column(name = "game_date", nullable = false)
    private Instant gameDate; // 경기 날짜/시간 (UTC 기준 시점. 화면 표시 시 원하는 타임존으로 변환해서 사용)

    @Column(nullable = false)
    private Integer season;    // 시즌 연도 (2024, 2025, 2026)

    @Column(name = "game_type", length = 20)
    private String gameType;   // 정규시즌, 포스트시즌, 디비전, 월드시리즈

    @Column(length = 20)
    private String status;     // Preview, Live, Final, Postponed 등

    @Column(name = "home_score")
    private Integer homeScore;

    @Column(name = "away_score")
    private Integer awayScore;

    @Column(length = 50)
    private String venue;      // 구장명

    @Column(name = "game_number")
    private Integer gameNumber; // 더블헤더 구분 (1, 2)

    @Column(name = "series_description", length = 50)
    private String seriesDescription; // Regular Season, Spring Training 등

    public void updateScore(Integer homeScore, Integer awayScore, String status) {
        this.homeScore = homeScore;
        this.awayScore = awayScore;
        this.status = status;
    }

    public void syncFromApi(Instant gameDate, String status, Integer homeScore, Integer awayScore, String venue) {
    this.gameDate = gameDate;
    this.status = status;
    this.homeScore = homeScore;
    this.awayScore = awayScore;
    this.venue = venue;
    }
}