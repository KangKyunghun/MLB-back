package com.mlb.mlb_back.Domain.chat.service;

import com.mlb.mlb_back.Domain.chat.dto.TimelineEvent;
import com.mlb.mlb_back.Domain.chat.dto.TimelineEvent.Pitch;
import com.mlb.mlb_back.Domain.chat.entity.ChatRoom;
import com.mlb.mlb_back.Domain.chat.repository.ChatRoomRepository;
import com.mlb.mlb_back.Domain.game.repository.GameRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TimelineService {

    private final WebClient webClient;
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatRoomRepository chatRoomRepository;
    private final GameRepository gameRepository;
    private final ChatService chatService;

    // 마지막으로 처리한 플레이 인덱스 (gamePk → atBatIndex)
    private final Map<Long, Integer> lastPlayIndex = new ConcurrentHashMap<>();

    // ── 구종 코드 → 한국어 매핑 ──────────────────────────────────────
    private static final Map<String, String> PITCH_TYPE_MAP = Map.ofEntries(
            Map.entry("FF", "포심 패스트볼"),
            Map.entry("FT", "투심 패스트볼"),
            Map.entry("FC", "컷 패스트볼"),
            Map.entry("SI", "싱커"),
            Map.entry("FS", "스플리터"),
            Map.entry("SL", "슬라이더"),
            Map.entry("ST", "스위퍼"),
            Map.entry("CU", "커브"),
            Map.entry("KC", "너클커브"),
            Map.entry("CH", "체인지업"),
            Map.entry("KN", "너클볼"),
            Map.entry("EP", "엡이파니"),
            Map.entry("SC", "스크류볼"),
            Map.entry("PO", "피치아웃"),
            Map.entry("UN", "불명확")
    );

    // ── 투구 결과 코드 → 한국어 매핑 ────────────────────────────────
    private static final Map<String, String> CALL_CODE_MAP = Map.ofEntries(
            Map.entry("B",  "볼"),
            Map.entry("*B", "볼 (흙볼)"),
            Map.entry("S",  "스트라이크 (헛스윙)"),
            Map.entry("C",  "스트라이크 (루킹)"),
            Map.entry("F",  "파울"),
            Map.entry("T",  "파울팁"),
            Map.entry("X",  "타격"),
            Map.entry("D",  "파울 (번트)"),
            Map.entry("E",  "파울 (번트)"),
            Map.entry("Q",  "스트라이크 (번트)"),
            Map.entry("L",  "파울팁 (번트)"),
            Map.entry("P",  "피치아웃"),
            Map.entry("M",  "피치아웃 (타격)"),
            Map.entry("N",  "피치아웃 (번트)"),
            Map.entry("O",  "파울 (번트, 스트라이크)"),
            Map.entry("R",  "파울 (번트, 스트라이크)"),
            Map.entry("I",  "고의 볼"),
            Map.entry("H",  "타자 맞음 (HBP)"),
            Map.entry("K",  "스트라이크 (알 수 없음)")
    );

    // ── 타석 결과 영어 → 한국어 매핑 ────────────────────────────────
    private static final Map<String, String> EVENT_MAP = Map.ofEntries(
            Map.entry("Single",                  "1루타"),
            Map.entry("Double",                  "2루타"),
            Map.entry("Triple",                  "3루타"),
            Map.entry("Home Run",                "홈런"),
            Map.entry("Strikeout",               "삼진 아웃"),
            Map.entry("Strikeout - DP",          "삼진 병살"),
            Map.entry("Walk",                    "볼넷"),
            Map.entry("Intent Walk",             "고의 볼넷"),
            Map.entry("Hit By Pitch",            "몸에 맞는 공"),
            Map.entry("Field Error",             "실책 출루"),
            Map.entry("Fielders Choice",         "야수 선택"),
            Map.entry("Fielders Choice Out",     "야수 선택 아웃"),
            Map.entry("Grounded Into DP",        "병살타"),
            Map.entry("Double Play",             "병살"),
            Map.entry("Triple Play",             "삼중살"),
            Map.entry("Groundout",               "땅볼 아웃"),
            Map.entry("Flyout",                  "플라이 아웃"),
            Map.entry("Lineout",                 "라인드라이브 아웃"),
            Map.entry("Pop Out",                 "내야 플라이 아웃"),
            Map.entry("Forceout",                "포스 아웃"),
            Map.entry("Bunt Groundout",          "번트 땅볼 아웃"),
            Map.entry("Bunt Pop Out",            "번트 플라이 아웃"),
            Map.entry("Sac Fly",                 "희생 플라이"),
            Map.entry("Sac Fly DP",              "희생 플라이 병살"),
            Map.entry("Sac Bunt",                "희생 번트"),
            Map.entry("Sac Bunt DP",             "희생 번트 병살"),
            Map.entry("Catcher Interference",    "포수 방해"),
            Map.entry("Fan interference",        "관중 방해"),
            Map.entry("Stolen Base 2B",          "2루 도루"),
            Map.entry("Stolen Base 3B",          "3루 도루"),
            Map.entry("Stolen Base Home",        "홈 도루"),
            Map.entry("Caught Stealing 2B",      "2루 도루 실패"),
            Map.entry("Caught Stealing 3B",      "3루 도루 실패"),
            Map.entry("Caught Stealing Home",    "홈 도루 실패"),
            Map.entry("Pickoff 1B",              "1루 견제 아웃"),
            Map.entry("Pickoff 2B",              "2루 견제 아웃"),
            Map.entry("Pickoff 3B",              "3루 견제 아웃"),
            Map.entry("Wild Pitch",              "폭투"),
            Map.entry("Passed Ball",             "포일"),
            Map.entry("Balk",                    "보크"),
            Map.entry("Pitching Substitution",   "투수 교체"),
            Map.entry("Offensive Substitution",  "공격 교체"),
            Map.entry("Defensive Sub",           "수비 교체"),
            Map.entry("Defensive Switch",        "수비 위치 변경")
    );

    // ────────────────────────────────────────────────────────────────

    /**
     * Live 경기(isActive=true) 타임라인 갱신 (스케줄러에서 호출)
     */
    public void refreshTimelines() {
        List<ChatRoom> liveRooms = chatRoomRepository.findByIsActiveTrue();
        for (ChatRoom room : liveRooms) {
            Long gamePk = room.getGame().getId();
            try {
                fetchAndBroadcastTimeline(gamePk, room.getId());
            } catch (Exception e) {
                log.error("타임라인 갱신 실패 gamePk={}: {}", gamePk, e.getMessage());
            }
        }
    }

    /**
     * 특정 경기 타임라인 전체 조회 (클라이언트 최초 진입 시)
     */
    @SuppressWarnings("unchecked")
    public List<TimelineEvent> getFullTimeline(Long gamePk) {
        List<TimelineEvent> events = new ArrayList<>();
        try {
            Map<String, Object> response = fetchPlayByPlay(gamePk);
            if (response == null) return events;

            List<Map<String, Object>> allPlays =
                    (List<Map<String, Object>>) response.get("allPlays");
            if (allPlays == null) return events;

            for (Map<String, Object> play : allPlays) {
                events.addAll(parseAllEvents(play));
            }
        } catch (Exception e) {
            log.error("타임라인 전체 조회 실패 gamePk={}: {}", gamePk, e.getMessage());
        }
        return events;
    }

    // ── private ──────────────────────────────────────────────────────

    @SuppressWarnings("unchecked")
    private void fetchAndBroadcastTimeline(Long gamePk, Long chatRoomId) {
        Map<String, Object> response = fetchPlayByPlay(gamePk);
        if (response == null) return;

        List<Map<String, Object>> allPlays =
                (List<Map<String, Object>>) response.get("allPlays");
        if (allPlays == null || allPlays.isEmpty()) return;

        int lastIndex = lastPlayIndex.getOrDefault(gamePk, -1);

        for (Map<String, Object> play : allPlays) {
            Map<String, Object> about = (Map<String, Object>) play.get("about");
            if (about == null) continue;
            int atBatIndex = ((Number) about.get("atBatIndex")).intValue();
            if (atBatIndex <= lastIndex) continue;

            List<TimelineEvent> events = parseAllEvents(play);
            for (TimelineEvent event : events) {
                messagingTemplate.convertAndSend("/topic/timeline/" + gamePk, event);

                // 득점 시 채팅방 시스템 메시지
                if ("SCORE_CHANGE".equals(event.getType())) {
                    String msg = String.format("⚾ 득점! [%d이닝 %s] %s (%d - %d)",
                            event.getInning(),
                            "top".equals(event.getHalfInning()) ? "초" : "말",
                            event.getDescription(),
                            event.getAwayScore(),
                            event.getHomeScore());
                    chatService.sendSystemMessage(chatRoomId, msg);
                }

                // 투수 교체 시 채팅방 시스템 메시지
                if ("PITCHING_CHANGE".equals(event.getType())) {
                    String msg = String.format("🔄 투수 교체: %s → %s",
                            event.getOutgoingPitcher(), event.getIncomingPitcher());
                    chatService.sendSystemMessage(chatRoomId, msg);
                }
            }

            lastPlayIndex.put(gamePk, atBatIndex);
        }

        checkGameEnd(gamePk, chatRoomId, response);
    }

    /**
     * 하나의 play에서 발생하는 모든 이벤트 파싱
     * playEvents 배열에서 투구(pitch) + 액션(action) + 타석결과 순서대로 반환
     */
    @SuppressWarnings("unchecked")
    private List<TimelineEvent> parseAllEvents(Map<String, Object> play) {
        List<TimelineEvent> events = new ArrayList<>();
        try {
            Map<String, Object> about   = (Map<String, Object>) play.get("about");
            Map<String, Object> result  = (Map<String, Object>) play.get("result");
            Map<String, Object> matchup = (Map<String, Object>) play.get("matchup");
            if (about == null || result == null) return events;

            int    inning     = ((Number) about.get("inning")).intValue();
            String halfInning = (String) about.get("halfInning");
            String atBatIndex = String.valueOf(((Number) about.get("atBatIndex")).intValue());

            String batterName  = extractPlayerName(matchup, "batter");
            String pitcherName = extractPlayerName(matchup, "pitcher");

            List<Map<String, Object>> playEvents =
                    (List<Map<String, Object>>) play.get("playEvents");

            // 투구 시퀀스 전체 수집 (타석 결과 이벤트에 함께 포함)
            List<Pitch> pitchSequence = new ArrayList<>();

            if (playEvents != null) {
                for (Map<String, Object> pe : playEvents) {
                    String peType = (String) pe.get("type");

                    if ("pitch".equals(peType)) {
                        // 개별 투구 이벤트
                        Pitch pitch = parsePitch(pe);
                        if (pitch != null) {
                            pitchSequence.add(pitch);

                            // 개별 투구도 이벤트로 전송 (프론트에서 볼/스트라이크 실시간 표시용)
                            events.add(TimelineEvent.builder()
                                    .type("PITCH")
                                    .inning(inning)
                                    .halfInning(halfInning)
                                    .atBatIndex(atBatIndex)
                                    .batterName(batterName)
                                    .pitcherName(pitcherName)
                                    .pitches(List.of(pitch))
                                    .balls(pitch.getBalls())
                                    .strikes(pitch.getStrikes())
                                    .outs(pitch.getOuts())
                                    .build());
                        }

                    } else if ("action".equals(peType)) {
                        // 액션 이벤트: 투수교체, 대타, 대주자, 도루 등
                        TimelineEvent actionEvent = parseAction(
                                pe, inning, halfInning, atBatIndex);
                        if (actionEvent != null) events.add(actionEvent);
                    }
                }
            }

            // 타석 결과 이벤트 (마지막에 추가)
            TimelineEvent playResult = parsePlayResult(
                    play, inning, halfInning, atBatIndex,
                    batterName, pitcherName, pitchSequence);
            if (playResult != null) events.add(playResult);

        } catch (Exception e) {
            log.warn("이벤트 파싱 실패: {}", e.getMessage());
        }
        return events;
    }

    /**
     * 개별 투구 파싱 (구종, 구속, 볼/스트라이크)
     */
    @SuppressWarnings("unchecked")
    private Pitch parsePitch(Map<String, Object> pe) {
        try {
            Map<String, Object> details   = (Map<String, Object>) pe.get("details");
            Map<String, Object> count     = (Map<String, Object>) pe.get("count");
            Map<String, Object> pitchData = (Map<String, Object>) pe.get("pitchData");
            if (details == null) return null;

            // 투구 결과
            Map<String, Object> call = (Map<String, Object>) details.get("call");
            String callCode = call != null ? (String) call.get("code") : "";
            String callDesc = CALL_CODE_MAP.getOrDefault(callCode, callCode);

            // 구종
            Map<String, Object> pitchType = (Map<String, Object>) details.get("type");
            String pitchTypeCode = pitchType != null ? (String) pitchType.get("code") : null;
            String pitchTypeName = pitchTypeCode != null
                    ? PITCH_TYPE_MAP.getOrDefault(pitchTypeCode, pitchTypeCode) : null;

            // 구속 (mph)
            Double startSpeed = null;
            if (pitchData != null && pitchData.get("startSpeed") != null) {
                startSpeed = ((Number) pitchData.get("startSpeed")).doubleValue();
            }

            // 볼카운트
            int balls   = count != null && count.get("balls")   != null ? ((Number) count.get("balls")).intValue()   : 0;
            int strikes = count != null && count.get("strikes") != null ? ((Number) count.get("strikes")).intValue() : 0;
            int outs    = count != null && count.get("outs")    != null ? ((Number) count.get("outs")).intValue()    : 0;

            Object pitchNum = pe.get("pitchNumber");

            return Pitch.builder()
                    .pitchNumber(pitchNum != null ? ((Number) pitchNum).intValue() : null)
                    .callCode(callCode)
                    .callDescription(callDesc)
                    .pitchTypeCode(pitchTypeCode)
                    .pitchTypeName(pitchTypeName)
                    .startSpeed(startSpeed)
                    .balls(balls)
                    .strikes(strikes)
                    .outs(outs)
                    .isInPlay((Boolean) details.getOrDefault("isInPlay", false))
                    .isStrike((Boolean) details.getOrDefault("isStrike", false))
                    .isBall((Boolean) details.getOrDefault("isBall", false))
                    .build();
        } catch (Exception e) {
            log.warn("투구 파싱 실패: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 액션 이벤트 파싱 (투수교체, 대타, 대주자, 도루 등)
     */
    @SuppressWarnings("unchecked")
    private TimelineEvent parseAction(Map<String, Object> pe,
                                       int inning, String halfInning, String atBatIndex) {
        try {
            Map<String, Object> details = (Map<String, Object>) pe.get("details");
            if (details == null) return null;

            String eventType = (String) details.get("eventType");
            String event     = (String) details.get("event");
            String desc      = (String) details.get("description");

            if (eventType == null && event == null) return null;

            String key = eventType != null ? eventType : event;

            // 투수 교체
            if (key.contains("pitching_substitution") || "Pitching Substitution".equals(event)) {
                Map<String, Object> player = (Map<String, Object>) details.get("player");
                String incomingPitcher = player != null ? (String) player.get("fullName") : "알 수 없음";
                // description에서 outgoing pitcher 파싱 시도
                String outgoingPitcher = parseOutgoingPitcher(desc);

                return TimelineEvent.builder()
                        .type("PITCHING_CHANGE")
                        .inning(inning)
                        .halfInning(halfInning)
                        .atBatIndex(atBatIndex)
                        .outgoingPitcher(outgoingPitcher)
                        .incomingPitcher(incomingPitcher)
                        .description(desc)
                        .build();
            }

            // 대타
            if (key.contains("offensive_substitution") || "Offensive Substitution".equals(event)) {
                Map<String, Object> player = (Map<String, Object>) details.get("player");
                String substituteName = player != null ? (String) player.get("fullName") : "알 수 없음";

                return TimelineEvent.builder()
                        .type("PINCH_HITTER")
                        .inning(inning)
                        .halfInning(halfInning)
                        .atBatIndex(atBatIndex)
                        .substituteName(substituteName)
                        .description(desc)
                        .build();
            }

            // 도루
            if (key.contains("stolen_base") || (event != null && event.startsWith("Stolen Base"))) {
                Map<String, Object> player = (Map<String, Object>) details.get("player");
                String playerName = player != null ? (String) player.get("fullName") : "알 수 없음";
                String base = extractStolenBase(event);

                return TimelineEvent.builder()
                        .type("STOLEN_BASE")
                        .inning(inning)
                        .halfInning(halfInning)
                        .atBatIndex(atBatIndex)
                        .stolenBasePlayer(playerName)
                        .stolenBase(base)
                        .description(EVENT_MAP.getOrDefault(event, event))
                        .build();
            }

            // 도루 실패
            if (key.contains("caught_stealing") || (event != null && event.startsWith("Caught Stealing"))) {
                Map<String, Object> player = (Map<String, Object>) details.get("player");
                String playerName = player != null ? (String) player.get("fullName") : "알 수 없음";
                String base = extractStolenBase(event);

                return TimelineEvent.builder()
                        .type("CAUGHT_STEALING")
                        .inning(inning)
                        .halfInning(halfInning)
                        .atBatIndex(atBatIndex)
                        .stolenBasePlayer(playerName)
                        .stolenBase(base)
                        .description(EVENT_MAP.getOrDefault(event, event))
                        .build();
            }

        } catch (Exception e) {
            log.warn("액션 파싱 실패: {}", e.getMessage());
        }
        return null;
    }

    /**
     * 타석 결과 파싱 (PLAY / SCORE_CHANGE)
     */
    @SuppressWarnings("unchecked")
    private TimelineEvent parsePlayResult(Map<String, Object> play,
                                           int inning, String halfInning, String atBatIndex,
                                           String batterName, String pitcherName,
                                           List<Pitch> pitchSequence) {
        try {
            Map<String, Object> result  = (Map<String, Object>) play.get("result");
            Map<String, Object> about   = (Map<String, Object>) play.get("about");
            List<Map<String, Object>> runners = (List<Map<String, Object>>) play.get("runners");

            if (result == null) return null;

            String event       = (String) result.get("event");           // 영어 이벤트명
            String description = (String) result.get("description");     // MLB 원문

            // 이벤트명 한국어 변환
            String koEvent = EVENT_MAP.getOrDefault(event, event);

            int rbi        = result.get("rbi")       != null ? ((Number) result.get("rbi")).intValue()       : 0;
            int homeScore  = result.get("homeScore")  != null ? ((Number) result.get("homeScore")).intValue()  : 0;
            int awayScore  = result.get("awayScore")  != null ? ((Number) result.get("awayScore")).intValue()  : 0;

            // 득점 선수 이름 파싱
            List<String> scoringPlayers = new ArrayList<>();
            if (runners != null) {
                for (Map<String, Object> runner : runners) {
                    Map<String, Object> movement = (Map<String, Object>) runner.get("movement");
                    Map<String, Object> details  = (Map<String, Object>) runner.get("details");
                    if (movement == null || details == null) continue;

                    Object end = movement.get("end");
                    Boolean scored = (Boolean) movement.get("isOut");
                    // end == "score" 이면 득점
                    if ("score".equals(end)) {
                        Map<String, Object> runnerInfo = (Map<String, Object>) details.get("runner");
                        if (runnerInfo != null) {
                            String runnerName = (String) runnerInfo.get("fullName");
                            if (runnerName != null) scoringPlayers.add(runnerName);
                        }
                    }
                }
            }

            Boolean isComplete = (Boolean) about.getOrDefault("isComplete", false);
            if (!Boolean.TRUE.equals(isComplete)) return null; // 타석 완료된 것만

            String type = rbi > 0 || !scoringPlayers.isEmpty() ? "SCORE_CHANGE" : "PLAY";

            return TimelineEvent.builder()
                    .type(type)
                    .inning(inning)
                    .halfInning(halfInning)
                    .atBatIndex(atBatIndex)
                    .batterName(batterName)
                    .pitcherName(pitcherName)
                    .description(koEvent)        // 한국어 타석 결과
                    .rbi(rbi)
                    .homeScore(homeScore)
                    .awayScore(awayScore)
                    .scoringPlayers(scoringPlayers)
                    .pitches(pitchSequence)      // 타석 전체 투구 시퀀스 포함
                    .build();

        } catch (Exception e) {
            log.warn("타석 결과 파싱 실패: {}", e.getMessage());
            return null;
        }
    }

    // ── 유틸 ─────────────────────────────────────────────────────────

    @SuppressWarnings("unchecked")
    private String extractPlayerName(Map<String, Object> matchup, String role) {
        if (matchup == null) return "알 수 없음";
        Map<String, Object> player = (Map<String, Object>) matchup.get(role);
        return player != null ? (String) player.get("fullName") : "알 수 없음";
    }

    private String extractStolenBase(String event) {
        if (event == null) return "";
        if (event.contains("2B")) return "2루";
        if (event.contains("3B")) return "3루";
        if (event.contains("Home")) return "홈";
        return "";
    }

    /**
     * description에서 outgoing pitcher 파싱 시도
     * 예: "Pitching Change: LHP John Smith replaces RHP Mike Jones" → "Mike Jones"
     */
    private String parseOutgoingPitcher(String description) {
        if (description == null) return "알 수 없음";
        try {
            int idx = description.indexOf("replaces");
            if (idx > 0) {
                String after = description.substring(idx + 9).trim();
                // "RHP " 또는 "LHP " 제거
                after = after.replaceAll("^[LR]HP\\s+", "").trim();
                // 마침표 제거
                after = after.replaceAll("\\.$", "").trim();
                return after;
            }
        } catch (Exception ignored) {}
        return "알 수 없음";
    }

    private Map<String, Object> fetchPlayByPlay(Long gamePk) {
        try {
            return webClient.get()
                    .uri("/game/{gamePk}/playByPlay", gamePk)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
        } catch (Exception e) {
            log.error("playByPlay API 호출 실패 gamePk={}: {}", gamePk, e.getMessage());
            return null;
        }
    }

    /**
     * 경기 종료 감지 → isActive false (Live 배지 제거)
     */
    @SuppressWarnings("unchecked")
    private void checkGameEnd(Long gamePk, Long chatRoomId, Map<String, Object> response) {
        try {
            Map<String, Object> gameData = (Map<String, Object>) response.get("gameData");
            if (gameData == null) return;
            Map<String, Object> status = (Map<String, Object>) gameData.get("status");
            if (status == null) return;

            String abstractGameState = (String) status.get("abstractGameState");
            if ("Final".equalsIgnoreCase(abstractGameState)) {
                chatRoomRepository.findById(chatRoomId).ifPresent(room -> {
                    if (room.getIsActive()) {
                        room.deactivate();
                        chatRoomRepository.save(room);
                        chatService.sendSystemMessage(chatRoomId, "⚾ 경기가 종료되었습니다.");
                        messagingTemplate.convertAndSend("/topic/timeline/" + gamePk,
                                TimelineEvent.builder().type("GAME_END").build());
                        lastPlayIndex.remove(gamePk);
                        log.info("경기 종료 처리 gamePk={}", gamePk);
                    }
                });
            }
        } catch (Exception e) {
            log.error("경기 종료 감지 실패: {}", e.getMessage());
        }
    }
}