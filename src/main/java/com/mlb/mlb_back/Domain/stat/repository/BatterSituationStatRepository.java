package com.mlb.mlb_back.Domain.stat.repository;

import com.mlb.mlb_back.Domain.stat.entity.BatterSituationStat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BatterSituationStatRepository extends JpaRepository<BatterSituationStat, Long> {

    /** 선수의 특정 시즌 전체 상황 스탯 */
    List<BatterSituationStat> findByPlayerIdAndSeason(Long playerId, Integer season);

    /** 선수의 특정 시즌 + 상황 코드 */
    Optional<BatterSituationStat> findByPlayerIdAndSeasonAndSitCode(
            Long playerId, Integer season, String sitCode);

    boolean existsByPlayerIdAndSeasonAndSitCode(
            Long playerId, Integer season, String sitCode);

    long countBySeason(Integer season);
}
