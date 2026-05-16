package com.mlb.mlb_back.Domain.player.controller;

import com.mlb.mlb_back.Domain.player.dto.PlayerResponse;
import com.mlb.mlb_back.Domain.player.service.PlayerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/players")
@RequiredArgsConstructor
public class PlayerController {

    private final PlayerService playerService;

    // 선수 검색
    // GET /api/players/search?keyword=ohtani
    @GetMapping("/search")
    public ResponseEntity<List<PlayerResponse>> searchPlayers(@RequestParam String keyword) {
        return ResponseEntity.ok(playerService.searchPlayers(keyword));
    }

    // 활성 선수 목록
    // GET /api/players/active
    @GetMapping("/active")
    public ResponseEntity<List<PlayerResponse>> getActivePlayers() {
        return ResponseEntity.ok(playerService.getActivePlayers());
    }

    // 팀별 선수 목록
    // GET /api/players/team/147
    @GetMapping("/team/{teamId}")
    public ResponseEntity<List<PlayerResponse>> getPlayersByTeam(@PathVariable Long teamId) {
        return ResponseEntity.ok(playerService.getPlayersByTeam(teamId));
    }

    // 포지션별 선수 목록
    // GET /api/players/position/SP
    @GetMapping("/position/{position}")
    public ResponseEntity<List<PlayerResponse>> getPlayersByPosition(@PathVariable String position) {
        return ResponseEntity.ok(playerService.getPlayersByPosition(position));
    }

    // 선수 상세
    // GET /api/players/660271
    @GetMapping("/{playerId}")
    public ResponseEntity<PlayerResponse> getPlayer(@PathVariable Long playerId) {
        return ResponseEntity.ok(playerService.getPlayer(playerId));
    }
}