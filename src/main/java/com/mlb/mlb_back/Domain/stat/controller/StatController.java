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

    // 선수 시즌별 타자 스탯 전체
    // GET /api/players/123/batter-stats
    @GetMapping("/players/{playerId}/batter-stats")
    public ResponseEntity<List<BatterStatResponse>> getBatterStats(
            @PathVariable Long playerId) {
        return ResponseEntity.ok(statService.getBatterStats(playerId));
    }

    // 선수 특정 시즌 타자 스탯
    // GET /api/players/123/batter-stats/2024
    @GetMapping("/players/{playerId}/batter-stats/{season}")
    public ResponseEntity<BatterStatResponse> getBatterStatBySeason(
            @PathVariable Long playerId,
            @PathVariable Integer season) {
        return ResponseEntity.ok(statService.getBatterStatBySeason(playerId, season));
    }

    // 타자 리더보드
    // GET /api/stats/batters/leaderboard?season=2024&statType=avg&limit=10
    @GetMapping("/stats/batters/leaderboard")
    public ResponseEntity<List<BatterStatResponse>> getBatterLeaderboard(
            @RequestParam Integer season,
            @RequestParam(defaultValue = "avg") String statType,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(statService.getBatterLeaderboard(season, statType, limit));
    }

    // 팀별 타자 스탯
    // GET /api/teams/135/batter-stats?season=2024
    @GetMapping("/teams/{teamId}/batter-stats")
    public ResponseEntity<List<BatterStatResponse>> getBatterStatsByTeam(
            @PathVariable Long teamId,
            @RequestParam Integer season) {
        return ResponseEntity.ok(statService.getBatterStatsByTeam(teamId, season));
    }

    // ── 투수 스탯 ─────────────────────────────────────────────

    // 선수 시즌별 투수 스탯 전체
    // GET /api/players/123/pitcher-stats
    @GetMapping("/players/{playerId}/pitcher-stats")
    public ResponseEntity<List<PitcherStatResponse>> getPitcherStats(
            @PathVariable Long playerId) {
        return ResponseEntity.ok(statService.getPitcherStats(playerId));
    }

    // 선수 특정 시즌 투수 스탯
    // GET /api/players/123/pitcher-stats/2024
    @GetMapping("/players/{playerId}/pitcher-stats/{season}")
    public ResponseEntity<PitcherStatResponse> getPitcherStatBySeason(
            @PathVariable Long playerId,
            @PathVariable Integer season) {
        return ResponseEntity.ok(statService.getPitcherStatBySeason(playerId, season));
    }

    // 투수 리더보드
    // GET /api/stats/pitchers/leaderboard?season=2024&statType=era&limit=10
    @GetMapping("/stats/pitchers/leaderboard")
    public ResponseEntity<List<PitcherStatResponse>> getPitcherLeaderboard(
            @RequestParam Integer season,
            @RequestParam(defaultValue = "era") String statType,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(statService.getPitcherLeaderboard(season, statType, limit));
    }

    // 팀별 투수 스탯
    // GET /api/teams/135/pitcher-stats?season=2024
    @GetMapping("/teams/{teamId}/pitcher-stats")
    public ResponseEntity<List<PitcherStatResponse>> getPitcherStatsByTeam(
            @PathVariable Long teamId,
            @RequestParam Integer season) {
        return ResponseEntity.ok(statService.getPitcherStatsByTeam(teamId, season));
    }
}