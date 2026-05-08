package com.football.Football_back.Domain.match.repository;

import com.football.Football_back.Domain.match.entity.Lineup;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LineupRepository extends JpaRepository<Lineup, Long> {
    
    // 경기별 전체 라인업 (선발 + 후보)
    List<Lineup> findByMatchId(Long matchId);

    // 경기별 선발만
    List<Lineup> findByMatchIdAndIsStarterTrue(Long matchId);

    // 경기별 후보만
    List<Lineup> findByMatchIdAndIsStarterFalse(Long matchId);

    // 경기별 팀 라인업
    List<Lineup> findByMatchIdAndTeamId(Long matchId, Long teamId);

}
