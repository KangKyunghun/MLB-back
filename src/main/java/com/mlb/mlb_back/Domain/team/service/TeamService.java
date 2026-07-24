package com.mlb.mlb_back.Domain.team.service;

import com.mlb.mlb_back.Domain.team.dto.TeamResponse;
import com.mlb.mlb_back.Domain.team.entity.Team;
import com.mlb.mlb_back.Domain.team.repository.TeamRepository;
import com.mlb.mlb_back.Global.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
 
import java.util.List;
import java.util.stream.Collectors;
 
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TeamService {

    private final TeamRepository teamRepository;

    // 전체 팀 목록
    public List<TeamResponse> getAllTeams() {
        return teamRepository.findAll().stream()
                .map(TeamResponse::fromEntity)
                .collect(Collectors.toList());
    }

    // 팀 상세
    public TeamResponse getTeam(Long teamId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> ApiException.notFound("팀을 찾을 수 없습니다. ID: " + teamId));
        return TeamResponse.fromEntity(team);
    }

    // 리그별 팀 목록 (American League / National League)
    public List<TeamResponse> getTeamsByLeague(String league) {
        return teamRepository.findByLeague(league).stream()
                .map(TeamResponse::fromEntity)
                .collect(Collectors.toList());
    }
 
    // 지구별 팀 목록 (AL East, AL Central, AL West, NL East, NL Central, NL West)
    public List<TeamResponse> getTeamsByDivision(String division) {
        return teamRepository.findByDivision(division).stream()
                .map(TeamResponse::fromEntity)
                .collect(Collectors.toList());
    }
 
    // 팀 검색
    public List<TeamResponse> searchTeams(String keyword) {
        return teamRepository.findByNameContainingIgnoreCase(keyword).stream()
                .map(TeamResponse::fromEntity)
                .collect(Collectors.toList());
    }
    
}
