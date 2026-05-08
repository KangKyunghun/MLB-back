package com.football.Football_back.Domain.match.entity;

import com.football.Football_back.Domain.league.entity.League;
import com.football.Football_back.Domain.season.entity.Season;
import com.football.Football_back.Domain.team.entity.Team;
import com.football.Football_back.Global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "match")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder

public class Match extends BaseEntity {

    @Id
    @Column(name = "id")
    private Long id; // football-data.org에서 제공하는 경기 ID 사용

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "league_id", nullable = false)
    private League league; // 경기가 속한 리그

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "season_id", nullable = false)
    private Season season; // 경기가 속한 시즌

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "home_team_id", nullable = false)
    private Team homeTeam; // 홈 팀

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "away_team_id", nullable = false)
    private Team awayTeam; // 원정 팀

    @Column(name = "match_date")
    private LocalDateTime matchDate; // 경기 날짜 및 시간

    private Integer matchday; // 경기 라운드

    @Column(length = 20)
    private String status; // 경기 상태 (SCHEDULED, LIVE, FINISHED 등)

    @Column(name = "home_score")
    private Integer homeScore; // 홈 팀 점수

    @Column(name = "away_score")
    private Integer awayScore; // 원정 팀 점수

}
