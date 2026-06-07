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

    long countBySeasonAndGameType(Integer season, String gameType);

    boolean existsByBatterIdAndPitcherIdAndSeasonAndGameType(
            Long batterId, Long pitcherId, Integer season, String gameType);

    boolean existsByBatterIdAndPitcherIdAndSeason(
            Long batterId, Long pitcherId, Integer season);

    /** 특정 타자의 시즌 전체 상대전적 (상대 투수별) */
    List<BatterVsPitcher> findByBatterIdAndSeason(Long batterId, Integer season);

    /** 특정 투수의 시즌 전체 피상대 (상대 타자별) */
    List<BatterVsPitcher> findByPitcherIdAndSeason(Long pitcherId, Integer season);

    /** 게임타입별(R/W/D/L/F) pitch_data 집계 */
    @Query(value = """
        SELECT
            pd.batter_id,
            pd.pitcher_id,
            :season AS season,
            COUNT(*)                                                        AS total_pitches,
            COUNT(*) FILTER (WHERE pd.event IN (
                'Single','Double','Triple','Home Run',
                'Groundout','Flyout','Lineout','Pop Out','Forceout',
                'Strikeout','Grounded Into DP','Strikeout Double Play',
                'Sac Fly','Sac Bunt','Field Error',
                'Fielders Choice','Fielders Choice Out','Double Play'))     AS at_bats,
            COUNT(*) FILTER (WHERE pd.event IN (
                'Single','Double','Triple','Home Run'))                     AS hits,
            COUNT(*) FILTER (WHERE pd.event = 'Home Run')                  AS home_runs,
            COUNT(*) FILTER (WHERE pd.event IN (
                'Strikeout','Strikeout Double Play'))                       AS strike_outs,
            COUNT(*) FILTER (WHERE pd.event = 'Walk')                      AS base_on_balls,
            COUNT(*) FILTER (WHERE pd.scoring_play = true)                 AS pitches_in_scoring
        FROM pitch_data pd
        JOIN game g ON pd.game_id = g.id
        WHERE g.season = :season
          AND g.game_type = :gameType
          AND pd.batter_id IS NOT NULL
          AND pd.pitcher_id IS NOT NULL
        GROUP BY pd.batter_id, pd.pitcher_id
        HAVING COUNT(*) >= 3
        """, nativeQuery = true)
    List<Object[]> aggregateByGameType(@Param("season") Integer season, @Param("gameType") String gameType);
}