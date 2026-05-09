package com.football.Football_back.Domain.player.controller;

import com.football.Football_back.Domain.player.dto.PlayerResponse;
import com.football.Football_back.Domain.player.service.PlayerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/players")
@RequiredArgsConstructor
public class PlayerController {

    private final PlayerService playerService;

    // 선수 이름 검색
    // GET /api/players/search?name=salah
    @GetMapping("/search")
    public ResponseEntity<List<PlayerResponse>> searchPlayers(
            @RequestParam String name) {
        return ResponseEntity.ok(playerService.searchPlayers(name));
    }

    // 선수 필터 검색
    // GET /api/players/filter?position=Forward&nationality=Korean&teamId=65
    @GetMapping("/filter")
    public ResponseEntity<List<PlayerResponse>> filterPlayers(
            @RequestParam(required = false) String position,
            @RequestParam(required = false) String nationality,
            @RequestParam(required = false) Long teamId) {
        return ResponseEntity.ok(playerService.filterPlayers(position, nationality, teamId));
    }

    // 팀별 선수 목록
    // GET /api/players/team/65
    @GetMapping("/team/{teamId}")
    public ResponseEntity<List<PlayerResponse>> getPlayersByTeam(
            @PathVariable Long teamId) {
        return ResponseEntity.ok(playerService.getPlayersByTeam(teamId));
    }

    // 포지션별 선수 목록
    // GET /api/players/position/Centre-Forward
    @GetMapping("/position/{position}")
    public ResponseEntity<List<PlayerResponse>> getPlayersByPosition(
            @PathVariable String position) {
        return ResponseEntity.ok(playerService.getPlayersByPosition(position));
    }

    // 국적별 선수 목록
    // GET /api/players/nationality/England
    @GetMapping("/nationality/{nationality}")
    public ResponseEntity<List<PlayerResponse>> getPlayersByNationality(
            @PathVariable String nationality) {
        return ResponseEntity.ok(playerService.getPlayersByNationality(nationality));
    }

    // 특정 선수 조회
    // GET /api/players/123
    @GetMapping("/{playerId}")
    public ResponseEntity<PlayerResponse> getPlayer(@PathVariable Long playerId) {
        return ResponseEntity.ok(playerService.getPlayer(playerId));
    }
}