package com.football.Football_back.Domain.match.service;

import com.football.Football_back.Domain.match.dto.MatchResponse;
import com.football.Football_back.Domain.match.repository.MatchRepository;
import com.football.Football_back.Domain.season.repository.SeasonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MatchService {

    private final MatchRepository matchRepository;
    private final SeasonRepository seasonRepository;

    // 리그 + 시즌 전체 경기 목록
    public List<MatchResponse> getMatchesByLeague(Long leagueId, String year) {
        if (year == null || year.isEmpty()) {
            return seasonRepository.findByLeagueIdAndIsCurrentTrue(leagueId)
                    .map(season -> matchRepository
                            .findBySeasonIdOrderByMatchDateDesc(season.getId())
                            .stream()
                            .map(MatchResponse::from)
                            .collect(Collectors.toList()))
                    .orElse(List.of());
        }

        return seasonRepository.findByLeagueIdAndYear(leagueId, year)
                .map(season -> matchRepository
                        .findBySeasonIdOrderByMatchDateDesc(season.getId())
                        .stream()
                        .map(MatchResponse::from)
                        .collect(Collectors.toList()))
                .orElse(List.of());
    }

    // 특정 라운드 경기 목록
    public List<MatchResponse> getMatchesByMatchday(Long leagueId, String year, Integer matchday) {
        return seasonRepository.findByLeagueIdAndYear(leagueId, year)
                .map(season -> matchRepository
                        .findBySeasonIdAndMatchday(season.getId(), matchday)
                        .stream()
                        .map(MatchResponse::from)
                        .collect(Collectors.toList()))
                .orElse(List.of());
    }

    // 특정 팀 경기 목록
    public List<MatchResponse> getMatchesByTeam(Long teamId, String year, Long leagueId) {
        return seasonRepository.findByLeagueIdAndYear(leagueId, year)
                .map(season -> matchRepository
                        .findByTeamAndSeason(teamId, season.getId())
                        .stream()
                        .map(MatchResponse::from)
                        .collect(Collectors.toList()))
                .orElse(List.of());
    }

    // 챔스 stage별 경기 조회
    public List<MatchResponse> getMatchesByStage(Long leagueId, String year, String stage) {
        return seasonRepository.findByLeagueIdAndYear(leagueId, year)
                .map(season -> matchRepository
                        .findBySeasonIdAndStage(season.getId(), stage)
                        .stream()
                        .map(MatchResponse::from)
                        .collect(Collectors.toList()))
                .orElse(List.of());
    }

    // 챔스 토너먼트 경기 조회
    public List<MatchResponse> getTournamentMatches(Long leagueId, String year) {
        List<String> tournamentStages = List.of(
                "PLAYOFFS", "LAST_16", "QUARTER_FINALS", "SEMI_FINALS", "FINAL"
        );
        return seasonRepository.findByLeagueIdAndYear(leagueId, year)
                .map(season -> matchRepository
                        .findBySeasonIdAndStageIn(season.getId(), tournamentStages)
                        .stream()
                        .map(MatchResponse::from)
                        .collect(Collectors.toList()))
                .orElse(List.of());
    }

    // 경기 상세 조회
    public MatchResponse getMatch(Long matchId) {
        return matchRepository.findById(matchId)
                .map(MatchResponse::from)
                .orElseThrow(() -> new RuntimeException("경기를 찾을 수 없습니다: " + matchId));
    }

    // 진행중인 경기
    public List<MatchResponse> getLiveMatches() {
        return matchRepository.findByStatus("LIVE")
                .stream()
                .map(MatchResponse::from)
                .collect(Collectors.toList());
    }
}