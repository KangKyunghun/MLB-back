package com.mlb.mlb_back.Domain.stat.entity;

import com.mlb.mlb_back.Domain.player.entity.Player;
import com.mlb.mlb_back.Global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * 선수 월별 타격 스탯
 * API: /people/{id}/stats?stats=byMonth&season={season}&group=hitting
 * split.month = 3(3월)~10(10월)
 */
@Entity
@Table(name = "player_monthly_stat", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"player_id", "season", "month"})
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PlayerMonthlyStat extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id", nullable = false)
    private Player player;

    @Column(nullable = false)
    private Integer season;

    /** 월 (3=3월, 4=4월 … 10=10월) */
    @Column(nullable = false)
    private Integer month;

    @Column(name = "games_played")
    private Integer gamesPlayed;

    @Column(name = "plate_appearances")
    private Integer plateAppearances;

    @Column(name = "at_bats")
    private Integer atBats;

    private Integer hits;
    private Integer doubles;
    private Integer triples;

    @Column(name = "home_runs")
    private Integer homeRuns;

    private Integer runs;
    private Integer rbi;

    @Column(name = "strike_outs")
    private Integer strikeOuts;

    @Column(name = "base_on_balls")
    private Integer baseOnBalls;

    @Column(name = "intentional_walks")
    private Integer intentionalWalks;

    @Column(name = "stolen_bases")
    private Integer stolenBases;

    @Column(name = "caught_stealing")
    private Integer caughtStealing;

    @Column(name = "ground_into_double_play")
    private Integer groundIntoDoublePlay;

    @Column(name = "total_bases")
    private Integer totalBases;

    @Column(name = "left_on_base")
    private Integer leftOnBase;

    // 비율 스탯
    private Double avg;
    private Double obp;
    private Double slg;
    private Double ops;
    private Double babip;

    @Column(name = "stolen_base_percentage")
    private Double stolenBasePercentage;
}
