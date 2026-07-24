package com.mlb.mlb_back.Domain.game.service;

import com.mlb.mlb_back.Domain.game.dto.BoxScoreResponse;
import com.mlb.mlb_back.Domain.game.dto.GameResponse;
import com.mlb.mlb_back.Domain.game.dto.LineScoreResponse;
import com.mlb.mlb_back.Domain.game.entity.BoxScore;
import com.mlb.mlb_back.Domain.game.entity.Game;
import com.mlb.mlb_back.Domain.game.entity.LineScore;
import com.mlb.mlb_back.Domain.game.repository.BoxScoreRepository;
import com.mlb.mlb_back.Domain.game.repository.GameRepository;
import com.mlb.mlb_back.Domain.game.repository.LineScoreRepository;
import com.mlb.mlb_back.Global.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GameService {

    // 한국 사용자 기준 서비스이므로 "오늘", "날짜별 조회"는 KST(Asia/Seoul)를 기준으로 하루를 정의합니다.
    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    private final GameRepository gameRepository;
    private final BoxScoreRepository boxScoreRepository;
    private final LineScoreRepository lineScoreRepository;

    private Game findGameOrThrow(Long gameId) {
        return gameRepository.findById(gameId)
                .orElseThrow(() -> ApiException.notFound("경기를 찾을 수 없습니다: " + gameId));
    }

    // 경기 상세
    public GameResponse getGame(Long gameId) {
        return GameResponse.fromEntity(findGameOrThrow(gameId));
    }

    // 경기 박스스코어
    public List<BoxScoreResponse> getBoxScore(Long gameId) {
        findGameOrThrow(gameId);
        return boxScoreRepository.findByGameId(gameId).stream()
                .sorted(Comparator
                        .comparing((BoxScore bs) -> "BATTER".equals(bs.getPlayerType()) ? 0 : 1)
                        .thenComparing(bs -> {
                            if ("BATTER".equals(bs.getPlayerType())) {
                                return bs.getBattingOrder() != null ? bs.getBattingOrder() : 99;
                            }
                            return bs.getAppearanceOrder() != null ? bs.getAppearanceOrder() : 99;
                        })
                        .thenComparing(bs -> bs.getPlayer().getFullName()))
                .map(BoxScoreResponse::fromEntity)
                .collect(Collectors.toList());
    }

    // 경기 라인스코어
    public List<LineScoreResponse> getLineScore(Long gameId) {
        findGameOrThrow(gameId);
        return lineScoreRepository.findByGameId(gameId).stream()
                .sorted(Comparator
                        .comparing(LineScore::getInning)
                        .thenComparing(ls -> Boolean.TRUE.equals(ls.getIsHome()) ? 1 : 0))
                .map(LineScoreResponse::fromEntity)
                .collect(Collectors.toList());
    }

    // 시즌별 경기 목록
    public List<GameResponse> getGamesBySeason(Integer season) {
        return gameRepository.findBySeason(season).stream()
                .map(GameResponse::fromEntity)
                .collect(Collectors.toList());
    }

    // 날짜별 경기 목록 (date는 "한국시간 기준 그 날짜"로 해석)
    public List<GameResponse> getGamesByDate(LocalDate date) {
        Instant start = date.atStartOfDay(KST).toInstant();
        // Between은 양 끝을 포함하므로, 다음날 00:00:00(KST) "직전"까지로 끝을 살짝 당겨줍니다.
        Instant end = date.plusDays(1).atStartOfDay(KST).toInstant().minusNanos(1);
        return gameRepository.findByGameDateBetween(start, end).stream()
                .map(GameResponse::fromEntity)
                .collect(Collectors.toList());
    }

    // 팀별 경기 목록
    public List<GameResponse> getGamesByTeam(Long teamId) {
        return gameRepository.findByHomeTeamIdOrAwayTeamId(teamId, teamId).stream()
                .map(GameResponse::fromEntity)
                .collect(Collectors.toList());
    }

    // 상태별 경기 목록 (Final, Live, Scheduled 등)
    public List<GameResponse> getGamesByStatus(String status) {
        return gameRepository.findByStatus(status).stream()
                .map(GameResponse::fromEntity)
                .collect(Collectors.toList());
    }

    // 시즌 + 상태별 경기 목록
    public List<GameResponse> getGamesBySeasonAndStatus(Integer season, String status) {
        return gameRepository.findBySeasonAndStatus(season, status).stream()
                .map(GameResponse::fromEntity)
                .collect(Collectors.toList());
    }

    // 오늘 경기 목록 (한국시간 기준 오늘)
    public List<GameResponse> getTodayGames() {
        return getGamesByDate(LocalDate.now(KST));
    }
}