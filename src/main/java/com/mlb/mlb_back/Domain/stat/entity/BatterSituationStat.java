package com.mlb.mlb_back.Domain.stat.entity;

import com.mlb.mlb_back.Domain.player.entity.Player;
import com.mlb.mlb_back.Global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "batter_situation_stat", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"player_id", "season", "sit_code", "game_type"})
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class BatterSituationStat extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id", nullable = false)
    private Player player;

    @Column(nullable = false)
    private Integer season;

    /**
     * 상황 코드 — MLB Stats API split.code 값
     *   risp        : 득점권 (2루 또는 3루에 주자)
     *   bases_loaded: 만루
     *   two_outs    : 2아웃
     *   late_close  : 7회 이후 1점차 이내
     *   (이후 API 확인 후 추가 가능)
     */
    /**
     * R=정규시즌 / PS=포스트시즌전체 (와일드카드~월드시리즈 합산)
     */
    @Column(name = "game_type", nullable = false, length = 2)
    private String gameType;

    @Column(name = "sit_code", nullable = false, length = 30)
    private String sitCode;

    /** 상황 설명 — API split.description (예: "Scoring Position") */
    @Column(name = "sit_description", length = 50)
    private String sitDescription;

    // ── 비율 스탯 ──────────────────────────────
    /** 타율 */
    @Column(precision = 5)
    private Double avg;

    /** 출루율 */
    @Column(precision = 5)
    private Double obp;

    /** 장타율 */
    @Column(precision = 5)
    private Double slg;

    /** OPS (출루율 + 장타율) */
    @Column(precision = 5)
    private Double ops;

    /** BABIP (인플레이 타율) */
    @Column(precision = 5)
    private Double babip;

    // ── 기본 스탯 ─────────────────────────────
    @Column(name = "plate_appearances")
    private Integer plateAppearances;

    @Column(name = "at_bats")
    private Integer atBats;

    private Integer hits;

    private Integer doubles;

    private Integer triples;

    @Column(name = "home_runs")
    private Integer homeRuns;

    private Integer rbi;

    // ── 볼넷 / 삼진 ───────────────────────────
    @Column(name = "strike_outs")
    private Integer strikeOuts;

    @Column(name = "base_on_balls")
    private Integer baseOnBalls;

    @Column(name = "intentional_walks")
    private Integer intentionalWalks;

    // ── 기타 ──────────────────────────────────
    @Column(name = "ground_into_double_play")
    private Integer groundIntoDoublePlay;

    @Column(name = "total_bases")
    private Integer totalBases;

    @Column(name = "left_on_base")
    private Integer leftOnBase;

    @Column(name = "games_played")
    private Integer gamesPlayed;
}