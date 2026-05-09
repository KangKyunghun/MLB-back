package com.football.Football_back.Domain.standing.controller;

import com.football.Football_back.Domain.standing.dto.StandingResponse;
import com.football.Football_back.Domain.standing.service.StandingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leagues/{leagueId}/standings")
@RequiredArgsConstructor
public class StandingController {

    private final StandingService standingService;

    // 순위표 조회
    // GET /api/leagues/2021/standings          → 현재 시즌
    // GET /api/leagues/2021/standings?year=2024 → 특정 시즌
    @GetMapping
    public ResponseEntity<List<StandingResponse>> getStandings(
            @PathVariable Long leagueId,
            @RequestParam(required = false) String year) {
        return ResponseEntity.ok(standingService.getStandings(leagueId, year));
    }

    // 시즌 목록 조회 (드롭다운용)
    // GET /api/leagues/2021/standings/seasons
    @GetMapping("/seasons")
    public ResponseEntity<List<String>> getSeasonYears(@PathVariable Long leagueId) {
        return ResponseEntity.ok(standingService.getSeasonYears(leagueId));
    }
}