package com.mlb.mlb_back.Domain.team.controller;

import com.mlb.mlb_back.Domain.team.dto.TeamResponse;
import com.mlb.mlb_back.Domain.team.service.TeamService;
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

    // 팀 검색
    // GET /api/teams/search?keyword=yankees
    @GetMapping("/search")
    public ResponseEntity<List<TeamResponse>> searchTeams(@RequestParam String keyword) {
        return ResponseEntity.ok(teamService.searchTeams(keyword));
    }

    // 리그별 팀 목록
    // GET /api/teams/league/American League
    @GetMapping("/league/{league}")
    public ResponseEntity<List<TeamResponse>> getTeamsByLeague(@PathVariable String league) {
        return ResponseEntity.ok(teamService.getTeamsByLeague(league));
    }

    // 지구별 팀 목록
    // GET /api/teams/division/AL East
    @GetMapping("/division/{division}")
    public ResponseEntity<List<TeamResponse>> getTeamsByDivision(@PathVariable String division) {
        return ResponseEntity.ok(teamService.getTeamsByDivision(division));
    }

    // 팀 상세
    // GET /api/teams/147
    @GetMapping("/{teamId}")
    public ResponseEntity<TeamResponse> getTeam(@PathVariable Long teamId) {
        return ResponseEntity.ok(teamService.getTeam(teamId));
    }
}