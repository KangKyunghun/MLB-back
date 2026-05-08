package com.football.Football_back.Global.config;

import com.football.Football_back.Domain.league.entity.League;
import com.football.Football_back.Domain.league.repository.LeagueRepository;
import com.football.Football_back.Domain.match.entity.Lineup;
import com.football.Football_back.Domain.match.entity.Match;
import com.football.Football_back.Domain.match.entity.Timeline;
import com.football.Football_back.Domain.match.repository.LineupRepository;
import com.football.Football_back.Domain.match.repository.MatchRepository;
import com.football.Football_back.Domain.match.repository.TimelineRepository;
import com.football.Football_back.Domain.player.entity.Player;
import com.football.Football_back.Domain.player.repository.PlayerRepository;
import com.football.Football_back.Domain.season.entity.Season;
import com.football.Football_back.Domain.season.repository.SeasonRepository;
import com.football.Football_back.Domain.standing.entity.Standing;
import com.football.Football_back.Domain.standing.repository.StandingRepository;
import com.football.Football_back.Domain.team.entity.Team;
import com.football.Football_back.Domain.team.repository.TeamRepository;
import com.football.Football_back.Global.common.dto.FootballApiDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {
    private final FootballApiProperties properties;
    private final WebClient footballWebClient;
    private final LeagueRepository leagueRepository;
    private final SeasonRepository seasonRepository;
    private final TeamRepository teamRepository;
    private final PlayerRepository playerRepository;
    private final MatchRepository matchRepository;
    private final LineupRepository lineupRepository;
    private final TimelineRepository timelineRepository;
    private final StandingRepository standingRepository;

     @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("===== DataInitializer 시작 =====");
 
        for (Map.Entry<String, String> entry : properties.getLeagues().entrySet()) {
            String leagueName = entry.getKey();
            Integer leagueId = Integer.parseInt(entry.getValue());
 
            try {
                log.info("리그 수집 시작: {} (ID: {})", leagueName, leagueId);
 
                // 리그 저장
                League league = saveLeague(leagueId);
                delay();
 
                // 시즌별 데이터 수집
                for (String yearStr : properties.getSeasons()) {
                    Integer year = Integer.parseInt(yearStr);
                    log.info("시즌 수집 시작: {}-{}", year, (year + 1));
 
                    // 시즌 저장
                    Season season = saveSeason(league, year);
                    delay();
 
                    // 팀 + 선수 저장
                    saveTeamsAndPlayers(league, season, year);
                    delay();
 
                    // 순위 저장
                    // 챔스(2001)는 2024-25부터 리그 페이즈 (단일 순위표)
                    // 2022-23, 2023-24는 조별리그 방식이라 순위 스킵
                    if (isLeaguePhase(league.getId(), year)) {
                        saveStandings(league, season, year);
                        delay();
                    } else {
                        log.info("조별리그 방식 시즌 - 순위 스킵: {} {}-{}", leagueName, year, year + 1);
                    }
 
                    // 6. 경기 목록 저장
                    saveMatches(league, season, year);
                    delay();
                }
 
                log.info("리그 수집 완료: {}", leagueName);
 
            } catch (Exception e) {
                log.error("리그 수집 실패: {} - {}", leagueName, e.getMessage());
            }
        }
 
        log.info("===== DataInitializer 완료 =====");
    }
 
    // 챔피언스리그 리그페이즈 여부 
    private boolean isLeaguePhase(Long leagueId, Integer year) {
        // 챔스(2001)는 2024-25 시즌부터 단일 리그 순위표
        if (leagueId == 2001L || leagueId == 2146L) {
            return year >= 2024;
        }
        // 5대 리그는 항상 단일 순위표
        return true;
    }
 
    // 리그 저장
    private League saveLeague(Integer leagueId) {
        // 이미 존재하면 스킵
        if (leagueRepository.existsById(Long.valueOf(leagueId))) {
            log.info("이미 존재하는 리그 - 스킵: {}", leagueId);
            return leagueRepository.findById(Long.valueOf(leagueId)).get();
        }
 
        FootballApiDto.CompetitionResponse response = footballWebClient.get()
                .uri("/competitions/{id}", leagueId)
                .retrieve()
                .bodyToMono(FootballApiDto.CompetitionResponse.class)
                .block();
 
        League league = League.builder()
                .id(response.getId())
                .name(response.getName())
                .country(response.getArea() != null ? response.getArea().getName() : null)
                .emblemUrl(response.getEmblem())
                .build();
 
        log.info("리그 저장 완료: {}", league.getName());
        return leagueRepository.save(league);
    }
 
    // 시즌 저장 
    private Season saveSeason(League league, Integer year) {
        return seasonRepository.findByLeagueIdAndYear(league.getId(), String.valueOf(year))
                .orElseGet(() -> {
                    boolean isCurrent = year == 2025; // 2025-26이 현재 시즌
                    Season season = Season.builder()
                            .league(league)
                            .year(String.valueOf(year))
                            .displayName(year + "-" + String.valueOf(year + 1).substring(2))
                            .isCurrent(isCurrent)
                            .build();
                    log.info("시즌 저장 완료: {}", season.getDisplayName());
                    return seasonRepository.save(season);
                });
    }
 
    // 팀 + 선수 저장
    private void saveTeamsAndPlayers(League league, Season season, Integer year) {
        try {
            FootballApiDto.TeamsResponse response = footballWebClient.get()
                    .uri("/competitions/{id}/teams?season={year}", league.getId(), year)
                    .retrieve()
                    .bodyToMono(FootballApiDto.TeamsResponse.class)
                    .block();
 
            if (response == null || response.getTeams() == null) {
                log.warn("팀 데이터 없음: leagueId={}, year={}", league.getId(), year);
                return;
            }
 
            for (FootballApiDto.TeamDto teamDto : response.getTeams()) {
 
                // 팀 저장 (이미 존재하면 스킵)
                Team team = teamRepository.findById(teamDto.getId())
                        .orElseGet(() -> {
                            Team newTeam = Team.builder()
                                    .id(teamDto.getId())
                                    .league(league)
                                    .name(teamDto.getName())
                                    .shortName(teamDto.getShortName())
                                    .tla(teamDto.getTla())
                                    .emblemUrl(teamDto.getCrest())
                                    .founded(teamDto.getFounded())
                                    .venue(teamDto.getVenue())
                                    .build();
                            log.info("팀 저장: {}", newTeam.getName());
                            return teamRepository.save(newTeam);
                        });
 
                // 선수 저장 (squad 있을 때만)
                if (teamDto.getSquad() != null && !teamDto.getSquad().isEmpty()) {
                    for (FootballApiDto.PlayerDto playerDto : teamDto.getSquad()) {
                        if (!playerRepository.existsById(playerDto.getId())) {
                            Player player = Player.builder()
                                    .id(playerDto.getId())
                                    .team(team)
                                    .name(playerDto.getName())
                                    .firstName(playerDto.getFirstName())
                                    .lastName(playerDto.getLastName())
                                    .shirtNumber(playerDto.getShirtNumber())
                                    .position(playerDto.getPosition())
                                    .nationality(playerDto.getNationality())
                                    .dateOfBirth(parseDate(playerDto.getDateOfBirth()))
                                    .build();
                            playerRepository.save(player);
                        }
                    }
                    log.info("선수 저장 완료: {} - {}명", team.getName(), teamDto.getSquad().size());
                }
            }
 
        } catch (Exception e) {
            log.error("팀/선수 저장 실패: leagueId={}, year={} - {}", league.getId(), year, e.getMessage());
        }
    }
 
    // 순위 저장
    private void saveStandings(League league, Season season, Integer year) {
        try {
            FootballApiDto.StandingsResponse response = footballWebClient.get()
                    .uri("/competitions/{id}/standings?season={year}", league.getId(), year)
                    .retrieve()
                    .bodyToMono(FootballApiDto.StandingsResponse.class)
                    .block();
 
            if (response == null || response.getStandings() == null) {
                log.warn("순위 데이터 없음: leagueId={}, year={}", league.getId(), year);
                return;
            }
 
            // TOTAL 타입만 저장 (HOME/AWAY 제외)
            response.getStandings().stream()
                    .filter(s -> "TOTAL".equals(s.getType()))
                    .findFirst()
                    .ifPresent(table -> {
                        for (FootballApiDto.StandingDto dto : table.getTable()) {
                            Team team = teamRepository.findById(dto.getTeam().getId())
                                    .orElse(null);
                            if (team == null) {
                                log.warn("팀 없음 - 순위 저장 스킵: teamId={}", dto.getTeam().getId());
                                return;
                            }
 
                            // 이미 존재하면 스킵
                            standingRepository.findBySeasonIdAndTeamId(season.getId(), team.getId())
                                    .orElseGet(() -> standingRepository.save(
                                        Standing.builder()
                                            .league(league)
                                            .season(season)
                                            .team(team)
                                            .rank(dto.getPosition())
                                            .played(dto.getPlayedGames())
                                            .won(dto.getWon())
                                            .drawn(dto.getDraw())
                                            .lost(dto.getLost())
                                            .goalsFor(dto.getGoalsFor())
                                            .goalsAgainst(dto.getGoalsAgainst())
                                            .goalDiff(dto.getGoalDifference())
                                            .points(dto.getPoints())
                                            .form(dto.getForm())
                                            .build()
                                    ));
                        }
                        log.info("순위 저장 완료: leagueId={}, year={}, {}팀",
                                league.getId(), year, table.getTable().size());
                    });
 
        } catch (Exception e) {
            log.error("순위 저장 실패: leagueId={}, year={} - {}", league.getId(), year, e.getMessage());
        }
    }
 
    // 경기 저장 
    private void saveMatches(League league, Season season, Integer year) {
        try {
            FootballApiDto.MatchesResponse response = footballWebClient.get()
                    .uri("/competitions/{id}/matches?season={year}", league.getId(), year)
                    .retrieve()
                    .bodyToMono(FootballApiDto.MatchesResponse.class)
                    .block();
 
            if (response == null || response.getMatches() == null) {
                log.warn("경기 데이터 없음: leagueId={}, year={}", league.getId(), year);
                return;
            }
 
            int savedCount = 0;
            for (FootballApiDto.MatchDto matchDto : response.getMatches()) {
 
                // 이미 존재하면 스킵
                if (matchRepository.existsById(matchDto.getId())) continue;

                // 팀 ID 자체가 null인 경우 먼저 확인
                if (matchDto.getHomeTeam() == null || matchDto.getHomeTeam().getId() == null ||
                matchDto.getAwayTeam() == null || matchDto.getAwayTeam().getId() == null) {
                log.warn("팀 정보 없음 - 경기 스킵: matchId={}", matchDto.getId());
                continue;
    }
 
                Team homeTeam = teamRepository.findById(matchDto.getHomeTeam().getId())
                        .orElse(null);
                Team awayTeam = teamRepository.findById(matchDto.getAwayTeam().getId())
                        .orElse(null);
 
                if (homeTeam == null || awayTeam == null) {
                    log.warn("팀 없음 - 경기 저장 스킵: matchId={}", matchDto.getId());
                    continue;
                }
 
                Integer homeScore = 0;
                Integer awayScore = 0;
                if (matchDto.getScore() != null && matchDto.getScore().getFullTime() != null) {
                    homeScore = matchDto.getScore().getFullTime().getHome() != null
                            ? matchDto.getScore().getFullTime().getHome() : 0;
                    awayScore = matchDto.getScore().getFullTime().getAway() != null
                            ? matchDto.getScore().getFullTime().getAway() : 0;
                }
 
                matchRepository.save(
                    Match.builder()
                        .id(matchDto.getId())
                        .league(league)
                        .season(season)
                        .homeTeam(homeTeam)
                        .awayTeam(awayTeam)
                        .matchDate(parseDateTime(matchDto.getUtcDate()))
                        .matchday(matchDto.getMatchday())
                        .status(matchDto.getStatus())
                        .homeScore(homeScore)
                        .awayScore(awayScore)
                        .build()
                );
                savedCount++;
            }
 
            log.info("경기 저장 완료: leagueId={}, year={}, {}경기", league.getId(), year, savedCount);
 
        } catch (Exception e) {
            log.error("경기 저장 실패: leagueId={}, year={} - {}", league.getId(), year, e.getMessage());
        }
    }
 
    // 유틸 메서드 
 
    // API 호출 간격 (분당 10회 제한 대응 - 6.5초 간격)
    private void delay() {
        try {
            Thread.sleep(6500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
 
    // 날짜 파싱 (yyyy-MM-dd)
    private LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) return null;
        try {
            return LocalDate.parse(dateStr.substring(0, 10));
        } catch (Exception e) {
            log.warn("날짜 파싱 실패: {}", dateStr);
            return null;
        }
    }
 
    // 날짜시간 파싱 (ISO 8601)
    private LocalDateTime parseDateTime(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) return null;
        try {
            return LocalDateTime.parse(dateStr, DateTimeFormatter.ISO_DATE_TIME);
        } catch (Exception e) {
            log.warn("날짜시간 파싱 실패: {}", dateStr);
            return null;
        }
    }
}
