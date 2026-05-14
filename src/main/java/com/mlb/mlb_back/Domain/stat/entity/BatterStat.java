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
    private Integer season;                         // 시즌 연도

    // ================================================
    // 출전 기록
    // ================================================
    @Column(name = "games_played")
    private Integer gamesPlayed;                    // 출전 경기 수

    @Column(name = "plate_appearances")
    private Integer plateAppearances;               // 타석 수

    @Column(name = "at_bats")
    private Integer atBats;                         // 타수 (타석 - 볼넷 - 사구 - 희생타 등)

    // ================================================
    // 타격 기록
    // ================================================
    private Integer hits;                           // 안타

    private Integer doubles;                        // 2루타

    private Integer triples;                        // 3루타

    @Column(name = "home_runs")
    private Integer homeRuns;                       // 홈런

    @Column(name = "total_bases")
    private Integer totalBases;                     // 루타 (안타*1 + 2루타*2 + 3루타*3 + 홈런*4)

    private Integer runs;                           // 득점 (홈을 밟은 횟수)

    @Column(name = "rbi")
    private Integer rbi;                            // 타점 (득점을 만들어낸 횟수)

    // ================================================
    // 출루 기록
    // ================================================
    private Integer walks;                          // 볼넷 (4구)

    @Column(name = "intentional_walks")
    private Integer intentionalWalks;               // 고의 4구

    @Column(name = "hit_by_pitch")
    private Integer hitByPitch;                     // 사구 (몸에 맞는 공)

    @Column(name = "catchers_interference")
    private Integer catchersInterference;           // 포수 방해 (타격 방해로 출루)

    // ================================================
    // 주루 기록
    // ================================================
    @Column(name = "stolen_bases")
    private Integer stolenBases;                    // 도루 성공

    @Column(name = "caught_stealing")
    private Integer caughtStealing;                 // 도루 실패

    @Column(name = "stolen_base_percentage")
    private Double stolenBasePercentage;            // 도루 성공률

    // ================================================
    // 삼진 / 병살 기록
    // ================================================
    @Column(name = "strike_outs")
    private Integer strikeOuts;                     // 삼진 아웃

    @Column(name = "ground_into_double_play")
    private Integer groundIntoDoublePlay;           // 병살타 (땅볼로 2명 아웃)

    // ================================================
    // 희생 기록
    // ================================================
    @Column(name = "sac_bunts")
    private Integer sacBunts;                       // 희생 번트

    @Column(name = "sac_flies")
    private Integer sacFlies;                       // 희생 플라이 (외야 뜬공으로 득점)

    @Column(name = "left_on_base")
    private Integer leftOnBase;                     // 잔루 (득점 못하고 남은 주자)

    // ================================================
    // 타구 기록
    // ================================================
    @Column(name = "ground_outs")
    private Integer groundOuts;                     // 땅볼 아웃

    @Column(name = "air_outs")
    private Integer airOuts;                        // 뜬공 아웃

    @Column(name = "ground_outs_to_air_outs")
    private Double groundOutsToAirOuts;             // 땅볼/뜬공 비율 (낮을수록 장타형)

    @Column(name = "number_of_pitches")
    private Integer numberOfPitches;                // 상대한 총 투구 수

    @Column(name = "at_bats_per_home_run")
    private Double atBatsPerHomeRun;                // 홈런 1개당 타수 (낮을수록 홈런 타자)

    // ================================================
    // 비율 스탯
    // ================================================
    private Double avg;                             // 타율 (안타 / 타수)

    private Double obp;                             // 출루율 (출루 / 타석)

    private Double slg;                             // 장타율 (루타 / 타수)

    private Double ops;                             // OPS (출루율 + 장타율)

    private Double babip;                           // BABIP (인플레이 타구 타율, 운 측정 지표)
}