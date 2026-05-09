package com.football.Football_back.Domain.standing.service;

import com.football.Football_back.Domain.season.repository.SeasonRepository;
import com.football.Football_back.Domain.standing.dto.StandingResponse;
import com.football.Football_back.Domain.standing.repository.StandingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StandingService {

    private final StandingRepository standingRepository;
    private final SeasonRepository seasonRepository;

    // 리그 + 시즌 순위표
    public List<StandingResponse> getStandings(Long leagueId, String year) {
        // year가 없으면 현재 시즌 조회
        if (year == null || year.isEmpty()) {
            return seasonRepository.findByLeagueIdAndIsCurrentTrue(leagueId)
                    .map(season -> standingRepository
                            .findByLeagueIdAndSeasonIdOrderByRankAsc(leagueId, season.getId())
                            .stream()
                            .map(StandingResponse::from)
                            .collect(Collectors.toList()))
                    .orElse(List.of());
        }

        // year가 있으면 해당 시즌 조회
        return seasonRepository.findByLeagueIdAndYear(leagueId, year)
                .map(season -> standingRepository
                        .findByLeagueIdAndSeasonIdOrderByRankAsc(leagueId, season.getId())
                        .stream()
                        .map(StandingResponse::from)
                        .collect(Collectors.toList()))
                .orElse(List.of());
    }

    // 시즌 목록 조회 (드롭다운용)
    public List<String> getSeasonYears(Long leagueId) {
        return seasonRepository.findByLeagueIdOrderByYearDesc(leagueId)
                .stream()
                .map(season -> season.getDisplayName())
                .collect(Collectors.toList());
    }
}