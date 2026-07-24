package com.mlb.mlb_back.Domain.game.repository;

import com.mlb.mlb_back.Domain.game.entity.BoxScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BoxScoreRepository extends JpaRepository<BoxScore, Long> {

    List<BoxScore> findByGameId(Long gameId);

    long countByGameId(Long gameId);

    void deleteByGameId(Long gameId);

    List<BoxScore> findByPlayerId(Long playerId);

    @Query("""
        SELECT CASE WHEN COUNT(bs) > 0 THEN true ELSE false END
        FROM BoxScore bs WHERE bs.game.id = :gameId
    """)
    boolean existsByGameId(@Param("gameId") Long gameId);

    @Query("""
        SELECT CASE WHEN COUNT(bs) > 0 THEN true ELSE false END
        FROM BoxScore bs WHERE bs.game.id = :gameId AND bs.player.id = :playerId AND bs.playerType = :playerType
    """)
    boolean existsByGameIdAndPlayerIdAndPlayerType(
            @Param("gameId") Long gameId,
            @Param("playerId") Long playerId,
            @Param("playerType") String playerType);

    @Query("SELECT COUNT(bs) FROM BoxScore bs WHERE bs.game.season = :season")
    long countByGameSeason(@Param("season") int season);

    @Query("""
        SELECT CASE WHEN COUNT(bs) > 0 THEN true ELSE false END
        FROM BoxScore bs WHERE bs.game.id = :gameId AND bs.gamePosition IS NULL
    """)
    boolean existsByGameIdAndGamePositionIsNull(@Param("gameId") Long gameId);

    // ── 선수별 시즌별 game_type 조회 ──────────────────────────
    @Query("SELECT bs FROM BoxScore bs WHERE bs.player.id = :id AND bs.game.season = :season AND bs.game.gameType = 'R' ORDER BY bs.game.gameDate")
    List<BoxScore> findByPlayerRegularSeason(@Param("id") Long playerId, @Param("season") Integer season);

    @Query("SELECT bs FROM BoxScore bs WHERE bs.player.id = :id AND bs.game.season = :season AND bs.game.gameType = 'W' ORDER BY bs.game.gameDate")
    List<BoxScore> findByPlayerWildCard(@Param("id") Long playerId, @Param("season") Integer season);

    @Query("SELECT bs FROM BoxScore bs WHERE bs.player.id = :id AND bs.game.season = :season AND bs.game.gameType = 'D' ORDER BY bs.game.gameDate")
    List<BoxScore> findByPlayerDivisionSeries(@Param("id") Long playerId, @Param("season") Integer season);

    @Query("SELECT bs FROM BoxScore bs WHERE bs.player.id = :id AND bs.game.season = :season AND bs.game.gameType = 'L' ORDER BY bs.game.gameDate")
    List<BoxScore> findByPlayerChampionshipSeries(@Param("id") Long playerId, @Param("season") Integer season);

    @Query("SELECT bs FROM BoxScore bs WHERE bs.player.id = :id AND bs.game.season = :season AND bs.game.gameType = 'F' ORDER BY bs.game.gameDate")
    List<BoxScore> findByPlayerWorldSeries(@Param("id") Long playerId, @Param("season") Integer season);

    @Query("SELECT bs FROM BoxScore bs WHERE bs.player.id = :id AND bs.game.season = :season AND bs.game.gameType IN ('W','D','L','F') ORDER BY bs.game.gameDate")
    List<BoxScore> findByPlayerPostSeason(@Param("id") Long playerId, @Param("season") Integer season);

    // ── 시즌 전체 game_type 조회 (팀 단위) ─────────────────────
    @Query("SELECT bs FROM BoxScore bs WHERE bs.game.season = :season AND bs.game.gameType = 'W' ORDER BY bs.game.gameDate")
    List<BoxScore> findBySeasonWildCard(@Param("season") Integer season);

    @Query("SELECT bs FROM BoxScore bs WHERE bs.game.season = :season AND bs.game.gameType = 'D' ORDER BY bs.game.gameDate")
    List<BoxScore> findBySeasonDivisionSeries(@Param("season") Integer season);

    @Query("SELECT bs FROM BoxScore bs WHERE bs.game.season = :season AND bs.game.gameType = 'L' ORDER BY bs.game.gameDate")
    List<BoxScore> findBySeasonChampionshipSeries(@Param("season") Integer season);

    @Query("SELECT bs FROM BoxScore bs WHERE bs.game.season = :season AND bs.game.gameType = 'F' ORDER BY bs.game.gameDate")
    List<BoxScore> findBySeasonWorldSeries(@Param("season") Integer season);
}