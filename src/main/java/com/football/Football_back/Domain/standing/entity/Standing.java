package com.football.Football_back.Domain.standing.entity;

import com.football.Football_back.Domain.league.entity.League;
import com.football.Football_back.Domain.season.entity.Season;
import com.football.Football_back.Domain.team.entity.Team;
import com.football.Football_back.Global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "standing", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"league_id", "season_id", "team_id"})
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder

public class Standing {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "league_id", nullable = false)
    private League league;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "season_id", nullable = false)
    private Season season;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @Column(nullable = false)
    private Integer rank;          // 순위

    private Integer played = 0;    // 경기수
    private Integer won = 0;
    private Integer drawn = 0;
    private Integer lost = 0;

    @Column(name = "goals_for")
    private Integer goalsFor = 0;      // 득점

    @Column(name = "goals_against")
    private Integer goalsAgainst = 0;  // 실점

    @Column(name = "goal_diff")
    private Integer goalDiff = 0;      // 득실차

    private Integer points = 0;        // 승점

    @Column(length = 20)
    private String form;               // 최근 5경기 (예: WWDLW)

}
