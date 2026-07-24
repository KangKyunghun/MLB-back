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

    @Column(name = "appearance_order")
    private Integer appearanceOrder; // 투수 등판 순서 (MLB API teams.{side}.pitchers 배열 인덱스 기준)

    @Column(name = "game_position", length = 10)
    private String gamePosition; // 그 경기에서 뛴 수비 포지션 (P, C, 1B, 2B, 3B, SS, LF, CF, RF, DH 등) — MLB API boxscore의 player.position.abbreviation

    /** 지연 백필용 — gamePosition만 채워 넣을 때 사용 */
    public void updateGamePosition(String gamePosition) {
        this.gamePosition = gamePosition;
    }

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

    @Column(name = "is_hold")
    private Boolean isHold;      // 홀드 여부

    @Column(name = "is_save")
    private Boolean isSave;      // 세이브 여부
}