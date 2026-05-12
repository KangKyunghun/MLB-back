package com.mlb.mlb_back.Domain.stat.entity;

import com.mlb.mlb_back.Domain.player.entity.Player;
import com.mlb.mlb_back.Domain.team.entity.Team;
import com.mlb.mlb_back.Global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "batter_stat", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"player_id", "season", "team_id"})
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class BatterStat extends BaseEntity {

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
    private Integer season;    // 시즌 연도

    // 기본 타격 스탯
    @Column(name = "games_played")
    private Integer gamesPlayed = 0;    // 경기수

    @Column(name = "at_bats")
    private Integer atBats = 0;          // 타수

    private Integer hits = 0;            // 안타

    private Integer doubles = 0;         // 2루타

    private Integer triples = 0;         // 3루타

    @Column(name = "home_runs")
    private Integer homeRuns = 0;        // 홈런

    @Column(name = "rbi")
    private Integer rbi = 0;             // 타점

    private Integer runs = 0;            // 득점

    @Column(name = "stolen_bases")
    private Integer stolenBases = 0;     // 도루

    private Integer walks = 0;           // 볼넷

    @Column(name = "strike_outs")
    private Integer strikeOuts = 0;      // 삼진

    @Column(name = "hit_by_pitch")
    private Integer hitByPitch = 0;      // 사구

    // 비율 스탯
    @Column(name = "avg", precision = 5)
    private Double avg;                  // 타율

    @Column(name = "obp", precision = 5)
    private Double obp;                  // 출루율

    @Column(name = "slg", precision = 5)
    private Double slg;                  // 장타율

    @Column(name = "ops", precision = 5)
    private Double ops;                  // OPS
}
