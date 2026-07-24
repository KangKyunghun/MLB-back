package com.mlb.mlb_back.Domain.chat.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class TimelineEvent {

    /**
     * 이벤트 타입
     * PITCH            - 개별 투구 (볼/스트라이크/타격)
     * PLAY             - 타석 결과 (아웃, 안타 등)
     * SCORE_CHANGE     - 득점 발생
     * STOLEN_BASE      - 도루 성공
     * CAUGHT_STEALING  - 도루 실패
     * PITCHING_CHANGE  - 투수 교체
     * PINCH_HITTER     - 대타
     * PINCH_RUNNER     - 대주자
     * GAME_END         - 경기 종료
     */
    private String type;

    private Integer inning;
    private String halfInning;          // "top"(초) / "bottom"(말)
    private String atBatIndex;

    // ── 타석 정보 ──────────────────────────────
    private String batterName;          // 타자 이름
    private String pitcherName;         // 투수 이름

    // ── 타석 결과 (PLAY / SCORE_CHANGE) ────────
    private String description;         // 예: "유격수 땅볼 아웃", "좌중간 2루타"
    private Integer rbi;
    private Integer homeScore;
    private Integer awayScore;
    private List<String> scoringPlayers; // 득점 선수 이름 목록

    // ── 개별 투구 정보 (PITCH) ──────────────────
    private List<Pitch> pitches;        // 타석 전체 투구 시퀀스

    // ── 도루 (STOLEN_BASE / CAUGHT_STEALING) ───
    private String stolenBasePlayer;
    private String stolenBase;          // "2B", "3B", "HOME"

    // ── 투수 교체 (PITCHING_CHANGE) ────────────
    private String outgoingPitcher;
    private String incomingPitcher;

    // ── 대타/대주자 (PINCH_HITTER / PINCH_RUNNER) ─
    private String substituteName;      // 들어오는 선수
    private String replacedName;        // 빠지는 선수

    // ── 볼카운트 스냅샷 ──────────────────────────
    private Integer balls;
    private Integer strikes;
    private Integer outs;

    @Getter
    @Builder
    public static class Pitch {
        private Integer pitchNumber;

        // 투구 결과
        private String callCode;        // B(볼), S(스트라이크), X(타격), T(파울팁), F(파울) 등
        private String callDescription; // "볼", "스트라이크(헛스윙)", "타격" 등 (한국어)

        // 구종/구속
        private String pitchTypeCode;   // FF(포심), SL(슬라이더), CH(체인지업), CU(커브) 등
        private String pitchTypeName;   // 한국어 구종명
        private Double startSpeed;      // 구속 (mph)

        // 볼카운트 (이 투구 후)
        private Integer balls;
        private Integer strikes;
        private Integer outs;

        // 인플레이 여부
        private Boolean isInPlay;
        private Boolean isStrike;
        private Boolean isBall;
    }
}