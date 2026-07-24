package com.mlb.mlb_back.Domain.admin.controller;

import com.mlb.mlb_back.Global.config.DataInitializer;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 운영/유지보수용 수동 트리거 엔드포인트 모음.
 * 프론트에 노출되지 않고, 개발자가 직접(Postman 등) 호출하는 용도.
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final DataInitializer dataInitializer;

    /**
     * 특정 시즌 전체 box_score의 game_position을 일괄 백필.
     * 경기 수가 많으면 오래 걸릴 수 있어 백그라운드(@Async)로 돌고, 이 요청은 바로 응답한다.
     * 진행 상황은 애플리케이션 로그에서 "gamePosition 배치 백필"로 검색해서 확인.
     *
     * POST /api/admin/backfill/game-positions?season=2024
     */
    @PostMapping("/backfill/game-positions")
    public ResponseEntity<Map<String, Object>> backfillGamePositions(
            @RequestParam int season) {

        dataInitializer.backfillGamePositionsForSeason(season);

        return ResponseEntity.accepted().body(Map.of(
                "season", season,
                "status", "STARTED",
                "message", season + " 시즌 gamePosition 백필을 백그라운드에서 시작했습니다. "
                        + "진행 상황은 서버 로그를 확인하세요."
        ));
    }
}