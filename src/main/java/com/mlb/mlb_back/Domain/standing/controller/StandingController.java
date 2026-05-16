package com.mlb.mlb_back.Domain.standing.controller;

import com.mlb.mlb_back.Domain.standing.dto.StandingResponse;
import com.mlb.mlb_back.Domain.standing.service.StandingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/standings")
@RequiredArgsConstructor
public class StandingController {

    private final StandingService standingService;

    // 시즌 전체 순위
    // GET /api/standings/2025
    @GetMapping("/{season}")
    public ResponseEntity<List<StandingResponse>> getStandings(@PathVariable Integer season) {
        return ResponseEntity.ok(standingService.getStandings(season));
    }

    // 리그별 순위
    // GET /api/standings/2025/league/American League
    @GetMapping("/{season}/league/{league}")
    public ResponseEntity<List<StandingResponse>> getStandingsByLeague(
            @PathVariable Integer season,
            @PathVariable String league) {
        return ResponseEntity.ok(standingService.getStandingsByLeague(season, league));
    }

    // 지구별 순위
    // GET /api/standings/2025/division/AL East
    @GetMapping("/{season}/division/{division}")
    public ResponseEntity<List<StandingResponse>> getStandingsByDivision(
            @PathVariable Integer season,
            @PathVariable String division) {
        return ResponseEntity.ok(standingService.getStandingsByDivision(season, division));
    }
}