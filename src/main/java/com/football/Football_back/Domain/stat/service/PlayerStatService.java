package com.football.Football_back.Domain.stat.service;

import com.football.Football_back.Domain.player.entity.Player;
import com.football.Football_back.Domain.player.repository.PlayerRepository;
import com.football.Football_back.Domain.stat.dto.PlayerSeasonStatResponse;
import com.football.Football_back.Domain.stat.dto.PlayerStatResponse;
import com.football.Football_back.Domain.stat.entity.PlayerStat;
import com.football.Football_back.Domain.stat.repository.PlayerStatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlayerStatService {

    private final PlayerStatRepository playerStatRepository;
    private final PlayerRepository playerRepository;

    // 선수 경기별 스탯 (해당 시즌)
    public List<PlayerStatResponse> getPlayerStatsBySeason(Long playerId, Long seasonId) {
        return playerStatRepository
                .findByPlayerIdAndSeasonIdOrderByMatchMatchDateDesc(playerId, seasonId)
                .stream()
                .map(PlayerStatResponse::from)
                .collect(Collectors.toList());
    }

    // 경기별 전체 선수 스탯
    public List<PlayerStatResponse> getStatsByMatch(Long matchId) {
        return playerStatRepository.findByMatchId(matchId)
                .stream()
                .map(PlayerStatResponse::from)
                .collect(Collectors.toList());
    }

    // 선수 시즌 누적 스탯
    public PlayerSeasonStatResponse getPlayerSeasonStat(Long playerId, Long seasonId) {
        List<PlayerStat> stats = playerStatRepository
                .findByPlayerIdAndSeasonIdOrderByMatchMatchDateDesc(playerId, seasonId);

        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new RuntimeException("선수를 찾을 수 없습니다: " + playerId));

        if (stats.isEmpty()) {
            return null;
        }

        return PlayerSeasonStatResponse.builder()
                .playerId(playerId)
                .playerName(player.getName())
                .position(player.getPosition())
                .appearances(stats.size())
                .startingAppearances((int) stats.stream()
                        .filter(s -> s.getMinutesPlayed() != null && s.getMinutesPlayed() > 0)
                        .count())
                .totalMinutesPlayed(stats.stream()
                        .mapToInt(s -> s.getMinutesPlayed() != null ? s.getMinutesPlayed() : 0)
                        .sum())
                .totalGoals(stats.stream().mapToInt(s -> s.getGoals() != null ? s.getGoals() : 0).sum())
                .totalAssists(stats.stream().mapToInt(s -> s.getAssists() != null ? s.getAssists() : 0).sum())
                .totalShots(stats.stream().mapToInt(s -> s.getShots() != null ? s.getShots() : 0).sum())
                .totalShotsOnTarget(stats.stream().mapToInt(s -> s.getShotsOnTarget() != null ? s.getShotsOnTarget() : 0).sum())
                .totalYellowCards(stats.stream().mapToInt(s -> s.getYellowCards() != null ? s.getYellowCards() : 0).sum())
                .totalRedCards(stats.stream().mapToInt(s -> s.getRedCards() != null ? s.getRedCards() : 0).sum())
                .totalTackles(stats.stream().mapToInt(s -> s.getTackles() != null ? s.getTackles() : 0).sum())
                .totalInterceptions(stats.stream().mapToInt(s -> s.getInterceptions() != null ? s.getInterceptions() : 0).sum())
                .totalDribblesWon(stats.stream().mapToInt(s -> s.getDribblesWon() != null ? s.getDribblesWon() : 0).sum())
                .totalKeyPasses(stats.stream().mapToInt(s -> s.getKeyPasses() != null ? s.getKeyPasses() : 0).sum())
                .averagePassAccuracy(stats.stream()
                        .filter(s -> s.getPassAccuracy() != null)
                        .mapToDouble(PlayerStat::getPassAccuracy)
                        .average().orElse(0.0))
                .totalXg(stats.stream().mapToDouble(s -> s.getXg() != null ? s.getXg() : 0.0).sum())
                .totalXa(stats.stream().mapToDouble(s -> s.getXa() != null ? s.getXa() : 0.0).sum())
                // 골키퍼
                .totalSaves(stats.stream().mapToInt(s -> s.getSaves() != null ? s.getSaves() : 0).sum())
                .averageSavePercentage(stats.stream()
                        .filter(s -> s.getSavePercentage() != null)
                        .mapToDouble(PlayerStat::getSavePercentage)
                        .average().orElse(0.0))
                .totalGoalsConceded(stats.stream().mapToInt(s -> s.getGoalsConceded() != null ? s.getGoalsConceded() : 0).sum())
                .totalXga(stats.stream().mapToDouble(s -> s.getXga() != null ? s.getXga() : 0.0).sum())
                .totalPsxg(stats.stream().mapToDouble(s -> s.getPsxg() != null ? s.getPsxg() : 0.0).sum())
                .build();
    }
}
