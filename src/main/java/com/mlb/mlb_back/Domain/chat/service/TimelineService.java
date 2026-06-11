package com.mlb.mlb_back.Domain.chat.service;

import com.mlb.mlb_back.Domain.chat.dto.TimelineEvent;
import com.mlb.mlb_back.Domain.chat.dto.TimelineEvent.Pitch;
import com.mlb.mlb_back.Domain.chat.entity.ChatRoom;
import com.mlb.mlb_back.Domain.chat.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class TimelineService {

    private final WebClient webClient;
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatService chatService;

    private final Map<Long, Integer> lastPlayIndex = new ConcurrentHashMap<>();

    // ── 구종 코드 → 한국어 ───────────────────────────────────────────
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
            Map.entry("EP", "이퓨스볼"),
            Map.entry("SC", "스크류볼"),
            Map.entry("PO", "피치아웃"),
            Map.entry("UN", "불명확")
    );

    // ── 투구 결과 코드 → 한국어 ──────────────────────────────────────
    private static final Map<String, String> CALL_CODE_MAP = Map.ofEntries(
            Map.entry("B",  "볼"),
            Map.entry("*B", "볼 (바운드볼)"),
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
            Map.entry("I",  "고의 볼"),
            Map.entry("H",  "타자 맞음 (HBP)"),
            Map.entry("VP", "자동 볼 (피치 타이머 위반)"),
            Map.entry("VC", "자동 스트라이크 (타자 타이머 위반)")
    );

    // ── 타석 결과 영어 → 한국어 ─────────────────────────────────────
    private static final Map<String, String> EVENT_MAP = Map.ofEntries(
            Map.entry("Single",                "1루타"),
            Map.entry("Double",                "2루타"),
            Map.entry("Triple",                "3루타"),
            Map.entry("Home Run",              "홈런"),
            Map.entry("Strikeout",             "삼진 아웃"),
            Map.entry("Strikeout - DP",        "삼진 병살"),
            Map.entry("Walk",                  "볼넷"),
            Map.entry("Intent Walk",           "고의 볼넷"),
            Map.entry("Hit By Pitch",          "몸에 맞는 공"),
            Map.entry("Field Error",           "실책 출루"),
            Map.entry("Fielders Choice",       "야수 선택"),
            Map.entry("Fielders Choice Out",   "야수 선택 아웃"),
            Map.entry("Grounded Into DP",      "병살타"),
            Map.entry("Double Play",           "병살"),
            Map.entry("Triple Play",           "삼중살"),
            Map.entry("Groundout",             "땅볼 아웃"),
            Map.entry("Flyout",                "플라이 아웃"),
            Map.entry("Lineout",               "라인드라이브 아웃"),
            Map.entry("Pop Out",               "내야 플라이 아웃"),
            Map.entry("Forceout",              "포스 아웃"),
            Map.entry("Bunt Groundout",        "번트 땅볼 아웃"),
            Map.entry("Bunt Pop Out",          "번트 플라이 아웃"),
            Map.entry("Sac Fly",               "희생 플라이"),
            Map.entry("Sac Fly DP",            "희생 플라이 병살"),
            Map.entry("Sac Bunt",              "희생 번트"),
            Map.entry("Sac Bunt DP",           "희생 번트 병살"),
            Map.entry("Catcher Interference",  "포수 방해"),
            Map.entry("Fan interference",      "관중 방해"),
            Map.entry("Stolen Base 2B",        "2루 도루"),
            Map.entry("Stolen Base 3B",        "3루 도루"),
            Map.entry("Stolen Base Home",      "홈 도루"),
            Map.entry("Caught Stealing 2B",    "2루 도루 실패"),
            Map.entry("Caught Stealing 3B",    "3루 도루 실패"),
            Map.entry("Caught Stealing Home",  "홈 도루 실패"),
            Map.entry("Pickoff 1B",            "1루 견제 아웃"),
            Map.entry("Pickoff 2B",            "2루 견제 아웃"),
            Map.entry("Pickoff 3B",            "3루 견제 아웃"),
            Map.entry("Wild Pitch",            "폭투"),
            Map.entry("Passed Ball",           "포일"),
            Map.entry("Balk",                  "보크"),
            Map.entry("Pitching Substitution", "투수 교체"),
            Map.entry("Offensive Substitution","공격 교체"),
            Map.entry("Defensive Sub",         "수비 교체"),
            Map.entry("Defensive Switch",      "수비 위치 변경")
    );

    // ── 무시할 action eventType ──────────────────────────────────────
    // JSON 확인: action 이벤트 중 "game_advisory"는 경기 상태 변경 알림으로 타임라인 표시 불필요
    private static final Set<String> IGNORED_EVENT_TYPES = Set.of(
            "game_advisory",
            "umpire_substitution"
    );

    // ─────────────────────────────────────────────────────────────────

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

    // ── private ───────────────────────────────────────────────────────

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

            for (TimelineEvent event : parseAllEvents(play)) {
                messagingTemplate.convertAndSend("/topic/timeline/" + gamePk, event);

                if ("SCORE_CHANGE".equals(event.getType())) {
                    String msg = String.format("⚾ 득점! [%d이닝 %s] %s (%d - %d)",
                            event.getInning(),
                            "top".equals(event.getHalfInning()) ? "초" : "말",
                            event.getDescription(),
                            event.getAwayScore(),
                            event.getHomeScore());
                    chatService.sendSystemMessage(chatRoomId, msg);
                }

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
     *
     * JSON 구조 확인 결과:
     * - pitchIndex: playEvents 배열에서 pitch 타입 이벤트의 index 목록
     * - actionIndex: playEvents 배열에서 action 타입 이벤트의 index 목록
     * → isPitch 필드 또는 type 필드로 직접 구분하는 게 더 명확
     *
     * - action 이벤트의 player는 pe 최상위에 있고 id/link만 있음 (fullName 없음)
     *   → 투수교체/대타는 description 파싱으로 이름 추출
     *
     * - 득점 판단: runners[].details.isScoringEvent == true
     *   (movement.end == "score"가 아님 - JSON 확인 결과 볼넷은 "1B"였음)
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

            List<Pitch> pitchSequence = new ArrayList<>();

            if (playEvents != null) {
                for (Map<String, Object> pe : playEvents) {
                    String peType = (String) pe.get("type");

                    if ("pitch".equals(peType)) {
                        // 정상 투구 - pitchData, 구종, 구속 모두 있음
                        Pitch pitch = parsePitch(pe);
                        if (pitch != null) {
                            pitchSequence.add(pitch);
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

                    } else if ("no_pitch".equals(peType)) {
                        // 타이머 위반 등 - details.call 있음, pitchData/구종 없음
                        // isPitch: false 이지만 볼카운트에 영향 주므로 PITCH 타입으로 전송
                        Pitch noPitch = parseNoPitch(pe);
                        if (noPitch != null) {
                            pitchSequence.add(noPitch);
                            events.add(TimelineEvent.builder()
                                    .type("PITCH")
                                    .inning(inning)
                                    .halfInning(halfInning)
                                    .atBatIndex(atBatIndex)
                                    .batterName(batterName)
                                    .pitcherName(pitcherName)
                                    .pitches(List.of(noPitch))
                                    .balls(noPitch.getBalls())
                                    .strikes(noPitch.getStrikes())
                                    .outs(noPitch.getOuts())
                                    .build());
                        }

                    } else if ("action".equals(peType)) {
                        Map<String, Object> details = (Map<String, Object>) pe.get("details");
                        if (details == null) continue;

                        String eventType = (String) details.get("eventType");

                        // game_advisory (Pre-Game, Warmup, In Progress 등) 무시
                        if (eventType != null && IGNORED_EVENT_TYPES.contains(eventType)) continue;

                        TimelineEvent actionEvent = parseAction(pe, inning, halfInning, atBatIndex);
                        if (actionEvent != null) events.add(actionEvent);
                    }
                }
            }

            // 타석 완료된 경우만 결과 이벤트 추가
            // JSON 확인: about.isComplete = true/false
            Boolean isComplete = (Boolean) about.get("isComplete");
            if (Boolean.TRUE.equals(isComplete)) {
                TimelineEvent playResult = parsePlayResult(
                        play, inning, halfInning, atBatIndex,
                        batterName, pitcherName, pitchSequence);
                if (playResult != null) events.add(playResult);
            }

        } catch (Exception e) {
            log.warn("이벤트 파싱 실패: {}", e.getMessage());
        }
        return events;
    }

    /**
     * 정상 투구 파싱
     * JSON 구조: details.call.code, details.type.code(구종), pitchData.startSpeed(구속)
     */
    @SuppressWarnings("unchecked")
    private Pitch parsePitch(Map<String, Object> pe) {
        try {
            Map<String, Object> details   = (Map<String, Object>) pe.get("details");
            Map<String, Object> count     = (Map<String, Object>) pe.get("count");
            Map<String, Object> pitchData = (Map<String, Object>) pe.get("pitchData");
            if (details == null) return null;

            // 투구 결과: details.call.code (B, F, S, C, X 등)
            Map<String, Object> call = (Map<String, Object>) details.get("call");
            String callCode = call != null ? (String) call.get("code") : "";
            String callDesc = CALL_CODE_MAP.getOrDefault(callCode, callCode);

            // 구종: details.type.code (FF, SI, SL 등)
            Map<String, Object> pitchType = (Map<String, Object>) details.get("type");
            String pitchTypeCode = pitchType != null ? (String) pitchType.get("code") : null;
            String pitchTypeName = pitchTypeCode != null
                    ? PITCH_TYPE_MAP.getOrDefault(pitchTypeCode, pitchTypeCode) : null;

            // 구속: pitchData.startSpeed (mph)
            Double startSpeed = null;
            if (pitchData != null && pitchData.get("startSpeed") != null) {
                startSpeed = ((Number) pitchData.get("startSpeed")).doubleValue();
            }

            return Pitch.builder()
                    .pitchNumber(pe.get("pitchNumber") != null
                            ? ((Number) pe.get("pitchNumber")).intValue() : null)
                    .callCode(callCode)
                    .callDescription(callDesc)
                    .pitchTypeCode(pitchTypeCode)
                    .pitchTypeName(pitchTypeName)
                    .startSpeed(startSpeed)
                    .balls(getInt(count, "balls"))
                    .strikes(getInt(count, "strikes"))
                    .outs(getInt(count, "outs"))
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
     * no_pitch 파싱 (타이머 위반 자동 볼/스트라이크)
     * JSON 확인: details.call 있음, details.type(구종) 없음, pitchData 없음
     * details.violation.player.fullName으로 위반 선수 이름 있음 (사용 안 함)
     */
    @SuppressWarnings("unchecked")
    private Pitch parseNoPitch(Map<String, Object> pe) {
        try {
            Map<String, Object> details = (Map<String, Object>) pe.get("details");
            Map<String, Object> count   = (Map<String, Object>) pe.get("count");
            if (details == null) return null;

            Map<String, Object> call = (Map<String, Object>) details.get("call");
            String callCode = call != null ? (String) call.get("code") : "VP";
            String callDesc = CALL_CODE_MAP.getOrDefault(callCode,
                    (String) details.getOrDefault("description", "규칙 위반"));

            return Pitch.builder()
                    .pitchNumber(null)          // no_pitch는 별도 pitchNumber 없음
                    .callCode(callCode)
                    .callDescription(callDesc)
                    .pitchTypeCode(null)         // 구종 없음
                    .pitchTypeName(null)
                    .startSpeed(null)            // 구속 없음
                    .balls(getInt(count, "balls"))
                    .strikes(getInt(count, "strikes"))
                    .outs(getInt(count, "outs"))
                    .isInPlay(false)
                    .isStrike((Boolean) details.getOrDefault("isStrike", false))
                    .isBall((Boolean) details.getOrDefault("isBall", false))
                    .build();
        } catch (Exception e) {
            log.warn("no_pitch 파싱 실패: {}", e.getMessage());
            return null;
        }
    }

    /**
     * action 이벤트 파싱 (투수교체, 대타, 대주자, 도루 등)
     *
     * JSON 확인:
     * - pe.player = { id, link } → fullName 없음
     * - 선수 이름은 details.description 파싱으로 추출
     * - eventType으로 타입 판별 (event 영문명보다 더 일관적)
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

            if (eventType == null) return null;

            // 투수 교체
            // description 예: "Pitching Change: LHP John Smith replaces RHP Mike Jones."
            if ("pitching_substitution".equals(eventType)) {
                String incoming = parseIncomingPlayer(desc);
                String outgoing = parseOutgoingPlayer(desc);
                return TimelineEvent.builder()
                        .type("PITCHING_CHANGE")
                        .inning(inning)
                        .halfInning(halfInning)
                        .atBatIndex(atBatIndex)
                        .incomingPitcher(incoming)
                        .outgoingPitcher(outgoing)
                        .description(desc)
                        .build();
            }

            // 공격 교체 (대타/대주자)
            // description 예: "Pinch hitter X replaces Y." / "Pinch runner X replaces Y."
            if ("offensive_substitution".equals(eventType)) {
                String subType = (desc != null && desc.toLowerCase().contains("pinch runner"))
                        ? "PINCH_RUNNER" : "PINCH_HITTER";
                String substitute = parseIncomingPlayer(desc);
                String replaced   = parseOutgoingPlayer(desc);
                return TimelineEvent.builder()
                        .type(subType)
                        .inning(inning)
                        .halfInning(halfInning)
                        .atBatIndex(atBatIndex)
                        .substituteName(substitute)
                        .replacedName(replaced)
                        .description(desc)
                        .build();
            }

            // 도루 성공
            if (eventType.startsWith("stolen_base")) {
                String base = extractStolenBase(event);
                return TimelineEvent.builder()
                        .type("STOLEN_BASE")
                        .inning(inning)
                        .halfInning(halfInning)
                        .atBatIndex(atBatIndex)
                        .stolenBase(base)
                        .description(EVENT_MAP.getOrDefault(event, event))
                        .build();
            }

            // 도루 실패
            if (eventType.startsWith("caught_stealing")) {
                String base = extractStolenBase(event);
                return TimelineEvent.builder()
                        .type("CAUGHT_STEALING")
                        .inning(inning)
                        .halfInning(halfInning)
                        .atBatIndex(atBatIndex)
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
     * 타석 결과 파싱
     *
     * JSON 확인:
     * - result.event: "Walk", "Groundout", "Home Run" 등
     * - result.rbi: 숫자
     * - result.awayScore / homeScore: 이 타석 이후 점수
     * - runners[].details.isScoringEvent: true → 득점 선수
     *   (movement.end == "score"가 아님! 볼넷은 "1B"였음)
     */
    @SuppressWarnings("unchecked")
    private TimelineEvent parsePlayResult(Map<String, Object> play,
                                           int inning, String halfInning, String atBatIndex,
                                           String batterName, String pitcherName,
                                           List<Pitch> pitchSequence) {
        try {
            Map<String, Object> result  = (Map<String, Object>) play.get("result");
            List<Map<String, Object>> runners =
                    (List<Map<String, Object>>) play.get("runners");

            String event   = (String) result.get("event");
            String koEvent = EVENT_MAP.getOrDefault(event, event);

            int rbi       = getInt(result, "rbi");
            int homeScore = getInt(result, "homeScore");
            int awayScore = getInt(result, "awayScore");

            // 득점 선수: runners[].details.isScoringEvent == true
            List<String> scoringPlayers = new ArrayList<>();
            if (runners != null) {
                for (Map<String, Object> runner : runners) {
                    Map<String, Object> rDetails = (Map<String, Object>) runner.get("details");
                    if (rDetails == null) continue;
                    Boolean isScoringEvent = (Boolean) rDetails.get("isScoringEvent");
                    if (Boolean.TRUE.equals(isScoringEvent)) {
                        Map<String, Object> runnerInfo = (Map<String, Object>) rDetails.get("runner");
                        if (runnerInfo != null) {
                            String name = (String) runnerInfo.get("fullName");
                            if (name != null) scoringPlayers.add(name);
                        }
                    }
                }
            }

            String type = (rbi > 0 || !scoringPlayers.isEmpty()) ? "SCORE_CHANGE" : "PLAY";

            return TimelineEvent.builder()
                    .type(type)
                    .inning(inning)
                    .halfInning(halfInning)
                    .atBatIndex(atBatIndex)
                    .batterName(batterName)
                    .pitcherName(pitcherName)
                    .description(koEvent)
                    .rbi(rbi)
                    .homeScore(homeScore)
                    .awayScore(awayScore)
                    .scoringPlayers(scoringPlayers)
                    .pitches(pitchSequence)
                    .build();

        } catch (Exception e) {
            log.warn("타석 결과 파싱 실패: {}", e.getMessage());
            return null;
        }
    }

    // ── 유틸 ──────────────────────────────────────────────────────────

    @SuppressWarnings("unchecked")
    private String extractPlayerName(Map<String, Object> matchup, String role) {
        if (matchup == null) return "알 수 없음";
        Map<String, Object> player = (Map<String, Object>) matchup.get(role);
        return player != null
                ? (String) player.getOrDefault("fullName", "알 수 없음") : "알 수 없음";
    }

    private int getInt(Map<String, Object> map, String key) {
        if (map == null || map.get(key) == null) return 0;
        return ((Number) map.get(key)).intValue();
    }

    private String extractStolenBase(String event) {
        if (event == null) return "";
        if (event.contains("2B")) return "2루";
        if (event.contains("3B")) return "3루";
        if (event.contains("Home")) return "홈";
        return "";
    }

    /**
     * description에서 들어오는 선수 파싱
     * "Pitching Change: LHP John Smith replaces RHP Mike Jones." → "John Smith"
     * "Pinch hitter John Smith replaces Mike Jones." → "John Smith"
     */
    private String parseIncomingPlayer(String description) {
        if (description == null) return "알 수 없음";
        try {
            // ":" 이후 ~ "replaces" 이전
            int colonIdx = description.indexOf(":");
            int replacesIdx = description.indexOf("replaces");
            if (colonIdx >= 0 && replacesIdx > colonIdx) {
                String between = description.substring(colonIdx + 1, replacesIdx).trim();
                // "LHP " / "RHP " / "Pinch hitter " 등 접두어 제거
                between = between.replaceAll("^(L|R)HP\\s+", "")
                                 .replaceAll("(?i)^pinch (hitter|runner)\\s+", "")
                                 .trim();
                return between;
            }
            // ":" 없는 경우: "Pinch hitter X replaces Y"
            if (replacesIdx > 0) {
                String before = description.substring(0, replacesIdx).trim();
                before = before.replaceAll("(?i)^pinch (hitter|runner)\\s+", "").trim();
                return before;
            }
        } catch (Exception ignored) {}
        return "알 수 없음";
    }

    /**
     * description에서 나가는 선수 파싱
     * "... replaces RHP Mike Jones." → "Mike Jones"
     * "... replaces Mike Jones." → "Mike Jones"
     */
    private String parseOutgoingPlayer(String description) {
        if (description == null) return "알 수 없음";
        try {
            int idx = description.indexOf("replaces");
            if (idx >= 0) {
                String after = description.substring(idx + 8).trim();
                after = after.replaceAll("^(L|R)HP\\s+", "")
                             .replaceAll("\\.$", "")
                             .trim();
                return after;
            }
        } catch (Exception ignored) {}
        return "알 수 없음";
    }

    private Map<String, Object> fetchPlayByPlay(Long gamePk) {
        try {
            return webClient.get()
                    .uri("/api/v1/game/{gamePk}/playByPlay", gamePk)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
        } catch (Exception e) {
            log.error("playByPlay API 호출 실패 gamePk={}: {}", gamePk, e.getMessage());
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private void checkGameEnd(Long gamePk, Long chatRoomId, Map<String, Object> response) {
        try {
            Map<String, Object> gameData = (Map<String, Object>) response.get("gameData");
            if (gameData == null) return;
            Map<String, Object> status = (Map<String, Object>) gameData.get("status");
            if (status == null) return;

            if ("Final".equalsIgnoreCase((String) status.get("abstractGameState"))) {
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