package com.mlb.mlb_back.Domain.stat.entity;

import com.mlb.mlb_back.Domain.player.entity.Player;
import com.mlb.mlb_back.Domain.team.entity.Team;
import com.mlb.mlb_back.Global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "pitcher_stat", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"player_id", "season", "team_id"})
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PitcherStat extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id", nullable = false)
    private Player player;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @Column(nullable = false)
    private Integer season;

    // 기본 투구 스탯
    @Column(name = "games_played")
    private Integer gamesPlayed = 0;    // 경기수

    @Column(name = "games_started")
    private Integer gamesStarted = 0;   // 선발 경기수

    private Integer wins = 0;           // 승

    private Integer losses = 0;         // 패

    private Integer saves = 0;          // 세이브

    private Integer holds = 0;          // 홀드

    @Column(name = "innings_pitched")
    private Double inningsPitched;      // 이닝 (예: 150.2)

    @Column(name = "strike_outs")
    private Integer strikeOuts = 0;     // 탈삼진

    private Integer walks = 0;          // 볼넷

    @Column(name = "hits_allowed")
    private Integer hitsAllowed = 0;    // 피안타

    @Column(name = "home_runs_allowed")
    private Integer homeRunsAllowed = 0; // 피홈런

    @Column(name = "earned_runs")
    private Integer earnedRuns = 0;     // 자책점

    // 비율 스탯
    private Double era;                 // 평균자책점

    private Double whip;                // WHIP

    @Column(name = "avg_allowed")
    private Double avgAllowed;          // 피안타율

    @Column(name = "strike_out_per_9")
    private Double strikeOutPer9;       // 9이닝당 탈삼진 (K/9)

    @Column(name = "walk_per_9")
    private Double walkPer9;            // 9이닝당 볼넷 (BB/9)
}
