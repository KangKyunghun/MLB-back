package com.mlb.mlb_back.Domain.stat.entity;

import com.mlb.mlb_back.Domain.player.entity.Player;
import com.mlb.mlb_back.Global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * 타자 vs 투수 상대전적
 *
 * MLB Stats API vsPlayer 엔드포인트가 현재 빈 배열을 반환하므로
 * pitch_data 테이블을 집계해서 저장합니다.
 *
 * 집계 기준: pitch_data에서 (batter_id, pitcher_id, season) 그룹별
 *   - 타수: result IN ('single','double','triple','home_run','field_out','strikeout','grounded_into_double_play','pop_out','fly_out','line_out')
 *   - 안타: result IN ('single','double','triple','home_run')
 *   - 홈런: result = 'home_run'
 *   - 삼진: result = 'strikeout'
 *   - 볼넷: result = 'walk'
 */
@Entity
@Table(name = "batter_vs_pitcher", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"batter_id", "pitcher_id", "season", "game_type"})
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class BatterVsPitcher extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "batter_id", nullable = false)
    private Player batter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pitcher_id", nullable = false)
    private Player pitcher;

    @Column(nullable = false)
    private Integer season;

    /**
     * R=정규시즌 / PS=포스트시즌전체
     */
    @Column(name = "game_type", nullable = false, length = 2)
    private String gameType;

    /** 총 투구 수 (상대한 전체 투구) */
    @Column(name = "total_pitches")
    private Integer totalPitches;

    /** 타수 */
    @Column(name = "at_bats")
    private Integer atBats;

    /** 안타 */
    private Integer hits;

    /** 홈런 */
    @Column(name = "home_runs")
    private Integer homeRuns;

    /** 삼진 */
    @Column(name = "strike_outs")
    private Integer strikeOuts;

    /** 볼넷 */
    @Column(name = "base_on_balls")
    private Integer baseOnBalls;

    /** 타율 (hits / atBats) */
    private Double avg;

    /** 득점권 상황(scoringPlay=true) 투구 수 */
    @Column(name = "pitches_in_scoring")
    private Integer pitchesInScoring;
}