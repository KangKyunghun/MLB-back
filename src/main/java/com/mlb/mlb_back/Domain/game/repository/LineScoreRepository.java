package com.mlb.mlb_back.Domain.game.repository;

import com.mlb.mlb_back.Domain.game.entity.LineScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LineScoreRepository extends JpaRepository<LineScore, Long> {

    List<LineScore> findByGameId(Long gameId);

    @Query("""
        SELECT COUNT(ls)
        FROM LineScore ls
        WHERE ls.game.id = :gameId
    """)
    long countByGameId(@Param("gameId") Long gameId);

    void deleteByGameId(Long gameId);

    @Query("""
        SELECT CASE WHEN COUNT(ls) > 0 THEN true ELSE false END
        FROM LineScore ls
        WHERE ls.game.id = :gameId
    """)
    boolean existsByGameId(@Param("gameId") Long gameId);

    @Query("""
        SELECT COUNT(ls)
        FROM LineScore ls
        WHERE ls.game.season = :season
    """)
    long countByGameSeason(@Param("season") int season);
}