package com.football.Football_back.Domain.match.controller;

import com.football.Football_back.Domain.match.dto.MatchResponse;
import com.football.Football_back.Domain.match.service.MatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MatchController {

    private final MatchService matchService;

    // 리그 경기 목록
    // GET /api/leagues/2021/matches
    // GET /api/leagues/2021/matches?year=2024
    @GetMapping("/leagues/{leagueId}/matches")
    public ResponseEntity<List<MatchResponse>> getMatchesByLeague(
            @PathVariable Long leagueId,
            @RequestParam(required = false) String year) {
        return ResponseEntity.ok(matchService.getMatchesByLeague(leagueId, year));
    }

    // 특정 라운드 경기
    // GET /api/leagues/2021/matches/matchday/1?year=2024
    @GetMapping("/leagues/{leagueId}/matches/matchday/{matchday}")
    public ResponseEntity<List<MatchResponse>> getMatchesByMatchday(
            @PathVariable Long leagueId,
            @PathVariable Integer matchday,
            @RequestParam(required = false) String year) {
        return ResponseEntity.ok(matchService.getMatchesByMatchday(leagueId, year, matchday));
    }

    // 챔스 stage별 경기
    // GET /api/leagues/2001/matches/stage/GROUP_STAGE?year=2023
    @GetMapping("/leagues/{leagueId}/matches/stage/{stage}")
    public ResponseEntity<List<MatchResponse>> getMatchesByStage(
            @PathVariable Long leagueId,
            @PathVariable String stage,
            @RequestParam(required = false) String year) {
        return ResponseEntity.ok(matchService.getMatchesByStage(leagueId, year, stage));
    }

    // 챔스 토너먼트 경기
    // GET /api/leagues/2001/matches/tournament?year=2024
    @GetMapping("/leagues/{leagueId}/matches/tournament")
    public ResponseEntity<List<MatchResponse>> getTournamentMatches(
            @PathVariable Long leagueId,
            @RequestParam(required = false) String year) {
        return ResponseEntity.ok(matchService.getTournamentMatches(leagueId, year));
    }

    // 팀 경기 목록
    // GET /api/teams/65/matches?year=2024&leagueId=2021
    @GetMapping("/teams/{teamId}/matches")
    public ResponseEntity<List<MatchResponse>> getMatchesByTeam(
            @PathVariable Long teamId,
            @RequestParam(required = false) String year,
            @RequestParam(required = false) Long leagueId) {
        return ResponseEntity.ok(matchService.getMatchesByTeam(teamId, year, leagueId));
    }

    // 경기 상세
    // GET /api/matches/123456
    @GetMapping("/matches/{matchId}")
    public ResponseEntity<MatchResponse> getMatch(@PathVariable Long matchId) {
        return ResponseEntity.ok(matchService.getMatch(matchId));
    }

    // 진행중인 경기
    // GET /api/matches/live
    @GetMapping("/matches/live")
    public ResponseEntity<List<MatchResponse>> getLiveMatches() {
        return ResponseEntity.ok(matchService.getLiveMatches());
    }
}