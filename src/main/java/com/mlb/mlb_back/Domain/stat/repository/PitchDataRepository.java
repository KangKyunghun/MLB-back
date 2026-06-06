package com.mlb.mlb_back.Domain.stat.repository;

import com.mlb.mlb_back.Domain.stat.entity.PitchData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PitchDataRepository extends JpaRepository<PitchData, Long> {

    List<PitchData> findByPitcherId(Long pitcherId);

    List<PitchData> findByGameId(Long gameId);

    long countByGameId(Long gameId);

    void deleteByGameId(Long gameId);

    // ── 투수별 조회 ─────────────────────────────────────────────
    @Query("SELECT pd FROM PitchData pd WHERE pd.pitcher.id = :id AND pd.game.season = :season AND pd.game.gameType = 'R' ORDER BY pd.game.gameDate")
    List<PitchData> findByPitcherRegularSeason(@Param("id") Long pitcherId, @Param("season") Integer season);

    @Query("SELECT pd FROM PitchData pd WHERE pd.pitcher.id = :id AND pd.game.season = :season AND pd.game.gameType = 'W' ORDER BY pd.game.gameDate")
    List<PitchData> findByPitcherWildCard(@Param("id") Long pitcherId, @Param("season") Integer season);

    @Query("SELECT pd FROM PitchData pd WHERE pd.pitcher.id = :id AND pd.game.season = :season AND pd.game.gameType = 'D' ORDER BY pd.game.gameDate")
    List<PitchData> findByPitcherDivisionSeries(@Param("id") Long pitcherId, @Param("season") Integer season);

    @Query("SELECT pd FROM PitchData pd WHERE pd.pitcher.id = :id AND pd.game.season = :season AND pd.game.gameType = 'L' ORDER BY pd.game.gameDate")
    List<PitchData> findByPitcherChampionshipSeries(@Param("id") Long pitcherId, @Param("season") Integer season);

    @Query("SELECT pd FROM PitchData pd WHERE pd.pitcher.id = :id AND pd.game.season = :season AND pd.game.gameType = 'F' ORDER BY pd.game.gameDate")
    List<PitchData> findByPitcherWorldSeries(@Param("id") Long pitcherId, @Param("season") Integer season);

    /** 포스트시즌 전체 (W+D+L+F) */
    @Query("SELECT pd FROM PitchData pd WHERE pd.pitcher.id = :id AND pd.game.season = :season AND pd.game.gameType IN ('W','D','L','F') ORDER BY pd.game.gameDate")
    List<PitchData> findByPitcherPostSeason(@Param("id") Long pitcherId, @Param("season") Integer season);

    // ── 타자별 조회 ─────────────────────────────────────────────
    @Query("SELECT pd FROM PitchData pd WHERE pd.batter.id = :id AND pd.game.season = :season AND pd.game.gameType = 'R' ORDER BY pd.game.gameDate")
    List<PitchData> findByBatterRegularSeason(@Param("id") Long batterId, @Param("season") Integer season);

    @Query("SELECT pd FROM PitchData pd WHERE pd.batter.id = :id AND pd.game.season = :season AND pd.game.gameType = 'W' ORDER BY pd.game.gameDate")
    List<PitchData> findByBatterWildCard(@Param("id") Long batterId, @Param("season") Integer season);

    @Query("SELECT pd FROM PitchData pd WHERE pd.batter.id = :id AND pd.game.season = :season AND pd.game.gameType = 'D' ORDER BY pd.game.gameDate")
    List<PitchData> findByBatterDivisionSeries(@Param("id") Long batterId, @Param("season") Integer season);

    @Query("SELECT pd FROM PitchData pd WHERE pd.batter.id = :id AND pd.game.season = :season AND pd.game.gameType = 'L' ORDER BY pd.game.gameDate")
    List<PitchData> findByBatterChampionshipSeries(@Param("id") Long batterId, @Param("season") Integer season);

    @Query("SELECT pd FROM PitchData pd WHERE pd.batter.id = :id AND pd.game.season = :season AND pd.game.gameType = 'F' ORDER BY pd.game.gameDate")
    List<PitchData> findByBatterWorldSeries(@Param("id") Long batterId, @Param("season") Integer season);

    @Query("SELECT pd FROM PitchData pd WHERE pd.batter.id = :id AND pd.game.season = :season AND pd.game.gameType IN ('W','D','L','F') ORDER BY pd.game.gameDate")
    List<PitchData> findByBatterPostSeason(@Param("id") Long batterId, @Param("season") Integer season);
}