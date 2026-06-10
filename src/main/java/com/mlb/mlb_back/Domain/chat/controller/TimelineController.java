package com.mlb.mlb_back.Domain.chat.controller;

import com.mlb.mlb_back.Domain.chat.dto.TimelineEvent;
import com.mlb.mlb_back.Domain.chat.service.TimelineService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/timeline")
@RequiredArgsConstructor
public class TimelineController {

    private final TimelineService timelineService;

    /**
     * 특정 경기 타임라인 전체 조회 (최초 진입 시 기존 플레이 로드)
     * GET /api/timeline/{gamePk}
     * 이후 실시간 업데이트는 WebSocket /topic/timeline/{gamePk} 구독
     */
    @GetMapping("/{gamePk}")
    public ResponseEntity<List<TimelineEvent>> getTimeline(@PathVariable Long gamePk) {
        return ResponseEntity.ok(timelineService.getFullTimeline(gamePk));
    }
}