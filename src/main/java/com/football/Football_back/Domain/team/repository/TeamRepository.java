package com.football.Football_back.Domain.team.repository;

import com.football.Football_back.Domain.team.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeamRepository extends JpaRepository<Team, Long> {

    // 리그별 팀 목록 조회
    List<Team> findByLeagueId(Long leagueId);

    // 팀명 검색 (엠블럼 포함)
    List<Team> findByNameContainingIgnoreCase(String name);
    
}
