package com.football.Football_back.Domain.league.controller;

import com.football.Football_back.Domain.league.dto.LeagueResponse;
import com.football.Football_back.Domain.league.service.LeagueService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/leagues")
@RequiredArgsConstructor
public class LeagueController {

    private final LeagueService leagueService;

    // 전체 리그 목록 조회
    @GetMapping
    public ResponseEntity<?> getAllLeagues() {
        return ResponseEntity.ok(leagueService.getAllLeagues());
    }

    // 특정 리그 조회
    @GetMapping("/{leagueId}")
    public ResponseEntity<?> getLeagueById(@PathVariable Long leagueId) {
        return ResponseEntity.ok(leagueService.getLeagueById(leagueId));
    }
    
}
