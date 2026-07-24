package com.mlb.mlb_back.Domain.game.repository;

import com.mlb.mlb_back.Domain.game.entity.LineScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LineScoreRepository extends JpaRepository<LineScore, Long> {

    List<LineScore> findByGameId(Long gameId);

    @Query("SELECT COUNT(ls) FROM LineScore ls WHERE ls.game.id = :gameId")
    long countByGameId(@Param("gameId") Long gameId);

    void deleteByGameId(Long gameId);

    @Query("SELECT CASE WHEN COUNT(ls) > 0 THEN true ELSE false END FROM LineScore ls WHERE ls.game.id = :gameId")
    boolean existsByGameId(@Param("gameId") Long gameId);

    @Query("SELECT COUNT(ls) FROM LineScore ls WHERE ls.game.season = :season")
    long countByGameSeason(@Param("season") int season);

    // ── 시즌별 game_type 조회 ───────────────────────────────────
    @Query("SELECT ls FROM LineScore ls WHERE ls.game.season = :season AND ls.game.gameType = 'R' ORDER BY ls.game.gameDate")
    List<LineScore> findBySeasonRegularSeason(@Param("season") Integer season);

    @Query("SELECT ls FROM LineScore ls WHERE ls.game.season = :season AND ls.game.gameType = 'W' ORDER BY ls.game.gameDate")
    List<LineScore> findBySeasonWildCard(@Param("season") Integer season);

    @Query("SELECT ls FROM LineScore ls WHERE ls.game.season = :season AND ls.game.gameType = 'D' ORDER BY ls.game.gameDate")
    List<LineScore> findBySeasonDivisionSeries(@Param("season") Integer season);

    @Query("SELECT ls FROM LineScore ls WHERE ls.game.season = :season AND ls.game.gameType = 'L' ORDER BY ls.game.gameDate")
    List<LineScore> findBySeasonChampionshipSeries(@Param("season") Integer season);

    @Query("SELECT ls FROM LineScore ls WHERE ls.game.season = :season AND ls.game.gameType = 'F' ORDER BY ls.game.gameDate")
    List<LineScore> findBySeasonWorldSeries(@Param("season") Integer season);

    @Query("SELECT ls FROM LineScore ls WHERE ls.game.season = :season AND ls.game.gameType IN ('W','D','L','F') ORDER BY ls.game.gameDate")
    List<LineScore> findBySeasonPostSeason(@Param("season") Integer season);
}