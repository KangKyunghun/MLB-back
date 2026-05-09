package com.football.Football_back.Domain.team.service;

import com.football.Football_back.Domain.team.dto.TeamResponse;
import com.football.Football_back.Domain.team.repository.TeamRepository;
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
        return teamRepository.findAll()
                .stream()
                .map(TeamResponse::from)
                .collect(Collectors.toList());
    }

    // 리그별 팀 목록
    public List<TeamResponse> getTeamsByLeague(Long leagueId) {
        return teamRepository.findByLeagueId(leagueId)
                .stream()
                .map(TeamResponse::from)
                .collect(Collectors.toList());
    }

    // 특정 팀 조회
    public TeamResponse getTeam(Long teamId) {
        return teamRepository.findById(teamId)
                .map(TeamResponse::from)
                .orElseThrow(() -> new RuntimeException("팀을 찾을 수 없습니다: " + teamId));
    }

    // 팀 검색 (엠블럼 포함)
    public List<TeamResponse> searchTeams(String name) {
        return teamRepository.findByNameContainingIgnoreCase(name)
                .stream()
                .map(TeamResponse::from)
                .collect(Collectors.toList());
    }
}