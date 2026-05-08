package com.football.Football_back.Domain.match.repository;

import com.football.Football_back.Domain.match.entity.TeamMatchStat;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface TeamMatchStatRepository extends JpaRepository<TeamMatchStat, Long> {

    // 경기별 양팀 통계
    List<TeamMatchStat> findByMatchId(Long matchId);

    // 경기별 특정 팀 통계
    Optional<TeamMatchStat> findByMatchIdAndTeamId(Long matchId, Long teamId);
    
}
