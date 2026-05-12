package com.mlb.mlb_back.Domain.game.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.mlb.mlb_back.Domain.game.entity.Game;

import java.time.LocalDateTime;
import java.util.List;

public interface GameRepository extends JpaRepository<Game, Long> {

    long countBySeason(Integer season);

    List<Game> findBySeason(Integer season);

    List<Game> findByHomeTeamIdOrAwayTeamId(Long homeTeamId, Long awayTeamId);

    List<Game> findByGameDateBetween(LocalDateTime start, LocalDateTime end);

    List<Game> findByStatus(String status);
}