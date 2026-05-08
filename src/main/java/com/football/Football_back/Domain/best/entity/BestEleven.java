package com.football.Football_back.Domain.best.entity;

import com.football.Football_back.Domain.league.entity.League;
import com.football.Football_back.Domain.season.entity.Season;
import com.football.Football_back.Domain.team.entity.Team;
import com.football.Football_back.Global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "best_eleven", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"league_id", "season_id", "team_id", "type"})
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class BestEleven extends BaseEntity {

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
    @JoinColumn(name = "team_id")
    private Team team;             // NULL: 리그 베스트 11 / 값 있음: 팀 베스트 11

    @Column(length = 20)
    private String formation;      // 예: 4-3-3

    @Column(nullable = false, length = 20)
    private String type;           // LEAGUE / TEAM

    @OneToMany(mappedBy = "bestEleven", cascade = CascadeType.ALL)
    @Builder.Default
    private List<BestElevenPlayer> players = new ArrayList<>();
}
