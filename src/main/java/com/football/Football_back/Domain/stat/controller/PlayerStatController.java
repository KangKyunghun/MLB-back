package com.football.Football_back.Domain.stat.controller;

import com.football.Football_back.Domain.stat.dto.PlayerSeasonStatResponse;
import com.football.Football_back.Domain.stat.dto.PlayerStatResponse;
import com.football.Football_back.Domain.stat.service.PlayerStatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PlayerStatController {

    private final PlayerStatService playerStatService;

    // 선수 경기별 스탯 (시즌)
    // GET /api/players/123/stats?seasonId=1
    @GetMapping("/players/{playerId}/stats")
    public ResponseEntity<List<PlayerStatResponse>> getPlayerStats(
            @PathVariable Long playerId,
            @RequestParam Long seasonId) {
        return ResponseEntity.ok(playerStatService.getPlayerStatsBySeason(playerId, seasonId));
    }

    // 선수 시즌 누적 스탯
    // GET /api/players/123/stats/season?seasonId=1
    @GetMapping("/players/{playerId}/stats/season")
    public ResponseEntity<PlayerSeasonStatResponse> getPlayerSeasonStat(
            @PathVariable Long playerId,
            @RequestParam Long seasonId) {
        return ResponseEntity.ok(playerStatService.getPlayerSeasonStat(playerId, seasonId));
    }

    // 경기별 전체 선수 스탯
    // GET /api/matches/123456/stats
    @GetMapping("/matches/{matchId}/stats")
    public ResponseEntity<List<PlayerStatResponse>> getStatsByMatch(
            @PathVariable Long matchId) {
        return ResponseEntity.ok(playerStatService.getStatsByMatch(matchId));
    }
}