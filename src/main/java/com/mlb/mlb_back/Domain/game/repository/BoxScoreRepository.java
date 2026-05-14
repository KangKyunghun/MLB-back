package com.mlb.mlb_back.Domain.game.repository;

import com.mlb.mlb_back.Domain.game.entity.BoxScore;
import com.mlb.mlb_back.Domain.game.entity.Game;

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
        FROM BoxScore bs
        WHERE bs.game.id = :gameId
    """)
    boolean existsByGameId(@Param("gameId") Long gameId);

    @Query("""
        SELECT COUNT(bs)
        FROM BoxScore bs
        WHERE bs.game.season = :season
    """)
    long countByGameSeason(@Param("season") int season);
}