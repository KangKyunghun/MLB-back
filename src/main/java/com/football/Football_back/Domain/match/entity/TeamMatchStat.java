package com.football.Football_back.Domain.match.entity;

import com.football.Football_back.Domain.team.entity.Team;
import com.football.Football_back.Global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "team_match_stat", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"match_id", "team_id"})
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder   

public class TeamMatchStat extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_id", nullable = false)
    private Match match;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    private Integer goals = 0;
    private Integer shots = 0;              // 총 슈팅

    @Column(name = "shots_on_target")
    private Integer shotsOnTarget = 0;      // 유효 슈팅

    private Double possession;              // 점유율 (%)
    private Integer passes = 0;             // 패스 횟수

    @Column(name = "pass_accuracy")
    private Double passAccuracy;            // 패스 성공률 (%)

    private Integer offsides = 0;

    @Column(name = "yellow_cards")
    private Integer yellowCards = 0;

    @Column(name = "red_cards")
    private Integer redCards = 0;

    private Integer corners = 0;            // 코너킥
    private Integer fouls = 0;

}
