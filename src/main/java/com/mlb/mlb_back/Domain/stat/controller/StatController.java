package com.mlb.mlb_back.Domain.stat.controller;

import com.mlb.mlb_back.Domain.stat.dto.BatterStatResponse;
import com.mlb.mlb_back.Domain.stat.dto.PitcherStatResponse;
import com.mlb.mlb_back.Domain.stat.service.StatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class StatController {

    private final StatService statService;

    // ── 타자 스탯 ─────────────────────────────────────────────

    // GET /api/players/123/batter-stats?gameType=R
    // GET /api/players/123/batter-stats?gameType=PS
    // GET /api/players/123/batter-stats  (gameType 없으면 전체)
    @GetMapping("/players/{playerId}/batter-stats")
    public ResponseEntity<List<BatterStatResponse>> getBatterStats(
            @PathVariable Long playerId,
            @RequestParam(required = false) String gameType) {
        return ResponseEntity.ok(statService.getBatterStats(playerId, gameType));
    }

    // GET /api/players/123/batter-stats/2024?gameType=R
    // GET /api/players/123/batter-stats/2024?gameType=PS
    @GetMapping("/players/{playerId}/batter-stats/{season}")
    public ResponseEntity<BatterStatResponse> getBatterStatBySeason(
            @PathVariable Long playerId,
            @PathVariable Integer season,
            @RequestParam(defaultValue = "R") String gameType) {
        return ResponseEntity.ok(statService.getBatterStatBySeason(playerId, season, gameType));
    }

    // GET /api/stats/batters/leaderboard?season=2024&gameType=R&statType=avg&limit=10
    // GET /api/stats/batters/leaderboard?season=2024&gameType=PS&statType=homeRuns
    @GetMapping("/stats/batters/leaderboard")
    public ResponseEntity<List<BatterStatResponse>> getBatterLeaderboard(
            @RequestParam Integer season,
            @RequestParam(defaultValue = "R") String gameType,
            @RequestParam(defaultValue = "avg") String statType,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(statService.getBatterLeaderboard(season, gameType, statType, limit));
    }

    // GET /api/teams/135/batter-stats?season=2024&gameType=R
    // GET /api/teams/135/batter-stats?season=2024&gameType=PS
    @GetMapping("/teams/{teamId}/batter-stats")
    public ResponseEntity<List<BatterStatResponse>> getBatterStatsByTeam(
            @PathVariable Long teamId,
            @RequestParam Integer season,
            @RequestParam(defaultValue = "R") String gameType) {
        return ResponseEntity.ok(statService.getBatterStatsByTeam(teamId, season, gameType));
    }

    // ── 투수 스탯 ─────────────────────────────────────────────

    // GET /api/players/123/pitcher-stats?gameType=R
    // GET /api/players/123/pitcher-stats?gameType=PS
    // GET /api/players/123/pitcher-stats  (gameType 없으면 전체)
    @GetMapping("/players/{playerId}/pitcher-stats")
    public ResponseEntity<List<PitcherStatResponse>> getPitcherStats(
            @PathVariable Long playerId,
            @RequestParam(required = false) String gameType) {
        return ResponseEntity.ok(statService.getPitcherStats(playerId, gameType));
    }

    // GET /api/players/123/pitcher-stats/2024?gameType=R
    // GET /api/players/123/pitcher-stats/2024?gameType=PS
    @GetMapping("/players/{playerId}/pitcher-stats/{season}")
    public ResponseEntity<PitcherStatResponse> getPitcherStatBySeason(
            @PathVariable Long playerId,
            @PathVariable Integer season,
            @RequestParam(defaultValue = "R") String gameType) {
        return ResponseEntity.ok(statService.getPitcherStatBySeason(playerId, season, gameType));
    }

    // GET /api/stats/pitchers/leaderboard?season=2024&gameType=R&statType=era&limit=10
    // GET /api/stats/pitchers/leaderboard?season=2024&gameType=PS&statType=strikeOuts
    @GetMapping("/stats/pitchers/leaderboard")
    public ResponseEntity<List<PitcherStatResponse>> getPitcherLeaderboard(
            @RequestParam Integer season,
            @RequestParam(defaultValue = "R") String gameType,
            @RequestParam(defaultValue = "era") String statType,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(statService.getPitcherLeaderboard(season, gameType, statType, limit));
    }

    // GET /api/teams/135/pitcher-stats?season=2024&gameType=R
    // GET /api/teams/135/pitcher-stats?season=2024&gameType=PS
    @GetMapping("/teams/{teamId}/pitcher-stats")
    public ResponseEntity<List<PitcherStatResponse>> getPitcherStatsByTeam(
            @PathVariable Long teamId,
            @RequestParam Integer season,
            @RequestParam(defaultValue = "R") String gameType) {
        return ResponseEntity.ok(statService.getPitcherStatsByTeam(teamId, season, gameType));
    }
}