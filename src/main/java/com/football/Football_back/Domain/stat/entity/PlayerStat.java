package com.football.Football_back.Domain.stat.entity;

import com.football.Football_back.Domain.match.entity.Match;
import com.football.Football_back.Domain.player.entity.Player;
import com.football.Football_back.Domain.team.entity.Team;
import com.football.Football_back.Domain.season.entity.Season;
import com.football.Football_back.Global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "player_stat", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"player_id", "match_id"})
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder

public class PlayerStat {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_id", nullable = false)
    private Match match;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id", nullable = false)
    private Player player;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "season_id", nullable = false)
    private Season season;

    // 공통
    @Column(name = "minutes_played")
    private Integer minutesPlayed = 0;

    // 필드 플레이어 요약
    private Integer goals = 0;
    private Integer assists = 0;
    private Integer shots = 0;

    @Column(name = "shots_on_target")
    private Integer shotsOnTarget = 0;

    @Column(name = "fouls_committed")
    private Integer foulsCommitted = 0;

    @Column(name = "yellow_cards")
    private Integer yellowCards = 0;

    @Column(name = "red_cards")
    private Integer redCards = 0;

    // 필드 플레이어 수비 지표
    private Integer tackles = 0;
    private Integer interceptions = 0;

    @Column(name = "aerials_won")
    private Integer aerialsWon = 0;

    private Integer clearances = 0;
    private Integer blocks = 0;

    // 필드 플레이어 공격 지표
    @Column(name = "dribbles_won")
    private Integer dribblesWon = 0;

    // 필드 플레이어 패스 지표
    private Integer passes = 0;

    @Column(name = "pass_accuracy")
    private Double passAccuracy;

    @Column(name = "key_passes")
    private Integer keyPasses = 0;

    private Integer crosses = 0;

    @Column(name = "long_balls")
    private Integer longBalls = 0;

    // 기대득점
    private Double xg = 0.0;
    private Double xa = 0.0;
    private Double xgot = 0.0;

    // 골키퍼
    private Integer saves = 0;

    @Column(name = "save_percentage")
    private Double savePercentage;

    @Column(name = "goals_conceded")
    private Integer goalsConceded = 0;

    private Double xga = 0.0;
    private Double psxg = 0.0;
}
