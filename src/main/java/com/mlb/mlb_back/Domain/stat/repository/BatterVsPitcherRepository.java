package com.mlb.mlb_back.Domain.stat.repository;

import com.mlb.mlb_back.Domain.stat.entity.BatterVsPitcher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BatterVsPitcherRepository extends JpaRepository<BatterVsPitcher, Long> {

    Optional<BatterVsPitcher> findByBatterIdAndPitcherIdAndSeason(
            Long batterId, Long pitcherId, Integer season);

    boolean existsByBatterIdAndPitcherIdAndSeason(
            Long batterId, Long pitcherId, Integer season);

    /** 특정 타자의 시즌 전체 상대전적 (상대 투수별) */
    List<BatterVsPitcher> findByBatterIdAndSeason(Long batterId, Integer season);

    /** 특정 투수의 시즌 전체 피상대 (상대 타자별) */
    List<BatterVsPitcher> findByPitcherIdAndSeason(Long pitcherId, Integer season);

    /** pitch_data 집계 → batter_vs_pitcher 생성 쿼리 */
    @Query(value = """
        SELECT
            pd.batter_id,
            pd.pitcher_id,
            :season AS season,
            COUNT(*)                                                        AS total_pitches,
            COUNT(*) FILTER (WHERE pd.result IN (
                'single','double','triple','home_run',
                'field_out','strikeout','grounded_into_double_play',
                'pop_out','fly_out','line_out','force_out','sac_fly',
                'sac_bunt','field_error','fielders_choice'))                AS at_bats,
            COUNT(*) FILTER (WHERE pd.result IN (
                'single','double','triple','home_run'))                     AS hits,
            COUNT(*) FILTER (WHERE pd.result = 'home_run')                 AS home_runs,
            COUNT(*) FILTER (WHERE pd.result = 'strikeout')                AS strike_outs,
            COUNT(*) FILTER (WHERE pd.result = 'walk')                     AS base_on_balls,
            COUNT(*) FILTER (WHERE pd.scoring_play = true)                 AS pitches_in_scoring
        FROM pitch_data pd
        JOIN game g ON pd.game_id = g.id
        WHERE g.season = :season
          AND pd.batter_id IS NOT NULL
          AND pd.pitcher_id IS NOT NULL
        GROUP BY pd.batter_id, pd.pitcher_id
        HAVING COUNT(*) >= 5
        """, nativeQuery = true)
    List<Object[]> aggregateFromPitchData(@Param("season") Integer season);
}
