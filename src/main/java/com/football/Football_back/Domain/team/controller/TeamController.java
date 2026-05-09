package com.football.Football_back.Domain.team.controller;

import com.football.Football_back.Domain.team.dto.TeamResponse;
import com.football.Football_back.Domain.team.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teams")
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;

    // 전체 팀 목록
    // GET /api/teams
    @GetMapping
    public ResponseEntity<List<TeamResponse>> getAllTeams() {
        return ResponseEntity.ok(teamService.getAllTeams());
    }

    // 리그별 팀 목록
    // GET /api/teams?leagueId=2021
    @GetMapping(params = "leagueId")
    public ResponseEntity<List<TeamResponse>> getTeamsByLeague(
            @RequestParam Long leagueId) {
        return ResponseEntity.ok(teamService.getTeamsByLeague(leagueId));
    }

    // 팀 검색 (엠블럼 포함)
    // GET /api/teams/search?name=manchester
    @GetMapping("/search")
    public ResponseEntity<List<TeamResponse>> searchTeams(
            @RequestParam String name) {
        return ResponseEntity.ok(teamService.searchTeams(name));
    }

    // 특정 팀 조회
    // GET /api/teams/65
    @GetMapping("/{teamId}")
    public ResponseEntity<TeamResponse> getTeam(@PathVariable Long teamId) {
        return ResponseEntity.ok(teamService.getTeam(teamId));
    }
}