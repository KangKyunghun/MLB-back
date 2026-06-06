package com.mlb.mlb_back.Domain.stat.repository;

import com.mlb.mlb_back.Domain.stat.entity.HotColdZone;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HotColdZoneRepository extends JpaRepository<HotColdZone, Long> {

    List<HotColdZone> findByPlayerId(Long playerId);

    List<HotColdZone> findByPlayerIdAndSeason(Long playerId, Integer season);

    boolean existsByPlayerIdAndSeason(Long playerId, Integer season);

    boolean existsByPlayerIdAndSeasonAndGameType(Long playerId, Integer season, String gameType);

    long countBySeason(Integer season);

}