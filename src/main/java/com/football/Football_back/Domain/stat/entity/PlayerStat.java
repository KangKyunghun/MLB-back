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

    private Double rating;

    // 필드 플레이어 요약
    private Integer goals = 0;
    private Integer assists = 0;
    private Integer shots = 0;

    @Column(name = "shots_on_target")
    private Integer shotsOnTarget = 0;      // 유효 슈팅

    @Column(name = "fouls_committed")
    private Integer foulsCommitted = 0;     // 파울

    @Column(name = "fouls_drawn")
    private Integer foulsDrawn = 0;         // 파울 유도

    @Column(name = "yellow_cards")
    private Integer yellowCards = 0;

    @Column(name = "red_cards")
    private Integer redCards = 0;

    // 필드 플레이어 수비 지표
    private Integer tackles = 0;
    private Integer interceptions = 0;

    @Column(name = "aerials_won")
    private Integer aerialsWon = 0;         // 공중볼 승리

    private Integer clearances = 0;         // 클리어런스
    private Integer blocks = 0;             // 블로킹

    // 필드 플레이어 공격 지표
    @Column(name = "dribbles_won")
    private Integer dribblesWon = 0;        // 드리블 성공

    @Column(name = "big_chance_missed")
    private Integer bigChanceMissed = 0;    // 빅찬스 미스

    // 필드 플레이어 패스 지표
    private Integer passes = 0;

    @Column(name = "pass_accuracy")
    private Double passAccuracy;            // 패스 성공률 (%)

    @Column(name = "key_passes")
    private Integer keyPasses = 0;         // 키패스

    private Integer crosses = 0;           // 크로스

    @Column(name = "long_balls")
    private Integer longBalls = 0;         // 롱볼

    @Column(name = "through_balls")
    private Integer throughBalls = 0;      // 스루볼

    // 기대득점
    private Double xg = 0.0;              // 기대 득점
    private Double xa = 0.0;              // 기대 어시스트
    private Double xgot = 0.0;            // 유효슈팅 기대득점

    // 골키퍼
    private Integer saves = 0;            // 선방 횟수

    @Column(name = "save_percentage")
    private Double savePercentage;        // 선방률 (%)

    @Column(name = "goals_conceded")
    private Integer goalsConceded = 0;    // 실점

    private Integer punches = 0;          // 펀칭
    private Integer claims = 0;           // 클레임
    private Double xga = 0.0;            // 기대 실점
    private Double psxg = 0.0;           // 포스트샷 기대실점

}
