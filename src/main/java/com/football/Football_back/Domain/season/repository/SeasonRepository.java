package com.football.Football_back.Domain.season.repository;

import com.football.Football_back.Domain.season.entity.Season;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SeasonRepository extends JpaRepository<Season, Long> {
 
    // 리그별 전체 시즌 조회
    List<Season> findByLeagueIdOrderByYearDesc(Long leagueId);

    // 현재 시즌 조회
    Optional<Season> findByLeagueIdAndIsCurrentTrue(Long leagueId);

    // 특정 연도 시즌 조회
    Optional<Season> findByLeagueIdAndYear(Long leagueId, String year);

}
