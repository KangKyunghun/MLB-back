package com.mlb.mlb_back.Domain.game.service;

import com.mlb.mlb_back.Domain.chat.dto.TimelineEvent;
import com.mlb.mlb_back.Domain.chat.service.TimelineService;
import com.mlb.mlb_back.Domain.game.dto.DefenseSnapshotResponse;
import com.mlb.mlb_back.Domain.game.dto.DefenseSnapshotResponse.DefensePlayer;
import com.mlb.mlb_back.Domain.game.dto.DefenseSnapshotResponse.LineupPlayer;
import com.mlb.mlb_back.Domain.game.entity.BoxScore;
import com.mlb.mlb_back.Domain.game.entity.Game;
import com.mlb.mlb_back.Domain.game.repository.BoxScoreRepository;
import com.mlb.mlb_back.Domain.game.repository.GameRepository;
import com.mlb.mlb_back.Domain.team.entity.Team;
import com.mlb.mlb_back.Global.config.DataInitializer;
import com.mlb.mlb_back.Global.exception.ApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 특정 이닝(초/말) 시점의 "수비 상황" 스냅샷을 재구성한다.
 *
 * 데이터 한계로 인한 재구성 방식:
 * - 투수: TimelineEvent의 PITCHING_CHANGE 이벤트를 선택된 이닝/이닝-말까지 순서대로 반영해
 *   "그 시점 등판 중이던 투수"를 정확히 추적한다.
 * - 그 외 8개 수비 포지션: 경기 중 수비 위치가 바뀌는 경우(수비 교체)는 흔치 않고, 현재 백엔드에는
 *   교체 시점별 포지션 스냅샷이 없기 때문에, 해당 경기 최종 박스스코어의 포지션(BoxScore.gamePosition)을
 *   그대로 사용한다. 즉 투수 교체 이력은 완벽히 반영되지만, 그 외 수비 위치는 "경기 최종 수비 라인업" 기준
 *   최선의 추정치다.
 * - 타석: 선택된 이닝/이닝-말에서 실제로 발생한 타석(완료된 PLAY/SCORE_CHANGE 이벤트, 없으면 진행 중인
 *   PITCH 이벤트)의 마지막 타자를 "현재 타자"로 보고, 그 팀 타순(battingOrder)을 기준으로 다음 두 타자를
 *   대기타석/그다음 타자로 계산한다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DefenseSnapshotService {

    private static final List<String> FIELD_POSITIONS =
            List.of("P", "C", "1B", "2B", "3B", "SS", "LF", "CF", "RF");

    private final GameRepository gameRepository;
    private final BoxScoreRepository boxScoreRepository;
    private final TimelineService timelineService;
    private final DataInitializer dataInitializer;

    public DefenseSnapshotResponse getSnapshot(Long gameId, int inning, String half) {
        String halfInning = normalizeHalf(half);

        // 이 경기의 box_score에 game_position이 비어있는 게 있으면(2024~2026 과거 데이터 등)
        // 이 경기 하나만 지금 조회해서 채운다. 시즌 전체 재수집은 필요 없다.
        dataInitializer.backfillGamePositionsIfNeeded(gameId);

        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> ApiException.notFound("경기를 찾을 수 없습니다: " + gameId));

        boolean topHalf = "top".equals(halfInning);
        Team battingTeam = topHalf ? game.getAwayTeam() : game.getHomeTeam();
        Team fieldingTeam = topHalf ? game.getHomeTeam() : game.getAwayTeam();

        List<BoxScore> allBoxScores = boxScoreRepository.findByGameId(gameId);
        List<BoxScore> fieldingBoxScores = allBoxScores.stream()
                .filter(bs -> bs.getTeam() != null && bs.getTeam().getId().equals(fieldingTeam.getId()))
                .toList();
        List<BoxScore> battingBoxScores = allBoxScores.stream()
                .filter(bs -> bs.getTeam() != null && bs.getTeam().getId().equals(battingTeam.getId()))
                .toList();

        List<TimelineEvent> timeline = timelineService.getFullTimeline(gameId);

        long targetKey = orderKey(inning, halfInning);

        // ── 1) 그 시점까지의 투수 교체 이력 반영 ─────────────────────────
        String currentPitcherName = startingPitcherName(fieldingBoxScores);
        for (TimelineEvent ev : timeline) {
            if (!"PITCHING_CHANGE".equals(ev.getType())) continue;
            if (orderKey(ev.getInning(), ev.getHalfInning()) > targetKey) break;
            if (ev.getIncomingPitcher() != null) currentPitcherName = ev.getIncomingPitcher();
        }

        // ── 2) 수비 라인업 구성 (투수는 위에서 구한 값으로 덮어씀, 나머지는 최종 포지션 기준) ──
        List<DefensePlayer> positions = buildDefense(fieldingBoxScores, currentPitcherName);

        // ── 3) 선택된 이닝/이닝-말의 이벤트만 추려서 타순 상황 계산 ─────────
        List<TimelineEvent> halfEvents = timeline.stream()
                .filter(ev -> ev.getInning() != null && ev.getInning() == inning
                        && halfInning.equals(ev.getHalfInning()))
                .toList();

        String lastBatter = lastBatterName(halfEvents);

        Map<Integer, BoxScore> orderSlots = new HashMap<>();
        Map<String, Integer> nameToSlot = new HashMap<>();
        for (BoxScore bs : battingBoxScores) {
            if (!"BATTER".equals(bs.getPlayerType()) || bs.getBattingOrder() == null) continue;
            orderSlots.put(bs.getBattingOrder(), bs);
            nameToSlot.put(bs.getPlayer().getFullName(), bs.getBattingOrder());
        }

        LineupPlayer currentBatter = null;
        LineupPlayer onDeck = null;
        LineupPlayer inHole = null;

        Integer currentSlot = lastBatter != null ? nameToSlot.get(lastBatter) : null;
        if (currentSlot != null) {
            currentBatter = toLineupPlayer(orderSlots.get(currentSlot));
            onDeck = toLineupPlayer(orderSlots.get(nextSlot(currentSlot)));
            inHole = toLineupPlayer(orderSlots.get(nextSlot(nextSlot(currentSlot))));
        }

        // ── 4) 스코어 (그 시점까지 마지막으로 갱신된 점수) ───────────────
        int awayScore = 0;
        int homeScore = 0;
        for (TimelineEvent ev : timeline) {
            if (orderKey(ev.getInning(), ev.getHalfInning()) > targetKey) break;
            if (!"SCORE_CHANGE".equals(ev.getType())) continue;
            if (ev.getAwayScore() != null) awayScore = ev.getAwayScore();
            if (ev.getHomeScore() != null) homeScore = ev.getHomeScore();
        }

        // ── 5) 볼-스트라이크-아웃 (해당 이닝-말 마지막 투구 기준) ────────
        int balls = 0, strikes = 0, outs = 0;
        for (TimelineEvent ev : halfEvents) {
            if (!"PITCH".equals(ev.getType())) continue;
            if (ev.getBalls() != null) balls = ev.getBalls();
            if (ev.getStrikes() != null) strikes = ev.getStrikes();
            if (ev.getOuts() != null) outs = ev.getOuts();
        }

        return DefenseSnapshotResponse.builder()
                .gameId(gameId)
                .inning(inning)
                .halfInning(halfInning)
                .battingTeamId(battingTeam.getId())
                .battingTeamName(battingTeam.getName())
                .battingTeamAbbreviation(battingTeam.getAbbreviation())
                .fieldingTeamId(fieldingTeam.getId())
                .fieldingTeamName(fieldingTeam.getName())
                .fieldingTeamAbbreviation(fieldingTeam.getAbbreviation())
                .awayScore(awayScore)
                .homeScore(homeScore)
                .balls(balls)
                .strikes(strikes)
                .outs(outs)
                .positions(positions)
                .currentBatter(currentBatter)
                .onDeck(onDeck)
                .inHole(inHole)
                .build();
    }

    // ── private ──────────────────────────────────────────────────────

    private String normalizeHalf(String half) {
        if (half == null) return "top";
        String h = half.trim().toLowerCase();
        if (h.startsWith("bot") || h.equals("말") || h.equals("bottom")) return "bottom";
        return "top";
    }

    /** 이닝/이닝-말을 하나의 정렬 가능한 정수 키로 변환 (초=0, 말=1) */
    private long orderKey(Integer inning, String halfInning) {
        int inn = inning == null ? 0 : inning;
        int halfOrd = "bottom".equals(halfInning) ? 1 : 0;
        return inn * 2L + halfOrd;
    }

    private String startingPitcherName(List<BoxScore> fieldingBoxScores) {
        return fieldingBoxScores.stream()
                .filter(bs -> "PITCHER".equals(bs.getPlayerType()))
                .min(Comparator.comparing(bs -> bs.getAppearanceOrder() == null ? 99 : bs.getAppearanceOrder()))
                .map(bs -> bs.getPlayer().getFullName())
                .orElse(null);
    }

    private List<DefensePlayer> buildDefense(List<BoxScore> fieldingBoxScores, String currentPitcherName) {
        // 포지션별로 그 경기 최종 포지션이 일치하는 선수를 찾는다 (선발 라인업 우선).
        Map<String, BoxScore> byPosition = new LinkedHashMap<>();
        for (BoxScore bs : fieldingBoxScores) {
            if (bs.getGamePosition() == null) continue;
            String pos = bs.getGamePosition();
            if (!FIELD_POSITIONS.contains(pos)) continue;

            BoxScore existing = byPosition.get(pos);
            if (existing == null) {
                byPosition.put(pos, bs);
            } else {
                // 타순(선발 라인업)에 배정된 선수를 대타 등 타순 없는 선수보다 우선한다.
                boolean existingHasOrder = existing.getBattingOrder() != null;
                boolean candidateHasOrder = bs.getBattingOrder() != null;
                if (!existingHasOrder && candidateHasOrder) byPosition.put(pos, bs);
            }
        }

        List<DefensePlayer> result = new ArrayList<>();
        for (String pos : FIELD_POSITIONS) {
            if ("P".equals(pos) && currentPitcherName != null) {
                BoxScore pitcherRow = fieldingBoxScores.stream()
                        .filter(bs -> "PITCHER".equals(bs.getPlayerType())
                                && currentPitcherName.equals(bs.getPlayer().getFullName()))
                        .findFirst()
                        .orElse(null);
                result.add(DefensePlayer.builder()
                        .position("P")
                        .playerId(pitcherRow != null ? pitcherRow.getPlayer().getId() : null)
                        .playerName(currentPitcherName)
                        .photoUrl(photoUrl(pitcherRow != null ? pitcherRow.getPlayer().getId() : null))
                        .build());
                continue;
            }

            BoxScore bs = byPosition.get(pos);
            result.add(DefensePlayer.builder()
                    .position(pos)
                    .playerId(bs != null ? bs.getPlayer().getId() : null)
                    .playerName(bs != null ? bs.getPlayer().getFullName() : null)
                    .photoUrl(bs != null ? photoUrl(bs.getPlayer().getId()) : null)
                    .build());
        }
        return result;
    }

    /** 완료된 타석(PLAY/SCORE_CHANGE) 중 마지막 타자, 없으면 진행 중인 PITCH의 마지막 타자 */
    private String lastBatterName(List<TimelineEvent> halfEvents) {
        String last = null;
        for (TimelineEvent ev : halfEvents) {
            if ("PLAY".equals(ev.getType()) || "SCORE_CHANGE".equals(ev.getType())) {
                if (ev.getBatterName() != null) last = ev.getBatterName();
            }
        }
        if (last != null) return last;

        for (TimelineEvent ev : halfEvents) {
            if ("PITCH".equals(ev.getType()) && ev.getBatterName() != null) {
                last = ev.getBatterName();
            }
        }
        return last;
    }

    private int nextSlot(Integer slot) {
        if (slot == null) return 1;
        return slot >= 9 ? 1 : slot + 1;
    }

    private LineupPlayer toLineupPlayer(BoxScore bs) {
        if (bs == null) return null;
        return LineupPlayer.builder()
                .playerId(bs.getPlayer().getId())
                .playerName(bs.getPlayer().getFullName())
                .photoUrl(photoUrl(bs.getPlayer().getId()))
                .battingOrder(bs.getBattingOrder())
                .build();
    }

    private String photoUrl(Long playerId) {
        if (playerId == null) return null;
        return "https://img.mlbstatic.com/mlb-photos/image/upload/d_people:generic:headshot:67:current.png/w_213,q_auto:best/v1/people/"
                + playerId + "/headshot/67/current";
    }
}