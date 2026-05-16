package com.mlb.mlb_back.Domain.game.controller;

import com.mlb.mlb_back.Domain.game.dto.BoxScoreResponse;
import com.mlb.mlb_back.Domain.game.dto.GameResponse;
import com.mlb.mlb_back.Domain.game.dto.LineScoreResponse;
import com.mlb.mlb_back.Domain.game.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/games")
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;

    // 오늘 경기 목록
    // GET /api/games/today
    @GetMapping("/today")
    public ResponseEntity<List<GameResponse>> getTodayGames() {
        return ResponseEntity.ok(gameService.getTodayGames());
    }

    // 시즌별 경기 목록
    // GET /api/games/season/2025
    @GetMapping("/season/{season}")
    public ResponseEntity<List<GameResponse>> getGamesBySeason(@PathVariable Integer season) {
        return ResponseEntity.ok(gameService.getGamesBySeason(season));
    }

    // 시즌 + 상태별 경기 목록
    // GET /api/games/season/2025/status/Final
    @GetMapping("/season/{season}/status/{status}")
    public ResponseEntity<List<GameResponse>> getGamesBySeasonAndStatus(
            @PathVariable Integer season,
            @PathVariable String status) {
        return ResponseEntity.ok(gameService.getGamesBySeasonAndStatus(season, status));
    }

    // 날짜별 경기 목록
    // GET /api/games/date/2025-05-15
    @GetMapping("/date/{date}")
    public ResponseEntity<List<GameResponse>> getGamesByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(gameService.getGamesByDate(date));
    }

    // 팀별 경기 목록
    // GET /api/games/team/147
    @GetMapping("/team/{teamId}")
    public ResponseEntity<List<GameResponse>> getGamesByTeam(@PathVariable Long teamId) {
        return ResponseEntity.ok(gameService.getGamesByTeam(teamId));
    }

    // 경기 박스스코어
    // GET /api/games/745453/boxscore
    @GetMapping("/{gameId}/boxscore")
    public ResponseEntity<List<BoxScoreResponse>> getBoxScore(@PathVariable Long gameId) {
        return ResponseEntity.ok(gameService.getBoxScore(gameId));
    }

    // 경기 라인스코어
    // GET /api/games/745453/linescore
    @GetMapping("/{gameId}/linescore")
    public ResponseEntity<List<LineScoreResponse>> getLineScore(@PathVariable Long gameId) {
        return ResponseEntity.ok(gameService.getLineScore(gameId));
    }

    // 경기 상세
    // GET /api/games/745453
    @GetMapping("/{gameId}")
    public ResponseEntity<GameResponse> getGame(@PathVariable Long gameId) {
        return ResponseEntity.ok(gameService.getGame(gameId));
    }
}