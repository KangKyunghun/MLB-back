package com.mlb.mlb_back.Domain.game.entity;

import com.mlb.mlb_back.Domain.player.entity.Player;
import com.mlb.mlb_back.Domain.team.entity.Team;
import com.mlb.mlb_back.Global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "box_score", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"game_id", "player_id"})
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class BoxScore extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id", nullable = false)
    private Player player;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @Column(name = "player_type", length = 10, nullable = false)
    private String playerType;  // BATTER / PITCHER

    // 타자 기록
    @Column(name = "at_bats")
    private Integer atBats;

    private Integer hits;

    @Column(name = "home_runs")
    private Integer homeRuns;

    @Column(name = "rbi")
    private Integer rbi;

    private Integer runs;

    private Integer walks;

    @Column(name = "strike_outs")
    private Integer strikeOuts;

    @Column(name = "batting_order")
    private Integer battingOrder; // 타순

    // 투수 기록
    @Column(name = "innings_pitched")
    private Double inningsPitched;

    @Column(name = "earned_runs")
    private Integer earnedRuns;

    @Column(name = "hits_allowed")
    private Integer hitsAllowed;

    @Column(name = "walks_allowed")
    private Integer walksAllowed;

    @Column(name = "strike_outs_pitched")
    private Integer strikeOutsPitched;

    @Column(name = "pitch_count")
    private Integer pitchCount;  // 투구수

    @Column(name = "is_win")
    private Boolean isWin;       // 승리 투수 여부

    @Column(name = "is_loss")
    private Boolean isLoss;      // 패전 투수 여부

    @Column(name = "is_save")
    private Boolean isSave;      // 세이브 여부
}
