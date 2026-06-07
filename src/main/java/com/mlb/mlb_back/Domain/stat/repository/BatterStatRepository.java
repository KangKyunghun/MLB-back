package com.mlb.mlb_back.Domain.stat.repository;

import com.mlb.mlb_back.Domain.stat.entity.BatterStat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BatterStatRepository extends JpaRepository<BatterStat, Long> {

    long countBySeason(Integer season);

    long countBySeasonAndGameType(Integer season, String gameType);

    boolean existsByPlayerIdAndSeasonAndTeamIdAndGameType(
            Long playerId, Integer season, Long teamId, String gameType);

    boolean existsByPlayerIdAndSeasonAndTeamId(Long playerId, Integer season, Long teamId);

    List<BatterStat> findBySeason(Integer season);

    List<BatterStat> findBySeasonAndGameType(Integer season, String gameType);  // 추가

    List<BatterStat> findByPlayerId(Long playerId);

    List<BatterStat> findByPlayerIdAndGameType(Long playerId, String gameType);  // 추가

    List<BatterStat> findByPlayerIdAndSeasonAndGameType(Long playerId, Integer season, String gameType);  // 추가

    List<BatterStat> findByTeamIdAndSeason(Long teamId, Integer season);

    List<BatterStat> findByTeamIdAndSeasonAndGameType(Long teamId, Integer season, String gameType);  // 추가
}