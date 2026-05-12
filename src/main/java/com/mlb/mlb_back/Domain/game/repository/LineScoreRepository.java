package com.mlb.mlb_back.Domain.game.repository;

import com.mlb.mlb_back.Domain.game.entity.LineScore;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LineScoreRepository extends JpaRepository<LineScore, Long> {

    List<LineScore> findByGameId(Long gameId);
}