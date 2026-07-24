package com.mlb.mlb_back.Domain.game.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.mlb.mlb_back.Domain.game.entity.Game;

import java.time.Instant;
import java.util.List;

public interface GameRepository extends JpaRepository<Game, Long> {

    long countBySeason(Integer season);

    List<Game> findBySeason(Integer season);

    List<Game> findByHomeTeamIdOrAwayTeamId(Long homeTeamId, Long awayTeamId);

    List<Game> findByGameDateBetween(Instant start, Instant end);

    List<Game> findByStatus(String status);

    List<Game> findBySeasonAndStatus(Integer season, String status);

    List<Game> findBySeasonAndStatusIn(Integer season, List<String> statuses);

    // 특정 날짜 범위 + status로 경기 조회 (Live 경기 감지용)
    List<Game> findByStatusAndGameDateBetween(String status, Instant start, Instant end);
}