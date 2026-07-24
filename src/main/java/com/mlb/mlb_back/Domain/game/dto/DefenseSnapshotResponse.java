package com.mlb.mlb_back.Domain.game.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * 특정 이닝(초/말) 시점의 수비 상황 스냅샷.
 * - 그 시점까지의 타임라인(투수교체·수비교체 이벤트)을 순서대로 반영해 재구성한 "당시 수비"이며,
 *   투수 교체는 정확히 반영되지만 그 외 수비 위치 교체는 해당 경기의 최종 수비 위치를 기준으로 한
 *   최선의 추정치입니다.
 */
@Getter
@Builder
public class DefenseSnapshotResponse {

    private Long gameId;
    private Integer inning;
    private String halfInning; // "top" / "bottom"

    private Long battingTeamId;
    private String battingTeamName;
    private String battingTeamAbbreviation;

    private Long fieldingTeamId;
    private String fieldingTeamName;
    private String fieldingTeamAbbreviation;

    private Integer awayScore;
    private Integer homeScore;

    private Integer balls;
    private Integer strikes;
    private Integer outs;

    /** 그 시점 수비 라인업 (9자리: P, C, 1B, 2B, 3B, SS, LF, CF, RF) */
    private List<DefensePlayer> positions;

    private LineupPlayer currentBatter;
    private LineupPlayer onDeck;
    private LineupPlayer inHole;

    @Getter
    @Builder
    public static class DefensePlayer {
        private String position;   // P, C, 1B, 2B, 3B, SS, LF, CF, RF
        private Long playerId;
        private String playerName;
        private String photoUrl;
    }

    @Getter
    @Builder
    public static class LineupPlayer {
        private Long playerId;
        private String playerName;
        private String photoUrl;
        private Integer battingOrder; // 1~9
    }
}