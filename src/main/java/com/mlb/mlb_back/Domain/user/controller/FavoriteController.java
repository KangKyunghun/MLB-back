package com.mlb.mlb_back.Domain.user.controller;

import com.mlb.mlb_back.Domain.user.dto.FavoriteResponse;
import com.mlb.mlb_back.Domain.user.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    // 즐겨찾기 추가
    // POST /api/favorites?targetId=660271&targetType=PLAYER
    @PostMapping
    public ResponseEntity<FavoriteResponse> addFavorite(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam Long targetId,
            @RequestParam String targetType) {
        return ResponseEntity.ok(favoriteService.addFavorite(userDetails.getUsername(), targetId, targetType));
    }

    // 즐겨찾기 삭제
    // DELETE /api/favorites?targetId=660271&targetType=PLAYER
    @DeleteMapping
    public ResponseEntity<Void> removeFavorite(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam Long targetId,
            @RequestParam String targetType) {
        favoriteService.removeFavorite(userDetails.getUsername(), targetId, targetType);
        return ResponseEntity.noContent().build();
    }

    // 내 즐겨찾기 전체 조회
    // GET /api/favorites
    @GetMapping
    public ResponseEntity<List<FavoriteResponse>> getMyFavorites(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(favoriteService.getMyFavorites(userDetails.getUsername()));
    }

    // 내 즐겨찾기 타입별 조회
    // GET /api/favorites/type/PLAYER
    @GetMapping("/type/{targetType}")
    public ResponseEntity<List<FavoriteResponse>> getMyFavoritesByType(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String targetType) {
        return ResponseEntity.ok(favoriteService.getMyFavoritesByType(userDetails.getUsername(), targetType));
    }

    // 즐겨찾기 여부 확인
    // GET /api/favorites/check?targetId=660271&targetType=PLAYER
    @GetMapping("/check")
    public ResponseEntity<Boolean> isFavorite(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam Long targetId,
            @RequestParam String targetType) {
        return ResponseEntity.ok(favoriteService.isFavorite(userDetails.getUsername(), targetId, targetType));
    }

    // 특정 선수/팀 즐겨찾기 수
    // GET /api/favorites/count?targetId=660271&targetType=PLAYER
    @GetMapping("/count")
    public ResponseEntity<Long> getFavoriteCount(
            @RequestParam Long targetId,
            @RequestParam String targetType) {
        return ResponseEntity.ok(favoriteService.getFavoriteCount(targetId, targetType));
    }
}