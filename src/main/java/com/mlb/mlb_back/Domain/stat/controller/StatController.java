package com.mlb.mlb_back.Domain.stat.controller;

import com.mlb.mlb_back.Domain.stat.dto.*;
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

    // GET /api/teams/{teamId}/pitcher-stats?season=2024&gameType=R
    @GetMapping("/teams/{teamId}/pitcher-stats")
    public ResponseEntity<List<PitcherStatResponse>> getPitcherStatsByTeam(
            @PathVariable Long teamId,
            @RequestParam Integer season,
            @RequestParam(defaultValue = "R") String gameType) {
        return ResponseEntity.ok(statService.getPitcherStatsByTeam(teamId, season, gameType));
    }

    // ── 핫콜드존 ──────────────────────────────────────────────

    // GET /api/players/{playerId}/hot-cold-zones?season=2024&gameType=R
    @GetMapping("/players/{playerId}/hot-cold-zones")
    public ResponseEntity<HotColdZoneResponse> getHotColdZone(
            @PathVariable Long playerId,
            @RequestParam Integer season,
            @RequestParam(defaultValue = "R") String gameType) {
        return ResponseEntity.ok(statService.getHotColdZone(playerId, season, gameType));
    }

    // GET /api/players/{playerId}/hot-cold-zones/all
    @GetMapping("/players/{playerId}/hot-cold-zones/all")
    public ResponseEntity<List<HotColdZoneResponse>> getHotColdZonesAll(
            @PathVariable Long playerId) {
        return ResponseEntity.ok(statService.getHotColdZonesAll(playerId));
    }

    // ── 스프레이차트 ───────────────────────────────────────────

    // GET /api/players/{playerId}/spray-data?season=2024&gameType=R
    @GetMapping("/players/{playerId}/spray-data")
    public ResponseEntity<List<SprayDataResponse>> getSprayData(
            @PathVariable Long playerId,
            @RequestParam Integer season,
            @RequestParam(defaultValue = "R") String gameType) {
        return ResponseEntity.ok(statService.getSprayData(playerId, season, gameType));
    }

    // ── 월간 스탯 ──────────────────────────────────────────────

    // GET /api/players/{playerId}/monthly-stats?season=2024
    @GetMapping("/players/{playerId}/monthly-stats")
    public ResponseEntity<List<PlayerMonthlyStatResponse>> getMonthlyStats(
            @PathVariable Long playerId,
            @RequestParam Integer season) {
        return ResponseEntity.ok(statService.getMonthlyStats(playerId, season));
    }

    // ── 스플릿 스탯 (득점권/좌우투수) ────────────────────────────

    // GET /api/players/{playerId}/situation-stats?season=2024&gameType=R
    @GetMapping("/players/{playerId}/situation-stats")
    public ResponseEntity<List<BatterSituationStatResponse>> getSituationStats(
            @PathVariable Long playerId,
            @RequestParam Integer season,
            @RequestParam(defaultValue = "R") String gameType) {
        return ResponseEntity.ok(statService.getSituationStats(playerId, season, gameType));
    }

    // GET /api/players/{playerId}/situation-stats/{sitCode}?season=2024&gameType=R
    // sitCode: risp(득점권), vl(좌투상대), vr(우투상대)
    @GetMapping("/players/{playerId}/situation-stats/{sitCode}")
    public ResponseEntity<BatterSituationStatResponse> getSituationStatBySitCode(
            @PathVariable Long playerId,
            @PathVariable String sitCode,
            @RequestParam Integer season,
            @RequestParam(defaultValue = "R") String gameType) {
        return ResponseEntity.ok(statService.getSituationStatBySitCode(playerId, season, sitCode, gameType));
    }

    // ── 타자 vs 투수 ───────────────────────────────────────────

    // GET /api/players/{batterId}/vs-pitcher?season=2024&gameType=R
    @GetMapping("/players/{batterId}/vs-pitcher")
    public ResponseEntity<List<BatterVsPitcherResponse>> getBatterVsPitcher(
            @PathVariable Long batterId,
            @RequestParam Integer season,
            @RequestParam(defaultValue = "R") String gameType) {
        return ResponseEntity.ok(statService.getBatterVsPitcher(batterId, season, gameType));
    }

    // GET /api/players/{pitcherId}/vs-batter?season=2024&gameType=R
    @GetMapping("/players/{pitcherId}/vs-batter")
    public ResponseEntity<List<BatterVsPitcherResponse>> getPitcherVsBatter(
            @PathVariable Long pitcherId,
            @RequestParam Integer season,
            @RequestParam(defaultValue = "R") String gameType) {
        return ResponseEntity.ok(statService.getPitcherVsBatter(pitcherId, season, gameType));
    }

    // GET /api/players/{batterId}/vs-pitcher/{pitcherId}?season=2024&gameType=R
    @GetMapping("/players/{batterId}/vs-pitcher/{pitcherId}")
    public ResponseEntity<BatterVsPitcherResponse> getBatterVsPitcherDetail(
            @PathVariable Long batterId,
            @PathVariable Long pitcherId,
            @RequestParam Integer season,
            @RequestParam(defaultValue = "R") String gameType) {
        return ResponseEntity.ok(statService.getBatterVsPitcherDetail(batterId, pitcherId, season, gameType));
    }
}