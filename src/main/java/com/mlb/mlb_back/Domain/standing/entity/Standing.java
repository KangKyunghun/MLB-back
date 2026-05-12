package com.mlb.mlb_back.Domain.standing.entity;

import com.mlb.mlb_back.Domain.team.entity.Team;
import com.mlb.mlb_back.Global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "standing", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"team_id", "season"})
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Standing extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @Column(nullable = false)
    private Integer season;

    @Column(name = "division_rank")
    private Integer divisionRank;   // 지구 순위

    @Column(name = "league_rank")
    private Integer leagueRank;     // 리그 순위

    private Integer wins;           // 승

    private Integer losses;         // 패

    @Column(name = "win_pct")
    private Double winPct;          // 승률

    @Column(name = "games_back")
    private Double gamesBack;       // 게임차

    @Column(name = "runs_scored")
    private Integer runsScored;     // 득점

    @Column(name = "runs_allowed")
    private Integer runsAllowed;    // 실점

    @Column(name = "run_differential")
    private Integer runDifferential; // 득실차

    @Column(name = "last_ten_wins")
    private Integer lastTenWins;    // 최근 10경기 승

    @Column(name = "last_ten_losses")
    private Integer lastTenLosses;  // 최근 10경기 패

    @Column(name = "streak", length = 10)
    private String streak;          // 연승/연패 (예: W3, L2)

    public void update(Integer wins, Integer losses, Double winPct, Double gamesBack,
                       Integer divisionRank, Integer leagueRank, String streak,
                       Integer lastTenWins, Integer lastTenLosses) {
        this.wins = wins;
        this.losses = losses;
        this.winPct = winPct;
        this.gamesBack = gamesBack;
        this.divisionRank = divisionRank;
        this.leagueRank = leagueRank;
        this.streak = streak;
        this.lastTenWins = lastTenWins;
        this.lastTenLosses = lastTenLosses;
    }
}
