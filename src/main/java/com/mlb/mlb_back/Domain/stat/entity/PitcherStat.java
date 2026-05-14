package com.mlb.mlb_back.Domain.stat.entity;

import com.mlb.mlb_back.Domain.player.entity.Player;
import com.mlb.mlb_back.Domain.team.entity.Team;
import com.mlb.mlb_back.Global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "pitcher_stat", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"player_id", "season", "team_id"})
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PitcherStat extends BaseEntity {

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

    @Column(name = "games_started")
    private Integer gamesStarted;                   // 선발 등판 수

    @Column(name = "games_pitched")
    private Integer gamesPitched;                   // 실제 투구한 경기 수

    @Column(name = "games_finished")
    private Integer gamesFinished;                  // 경기 마무리 횟수 (마지막 투수로 끝낸 경기)

    @Column(name = "complete_games")
    private Integer completeGames;                  // 완투 (혼자 경기 전체 투구)

    private Integer shutouts;                       // 완봉 (무실점 완투)

    // ================================================
    // 승패 기록
    // ================================================
    private Integer wins;                           // 승

    private Integer losses;                         // 패

    private Integer saves;                          // 세이브 (리드 상황에서 마무리)

    @Column(name = "save_opportunities")
    private Integer saveOpportunities;              // 세이브 기회

    @Column(name = "blown_saves")
    private Integer blownSaves;                     // 세이브 실패

    private Integer holds;                          // 홀드 (중간 계투가 리드 유지)

    @Column(name = "win_percentage")
    private Double winPercentage;                   // 승률 (승 / (승 + 패))

    // ================================================
    // 이닝 / 아웃 기록
    // ================================================
    @Column(name = "innings_pitched")
    private Double inningsPitched;                  // 투구 이닝 수 (예: 7.2 = 7이닝 2아웃)

    private Integer outs;                           // 잡은 총 아웃 카운트

    @Column(name = "batters_faced")
    private Integer battersFaced;                   // 상대한 총 타자 수

    // ================================================
    // 피타격 기록
    // ================================================
    @Column(name = "hits_allowed")
    private Integer hitsAllowed;                    // 피안타

    @Column(name = "home_runs_allowed")
    private Integer homeRunsAllowed;                // 피홈런

    private Integer doubles;                        // 피2루타

    private Integer triples;                        // 피3루타

    @Column(name = "total_bases_allowed")
    private Integer totalBasesAllowed;              // 피루타

    @Column(name = "ground_outs")
    private Integer groundOuts;                     // 유도한 땅볼 아웃 수

    @Column(name = "air_outs")
    private Integer airOuts;                        // 유도한 뜬공 아웃 수

    @Column(name = "ground_outs_to_air_outs")
    private Double groundOutsToAirOuts;             // 땅볼/뜬공 비율 (높을수록 땅볼 투수)

    // ================================================
    // 출루 허용 기록
    // ================================================
    private Integer walks;                          // 볼넷 허용

    @Column(name = "intentional_walks")
    private Integer intentionalWalks;               // 고의 4구 허용

    @Column(name = "hit_batsmen")
    private Integer hitBatsmen;                     // 사구 허용 (몸에 맞는 공)

    @Column(name = "catchers_interference")
    private Integer catchersInterference;           // 포수 방해

    // ================================================
    // 주루 허용 기록
    // ================================================
    @Column(name = "stolen_bases_allowed")
    private Integer stolenBasesAllowed;             // 허용 도루

    @Column(name = "caught_stealing")
    private Integer caughtStealing;                 // 도루 저지

    @Column(name = "inherited_runners")
    private Integer inheritedRunners;               // 이어받은 주자 수 (전 투수 잔루)

    @Column(name = "inherited_runners_scored")
    private Integer inheritedRunnersScored;         // 이어받은 주자 중 득점 허용 수

    // ================================================
    // 실점 기록
    // ================================================
    private Integer runs;                           // 총 실점

    @Column(name = "earned_runs")
    private Integer earnedRuns;                     // 자책점 (투수 실책으로 인한 실점)

    // ================================================
    // 삼진 기록
    // ================================================
    @Column(name = "strike_outs")
    private Integer strikeOuts;                     // 탈삼진

    // ================================================
    // 투구 기록
    // ================================================
    private Integer strikes;                        // 총 스트라이크 수

    @Column(name = "strike_percentage")
    private Double strikePercentage;                // 스트라이크 비율

    @Column(name = "number_of_pitches")
    private Integer numberOfPitches;                // 총 투구 수

    @Column(name = "pitches_per_inning")
    private Double pitchesPerInning;                // 이닝당 투구 수

    private Integer balks;                          // 보크 (불법 투구 동작)

    @Column(name = "wild_pitches")
    private Integer wildPitches;                    // 폭투 (포수가 잡지 못한 투구)

    private Integer pickoffs;                       // 견제 성공

    // ================================================
    // 희생 허용
    // ================================================
    @Column(name = "sac_bunts")
    private Integer sacBunts;                       // 허용 희생 번트

    @Column(name = "sac_flies")
    private Integer sacFlies;                       // 허용 희생 플라이

    @Column(name = "ground_into_double_play")
    private Integer groundIntoDoublePlay;           // 유도한 병살타

    // ================================================
    // 비율 스탯
    // ================================================
    private Double era;                             // 평균 자책점 (9이닝당 자책점)

    private Double whip;                            // WHIP (이닝당 볼넷+안타 허용)

    @Column(name = "avg_allowed")
    private Double avgAllowed;                      // 피안타율

    @Column(name = "obp_allowed")
    private Double obpAllowed;                      // 피출루율

    @Column(name = "slg_allowed")
    private Double slgAllowed;                      // 피장타율

    @Column(name = "ops_allowed")
    private Double opsAllowed;                      // 피OPS

    private Double babip;                           // BABIP 허용 (인플레이 타구 피안타율)

    // ================================================
    // 9이닝 환산 스탯
    // ================================================
    @Column(name = "strike_out_per_9")
    private Double strikeOutPer9;                   // K/9 (9이닝당 탈삼진)

    @Column(name = "walk_per_9")
    private Double walkPer9;                        // BB/9 (9이닝당 볼넷)

    @Column(name = "hits_per_9")
    private Double hitsPer9;                        // H/9 (9이닝당 피안타)

    @Column(name = "home_runs_per_9")
    private Double homeRunsPer9;                    // HR/9 (9이닝당 피홈런)

    @Column(name = "runs_scored_per_9")
    private Double runsScoredPer9;                  // R/9 (9이닝당 실점)

    // ================================================
    // 기타 비율
    // ================================================
    @Column(name = "strikeout_walk_ratio")
    private Double strikeoutWalkRatio;              // K/BB (탈삼진/볼넷 비율, 높을수록 좋음)
}