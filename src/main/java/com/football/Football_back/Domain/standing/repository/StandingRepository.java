package com.football.Football_back.Domain.standing.repository;

import com.football.Football_back.Domain.standing.entity.Standing;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;  

public interface StandingRepository extends JpaRepository<Standing, Long> {

    // 시즌별 순위표 (순위순)
    List<Standing> findBySeasonIdOrderByRankAsc(Long seasonId);

    // 리그 + 시즌별 순위표
    List<Standing> findByLeagueIdAndSeasonIdOrderByRankAsc(
        Long leagueId, Long seasonId
    );

    // 특정 팀 순위 조회
    Optional<Standing> findBySeasonIdAndTeamId(Long seasonId, Long teamId);

}
