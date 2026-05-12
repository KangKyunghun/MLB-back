package com.mlb.mlb_back.Domain.game.repository;

import com.mlb.mlb_back.Domain.game.entity.BoxScore;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoxScoreRepository extends JpaRepository<BoxScore, Long> {

    List<BoxScore> findByGameId(Long gameId);

    List<BoxScore> findByPlayerId(Long playerId);
}