package com.football.Football_back.Domain.best.repository;

import com.football.Football_back.Domain.best.entity.BestEleven;
import org.springframework.data.jpa.repository.JpaRepository;  
import java.util.Optional;

public interface BestElevenRepository extends JpaRepository<BestEleven, Long> {
    
    // 리그 베스트 11 조회
    Optional<BestEleven> findByLeagueIdAndSeasonIdAndType(
        Long leagueId, Long seasonId, String type
    );

    // 팀 베스트 11 조회
    Optional<BestEleven> findByLeagueIdAndSeasonIdAndTeamIdAndType(
        Long leagueId, Long seasonId, Long teamId, String type
    );

}
