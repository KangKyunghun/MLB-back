package com.mlb.mlb_back.Domain.stat.repository;

import com.mlb.mlb_back.Domain.stat.entity.PitcherStat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PitcherStatRepository extends JpaRepository<PitcherStat, Long> {

    long countBySeason(Integer season);

    void deleteBySeasonAndGameType(Integer season, String gameType);

    long countBySeasonAndGameType(Integer season, String gameType);

    boolean existsByPlayerIdAndSeasonAndTeamIdAndGameType(
            Long playerId, Integer season, Long teamId, String gameType);

    boolean existsByPlayerIdAndSeasonAndTeamId(Long playerId, Integer season, Long teamId);

    List<PitcherStat> findBySeason(Integer season);

    List<PitcherStat> findBySeasonAndGameType(Integer season, String gameType);  // 추가

    List<PitcherStat> findByPlayerId(Long playerId);

    List<PitcherStat> findByPlayerIdAndGameType(Long playerId, String gameType);  // 추가

    List<PitcherStat> findByPlayerIdAndSeasonAndGameType(Long playerId, Integer season, String gameType);  // 추가

    List<PitcherStat> findByTeamIdAndSeason(Long teamId, Integer season);

    List<PitcherStat> findByTeamIdAndSeasonAndGameType(Long teamId, Integer season, String gameType);  // 추가
}