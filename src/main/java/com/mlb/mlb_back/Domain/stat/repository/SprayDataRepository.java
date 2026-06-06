package com.mlb.mlb_back.Domain.stat.repository;

import com.mlb.mlb_back.Domain.stat.entity.SprayData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SprayDataRepository extends JpaRepository<SprayData, Long> {

    List<SprayData> findByPlayerId(Long playerId);

    List<SprayData> findBySeason(Integer season);

    long countBySeason(Integer season);

    long countByGameId(Long gameId);

    void deleteByGameId(Long gameId);

    @Query("SELECT sd FROM SprayData sd WHERE sd.player.id = :id AND sd.season = :season AND sd.game.gameType = 'R'")
    List<SprayData> findByPlayerRegularSeason(@Param("id") Long playerId, @Param("season") Integer season);

    @Query("SELECT sd FROM SprayData sd WHERE sd.player.id = :id AND sd.season = :season AND sd.game.gameType = 'W'")
    List<SprayData> findByPlayerWildCard(@Param("id") Long playerId, @Param("season") Integer season);

    @Query("SELECT sd FROM SprayData sd WHERE sd.player.id = :id AND sd.season = :season AND sd.game.gameType = 'D'")
    List<SprayData> findByPlayerDivisionSeries(@Param("id") Long playerId, @Param("season") Integer season);

    @Query("SELECT sd FROM SprayData sd WHERE sd.player.id = :id AND sd.season = :season AND sd.game.gameType = 'L'")
    List<SprayData> findByPlayerChampionshipSeries(@Param("id") Long playerId, @Param("season") Integer season);

    @Query("SELECT sd FROM SprayData sd WHERE sd.player.id = :id AND sd.season = :season AND sd.game.gameType = 'F'")
    List<SprayData> findByPlayerWorldSeries(@Param("id") Long playerId, @Param("season") Integer season);

    @Query("SELECT sd FROM SprayData sd WHERE sd.player.id = :id AND sd.season = :season AND sd.game.gameType IN ('W','D','L','F')")
    List<SprayData> findByPlayerPostSeason(@Param("id") Long playerId, @Param("season") Integer season);
}