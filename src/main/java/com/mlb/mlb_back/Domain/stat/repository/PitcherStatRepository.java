package com.mlb.mlb_back.Domain.stat.repository;

import com.mlb.mlb_back.Domain.stat.entity.PitcherStat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PitcherStatRepository extends JpaRepository<PitcherStat, Long> {

    long countBySeason(Integer season);

    boolean existsByPlayerIdAndSeasonAndTeamId(Long playerId, Integer season, Long teamId);

    List<PitcherStat> findBySeason(Integer season);

    List<PitcherStat> findByPlayerId(Long playerId);

    List<PitcherStat> findByTeamIdAndSeason(Long teamId, Integer season);
}