package com.mlb.mlb_back.Global.config;

import com.mlb.mlb_back.Domain.game.entity.Game;
import com.mlb.mlb_back.Domain.game.entity.LineScore;
import com.mlb.mlb_back.Domain.player.entity.Player;
import com.mlb.mlb_back.Domain.standing.entity.Standing;
import com.mlb.mlb_back.Domain.stat.entity.BatterStat;
import com.mlb.mlb_back.Domain.stat.entity.PitchData;
import com.mlb.mlb_back.Domain.stat.entity.PitcherStat;
import com.mlb.mlb_back.Domain.team.entity.Team;
import com.mlb.mlb_back.Domain.game.repository.GameRepository;
import com.mlb.mlb_back.Domain.game.repository.LineScoreRepository;
import com.mlb.mlb_back.Domain.player.repository.PlayerRepository;
import com.mlb.mlb_back.Domain.standing.repository.StandingRepository;
import com.mlb.mlb_back.Domain.stat.repository.BatterStatRepository;
import com.mlb.mlb_back.Domain.stat.repository.PitchDataRepository;
import com.mlb.mlb_back.Domain.stat.repository.PitcherStatRepository;
import com.mlb.mlb_back.Domain.team.repository.TeamRepository;
import com.mlb.mlb_back.Domain.user.repository.FavoriteRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
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

    private final TeamRepository teamRepository;
    private final PlayerRepository playerRepository;
    private final GameRepository gameRepository;
    private final LineScoreRepository lineScoreRepository;
    private final StandingRepository standingRepository;
    private final BatterStatRepository batterStatRepository;
    private final PitcherStatRepository pitcherStatRepository;
    private final PitchDataRepository pitchDataRepository;
    private final FavoriteRepository favoriteRepository;

    private final WebClient webClient = WebClient.builder()
        .baseUrl("https://statsapi.mlb.com/api/v1")
        .exchangeStrategies(
                ExchangeStrategies.builder()
                        .codecs(configurer ->
                                configurer.defaultCodecs()
                                        .maxInMemorySize(16 * 1024 * 1024)
                        )
                        .build()
        )
        .build();

    private static final List<Integer> SEASONS = List.of(2024, 2025, 2026);

    @Override
    public void run(ApplicationArguments args) {
        log.info("===== MLB DataInitializer 시작 =====");

        initTeams();
        initPlayers();
        for (int season : SEASONS) {
            initGames(season);
            initStandings(season);
            initBatterStats(season);
            initPitcherStats(season);
        }

        log.info("===== MLB DataInitializer 완료 =====");
    }

    // ================================================
    // 팀 데이터 수집
    // ================================================
    @SuppressWarnings("unchecked")
    private void initTeams() {
        if (teamRepository.count() > 0) {
            log.info("팀 데이터 이미 존재, 스킵");
            return;
        }

        log.info("팀 데이터 수집 시작...");

        Map<String, Object> response = webClient.get()
                .uri("/teams?sportId=1&activeStatus=Y")
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        if (response == null) return;

        List<Map<String, Object>> teams = (List<Map<String, Object>>) response.get("teams");
        if (teams == null) return;

        for (Map<String, Object> t : teams) {
            try {
                Long id = Long.valueOf(t.get("id").toString());

                Map<String, Object> leagueMap = (Map<String, Object>) t.get("league");
                Map<String, Object> divisionMap = (Map<String, Object>) t.get("division");
                Map<String, Object> venueMap = (Map<String, Object>) t.get("venue");

                String league = leagueMap != null ? leagueMap.get("name").toString() : "";
                String division = divisionMap != null ? divisionMap.get("name").toString() : "";
                String venue = venueMap != null ? venueMap.get("name").toString() : "";

                Team team = Team.builder()
                        .id(id)
                        .name(t.getOrDefault("name", "").toString())
                        .abbreviation(t.getOrDefault("abbreviation", "").toString())
                        .teamName(t.getOrDefault("teamName", "").toString())
                        .locationName(t.getOrDefault("locationName", "").toString())
                        .league(league)
                        .division(division)
                        .venue(venue)
                        .logoUrl("https://www.mlbstatic.com/team-logos/" + id + ".svg")
                        .build();

                teamRepository.save(team);
            } catch (Exception e) {
                log.warn("팀 저장 실패: {}", e.getMessage());
            }
        }

        log.info("팀 데이터 수집 완료: {}개", teamRepository.count());
    }

    // ================================================
    // 선수 데이터 수집
    // ================================================
    @SuppressWarnings("unchecked")
    private void initPlayers() {
        if (playerRepository.count() > 0) {
            log.info("선수 데이터 이미 존재, 스킵");
            return;
        }

        log.info("선수 데이터 수집 시작...");

        List<Team> teams = teamRepository.findAll();

        for (Team team : teams) {
            try {
                Map<String, Object> response = webClient.get()
                        .uri("/teams/" + team.getId() + "/roster?rosterType=fullRoster&season=2025")
                        .retrieve()
                        .bodyToMono(Map.class)
                        .block();

                if (response == null) continue;

                List<Map<String, Object>> roster = (List<Map<String, Object>>) response.get("roster");
                if (roster == null) continue;

                for (Map<String, Object> r : roster) {
                    try {
                        Map<String, Object> personMap = (Map<String, Object>) r.get("person");
                        Map<String, Object> positionMap = (Map<String, Object>) r.get("position");

                        Long playerId = Long.valueOf(personMap.get("id").toString());

                        if (playerRepository.existsById(playerId)) continue;

                        // 선수 상세 정보 조회
                        Map<String, Object> detailResponse = webClient.get()
                                .uri("/people/" + playerId)
                                .retrieve()
                                .bodyToMono(Map.class)
                                .block();

                        String firstName = "", lastName = "", fullName = "";
                        String batSide = "", pitchHand = "", nationality = "";
                        LocalDate dateOfBirth = null;

                        if (detailResponse != null) {
                            List<Map<String, Object>> people = (List<Map<String, Object>>) detailResponse.get("people");
                            if (people != null && !people.isEmpty()) {
                                Map<String, Object> p = people.get(0);
                                firstName = p.getOrDefault("firstName", "").toString();
                                lastName = p.getOrDefault("lastName", "").toString();
                                fullName = p.getOrDefault("fullName", "").toString();
                                nationality = p.getOrDefault("birthCountry", "").toString();

                                Map<String, Object> batSideMap = (Map<String, Object>) p.get("batSide");
                                Map<String, Object> pitchHandMap = (Map<String, Object>) p.get("pitchHand");

                                if (batSideMap != null) batSide = batSideMap.getOrDefault("code", "").toString();
                                if (pitchHandMap != null) pitchHand = pitchHandMap.getOrDefault("code", "").toString();

                                String dob = p.getOrDefault("birthDate", "").toString();
                                if (!dob.isEmpty()) {
                                    try {
                                        dateOfBirth = LocalDate.parse(dob);
                                    } catch (Exception ignored) {}
                                }
                            }
                        }

                        String position = positionMap != null ? positionMap.getOrDefault("abbreviation", "").toString() : "";
                        String jerseyNumber = r.getOrDefault("jerseyNumber", "").toString();

                        Player player = Player.builder()
                                .id(playerId)
                                .team(team)
                                .fullName(fullName.isEmpty() ? personMap.get("fullName").toString() : fullName)
                                .firstName(firstName)
                                .lastName(lastName)
                                .position(position)
                                .shirtNumber(jerseyNumber)
                                .batSide(batSide)
                                .pitchHand(pitchHand)
                                .dateOfBirth(dateOfBirth)
                                .nationality(nationality)
                                .photoUrl("https://img.mlbstatic.com/mlb-photos/image/upload/d_people:generic:headshot:67:current.png/w_213,q_auto:best/v1/people/" + playerId + "/headshot/67/current")
                                .isActive(true)
                                .build();

                        playerRepository.save(player);

                    } catch (Exception e) {
                        log.warn("선수 저장 실패: {}", e.getMessage());
                    }
                }

                log.info("팀 {} 선수 수집 완료", team.getName());
                Thread.sleep(200); // API 요청 간격

            } catch (Exception e) {
                log.warn("팀 {} 로스터 조회 실패: {}", team.getName(), e.getMessage());
            }
        }

        log.info("선수 데이터 수집 완료: {}명", playerRepository.count());
    }

    // ================================================
    // 경기 데이터 수집
    // ================================================
    @SuppressWarnings("unchecked")
    private void initGames(int season) {
        long count = gameRepository.countBySeason(season);
        if (count > 0) {
            log.info("{}시즌 경기 데이터 이미 존재, 스킵", season);
            return;
        }

        log.info("{}시즌 경기 데이터 수집 시작...", season);

        Map<String, Object> response = webClient.get()
                .uri("/schedule?sportId=1&season=" + season + "&gameType=R")
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        if (response == null) return;

        List<Map<String, Object>> dates = (List<Map<String, Object>>) response.get("dates");
        if (dates == null) return;

        int savedCount = 0;

        for (Map<String, Object> dateObj : dates) {
            List<Map<String, Object>> games = (List<Map<String, Object>>) dateObj.get("games");
            if (games == null) continue;

            for (Map<String, Object> g : games) {
                try {
                    Long gameId = Long.valueOf(g.get("gamePk").toString());

                    if (gameRepository.existsById(gameId)) continue;

                    Map<String, Object> teams = (Map<String, Object>) g.get("teams");
                    Map<String, Object> homeMap = (Map<String, Object>) teams.get("home");
                    Map<String, Object> awayMap = (Map<String, Object>) teams.get("away");
                    Map<String, Object> homeTeamMap = (Map<String, Object>) homeMap.get("team");
                    Map<String, Object> awayTeamMap = (Map<String, Object>) awayMap.get("team");

                    Long homeTeamId = Long.valueOf(homeTeamMap.get("id").toString());
                    Long awayTeamId = Long.valueOf(awayTeamMap.get("id").toString());

                    Team homeTeam = teamRepository.findById(homeTeamId).orElse(null);
                    Team awayTeam = teamRepository.findById(awayTeamId).orElse(null);

                    if (homeTeam == null || awayTeam == null) continue;

                    Map<String, Object> statusMap = (Map<String, Object>) g.get("status");
                    String status = statusMap != null ? statusMap.getOrDefault("detailedState", "").toString() : "";

                    String gameDateStr = g.getOrDefault("gameDate", "").toString();
                    LocalDateTime gameDate = null;
                    if (!gameDateStr.isEmpty()) {
                        try {
                            gameDate = LocalDateTime.parse(gameDateStr, DateTimeFormatter.ISO_DATE_TIME);
                        } catch (Exception ignored) {}
                    }

                    Integer homeScore = null, awayScore = null;
                    Object homeScoreObj = homeMap.get("score");
                    Object awayScoreObj = awayMap.get("score");
                    if (homeScoreObj != null) homeScore = Integer.valueOf(homeScoreObj.toString());
                    if (awayScoreObj != null) awayScore = Integer.valueOf(awayScoreObj.toString());

                    Map<String, Object> venueMap = (Map<String, Object>) g.get("venue");
                    String venue = venueMap != null ? venueMap.getOrDefault("name", "").toString() : "";

                    Game game = Game.builder()
                            .id(gameId)
                            .homeTeam(homeTeam)
                            .awayTeam(awayTeam)
                            .gameDate(gameDate)
                            .season(season)
                            .status(status)
                            .homeScore(homeScore)
                            .awayScore(awayScore)
                            .venue(venue)
                            .gameNumber(g.get("gameNumber") != null ? Integer.valueOf(g.get("gameNumber").toString()) : 1)
                            .seriesDescription(g.getOrDefault("seriesDescription", "Regular Season").toString())
                            .build();

                    gameRepository.save(game);
                    savedCount++;

                } catch (Exception e) {
                    log.warn("경기 저장 실패: {}", e.getMessage());
                }
            }
        }

        log.info("{}시즌 경기 데이터 수집 완료: {}경기", season, savedCount);
    }

    // ================================================
    // 순위 데이터 수집
    // ================================================
    @SuppressWarnings("unchecked")
    private void initStandings(int season) {
        long count = standingRepository.countBySeason(season);
        if (count > 0) {
            log.info("{}시즌 순위 데이터 이미 존재, 스킵", season);
            return;
        }

        log.info("{}시즌 순위 데이터 수집 시작...", season);

        // AL(103) + NL(104) 둘 다 조회
        for (int leagueId : List.of(103, 104)) {
            try {
                Map<String, Object> response = webClient.get()
                        .uri("/standings?leagueId=" + leagueId + "&season=" + season + "&standingsTypes=regularSeason")
                        .retrieve()
                        .bodyToMono(Map.class)
                        .block();

                if (response == null) continue;

                List<Map<String, Object>> records = (List<Map<String, Object>>) response.get("records");
                if (records == null) continue;

                for (Map<String, Object> record : records) {
                    List<Map<String, Object>> teamRecords = (List<Map<String, Object>>) record.get("teamRecords");
                    if (teamRecords == null) continue;

                    int divisionRank = 1;
                    for (Map<String, Object> tr : teamRecords) {
                        try {
                            Map<String, Object> teamMap = (Map<String, Object>) tr.get("team");
                            Long teamId = Long.valueOf(teamMap.get("id").toString());
                            Team team = teamRepository.findById(teamId).orElse(null);
                            if (team == null) continue;

                            Map<String, Object> leagueRecordMap = (Map<String, Object>) tr.get("leagueRecord");
                            int wins = Integer.parseInt(leagueRecordMap.get("wins").toString());
                            int losses = Integer.parseInt(leagueRecordMap.get("losses").toString());
                            double winPct = Double.parseDouble(leagueRecordMap.get("pct").toString());

                            Object gbObj = tr.get("gamesBack");
                            double gamesBack = 0.0;
                            if (gbObj != null && !gbObj.toString().equals("-")) {
                                try { gamesBack = Double.parseDouble(gbObj.toString()); } catch (Exception ignored) {}
                            }

                            Map<String, Object> streakMap = (Map<String, Object>) tr.get("streak");
                            String streak = streakMap != null ? streakMap.getOrDefault("streakCode", "").toString() : "";

                            Map<String, Object> lastTenMap = (Map<String, Object>) tr.get("records");
                            int lastTenWins = 0, lastTenLosses = 0;
                            if (lastTenMap != null) {
                                List<Map<String, Object>> splitRecords = (List<Map<String, Object>>) lastTenMap.get("splitRecords");
                                if (splitRecords != null) {
                                    for (Map<String, Object> split : splitRecords) {
                                        if ("lastTen".equals(split.get("type"))) {
                                            lastTenWins = Integer.parseInt(split.get("wins").toString());
                                            lastTenLosses = Integer.parseInt(split.get("losses").toString());
                                        }
                                    }
                                }
                            }

                            int runsScored = tr.get("runsScored") != null ? Integer.parseInt(tr.get("runsScored").toString()) : 0;
                            int runsAllowed = tr.get("runsAllowed") != null ? Integer.parseInt(tr.get("runsAllowed").toString()) : 0;

                            Standing standing = Standing.builder()
                                    .team(team)
                                    .season(season)
                                    .divisionRank(divisionRank++)
                                    .wins(wins)
                                    .losses(losses)
                                    .winPct(winPct)
                                    .gamesBack(gamesBack)
                                    .streak(streak)
                                    .lastTenWins(lastTenWins)
                                    .lastTenLosses(lastTenLosses)
                                    .runsScored(runsScored)
                                    .runsAllowed(runsAllowed)
                                    .runDifferential(runsScored - runsAllowed)
                                    .build();

                            standingRepository.save(standing);

                        } catch (Exception e) {
                            log.warn("순위 저장 실패: {}", e.getMessage());
                        }
                    }
                }
            } catch (Exception e) {
                log.warn("{}시즌 순위 조회 실패 (리그 {}): {}", season, leagueId, e.getMessage());
            }
        }

        log.info("{}시즌 순위 데이터 수집 완료", season);
    }

    // ================================================
    // 타자 시즌 스탯 수집
    // ================================================
    @SuppressWarnings("unchecked")
    private void initBatterStats(int season) {
        long count = batterStatRepository.countBySeason(season);
        if (count > 0) {
            log.info("{}시즌 타자 스탯 이미 존재, 스킵", season);
            return;
        }

        log.info("{}시즌 타자 스탯 수집 시작...", season);

        List<Team> teams = teamRepository.findAll();

        for (Team team : teams) {
            try {
                Map<String, Object> response = webClient.get()
                        .uri("/teams/" + team.getId() + "/stats?stats=season&group=hitting&season=" + season)
                        .retrieve()
                        .bodyToMono(Map.class)
                        .block();

                if (response == null) continue;

                List<Map<String, Object>> statsList = (List<Map<String, Object>>) response.get("stats");
                if (statsList == null || statsList.isEmpty()) continue;

                List<Map<String, Object>> splits = (List<Map<String, Object>>) statsList.get(0).get("splits");
                if (splits == null) continue;

                for (Map<String, Object> split : splits) {
                    try {
                        Map<String, Object> playerMap = (Map<String, Object>) split.get("player");
                        if (playerMap == null) continue;

                        Long playerId = Long.valueOf(playerMap.get("id").toString());
                        Player player = playerRepository.findById(playerId).orElse(null);
                        if (player == null) continue;

                        if (batterStatRepository.existsByPlayerIdAndSeasonAndTeamId(playerId, season, team.getId())) continue;

                        Map<String, Object> stat = (Map<String, Object>) split.get("stat");
                        if (stat == null) continue;

                        BatterStat batterStat = BatterStat.builder()
                                .player(player)
                                .team(team)
                                .season(season)
                                .gamesPlayed(parseIntSafe(stat.get("gamesPlayed")))
                                .atBats(parseIntSafe(stat.get("atBats")))
                                .hits(parseIntSafe(stat.get("hits")))
                                .doubles(parseIntSafe(stat.get("doubles")))
                                .triples(parseIntSafe(stat.get("triples")))
                                .homeRuns(parseIntSafe(stat.get("homeRuns")))
                                .rbi(parseIntSafe(stat.get("rbi")))
                                .runs(parseIntSafe(stat.get("runs")))
                                .stolenBases(parseIntSafe(stat.get("stolenBases")))
                                .walks(parseIntSafe(stat.get("baseOnBalls")))
                                .strikeOuts(parseIntSafe(stat.get("strikeOuts")))
                                .hitByPitch(parseIntSafe(stat.get("hitByPitch")))
                                .avg(parseDoubleSafe(stat.get("avg")))
                                .obp(parseDoubleSafe(stat.get("obp")))
                                .slg(parseDoubleSafe(stat.get("slg")))
                                .ops(parseDoubleSafe(stat.get("ops")))
                                .build();

                        batterStatRepository.save(batterStat);

                    } catch (Exception e) {
                        log.warn("타자 스탯 저장 실패: {}", e.getMessage());
                    }
                }

                Thread.sleep(100);

            } catch (Exception e) {
                log.warn("팀 {} 타자 스탯 조회 실패: {}", team.getName(), e.getMessage());
            }
        }

        log.info("{}시즌 타자 스탯 수집 완료", season);
    }

    // ================================================
    // 투수 시즌 스탯 수집
    // ================================================
    @SuppressWarnings("unchecked")
    private void initPitcherStats(int season) {
        long count = pitcherStatRepository.countBySeason(season);
        if (count > 0) {
            log.info("{}시즌 투수 스탯 이미 존재, 스킵", season);
            return;
        }

        log.info("{}시즌 투수 스탯 수집 시작...", season);

        List<Team> teams = teamRepository.findAll();

        for (Team team : teams) {
            try {
                Map<String, Object> response = webClient.get()
                        .uri("/teams/" + team.getId() + "/stats?stats=season&group=pitching&season=" + season)
                        .retrieve()
                        .bodyToMono(Map.class)
                        .block();

                if (response == null) continue;

                List<Map<String, Object>> statsList = (List<Map<String, Object>>) response.get("stats");
                if (statsList == null || statsList.isEmpty()) continue;

                List<Map<String, Object>> splits = (List<Map<String, Object>>) statsList.get(0).get("splits");
                if (splits == null) continue;

                for (Map<String, Object> split : splits) {
                    try {
                        Map<String, Object> playerMap = (Map<String, Object>) split.get("player");
                        if (playerMap == null) continue;

                        Long playerId = Long.valueOf(playerMap.get("id").toString());
                        Player player = playerRepository.findById(playerId).orElse(null);
                        if (player == null) continue;

                        if (pitcherStatRepository.existsByPlayerIdAndSeasonAndTeamId(playerId, season, team.getId())) continue;

                        Map<String, Object> stat = (Map<String, Object>) split.get("stat");
                        if (stat == null) continue;

                        PitcherStat pitcherStat = PitcherStat.builder()
                                .player(player)
                                .team(team)
                                .season(season)
                                .gamesPlayed(parseIntSafe(stat.get("gamesPitched")))
                                .gamesStarted(parseIntSafe(stat.get("gamesStarted")))
                                .wins(parseIntSafe(stat.get("wins")))
                                .losses(parseIntSafe(stat.get("losses")))
                                .saves(parseIntSafe(stat.get("saves")))
                                .holds(parseIntSafe(stat.get("holds")))
                                .inningsPitched(parseDoubleSafe(stat.get("inningsPitched")))
                                .strikeOuts(parseIntSafe(stat.get("strikeOuts")))
                                .walks(parseIntSafe(stat.get("baseOnBalls")))
                                .hitsAllowed(parseIntSafe(stat.get("hits")))
                                .homeRunsAllowed(parseIntSafe(stat.get("homeRuns")))
                                .earnedRuns(parseIntSafe(stat.get("earnedRuns")))
                                .era(parseDoubleSafe(stat.get("era")))
                                .whip(parseDoubleSafe(stat.get("whip")))
                                .avgAllowed(parseDoubleSafe(stat.get("avg")))
                                .strikeOutPer9(parseDoubleSafe(stat.get("strikeoutsPer9Inn")))
                                .walkPer9(parseDoubleSafe(stat.get("walksPer9Inn")))
                                .build();

                        pitcherStatRepository.save(pitcherStat);

                    } catch (Exception e) {
                        log.warn("투수 스탯 저장 실패: {}", e.getMessage());
                    }
                }

                Thread.sleep(100);

            } catch (Exception e) {
                log.warn("팀 {} 투수 스탯 조회 실패: {}", team.getName(), e.getMessage());
            }
        }

        log.info("{}시즌 투수 스탯 수집 완료", season);

    }

    // ================================================
    // PitchData 수집
    // ================================================
    @SuppressWarnings("unchecked")
    private void initPitchData(int season) {

        log.info("{} 시즌 PitchData 수집 시작...", season);

        List<Game> games = gameRepository.findBySeason(season);

        for (Game game : games) {

            try {

                Map<String, Object> response = webClient.get()
                        .uri("/game/" + game.getId() + "/feed/live")
                        .retrieve()
                        .bodyToMono(Map.class)
                        .block();

                if (response == null) continue;

                Map<String, Object> liveData =
                        (Map<String, Object>) response.get("liveData");

                if (liveData == null) continue;

                Map<String, Object> plays =
                        (Map<String, Object>) liveData.get("plays");

                if (plays == null) continue;

                List<Map<String, Object>> allPlays =
                        (List<Map<String, Object>>) plays.get("allPlays");

                if (allPlays == null) continue;

                for (Map<String, Object> play : allPlays) {

                    try {

                        Map<String, Object> matchup =
                                (Map<String, Object>) play.get("matchup");

                        if (matchup == null) continue;

                        Map<String, Object> pitcherMap =
                                (Map<String, Object>) matchup.get("pitcher");

                        Map<String, Object> batterMap =
                                (Map<String, Object>) matchup.get("batter");

                        if (pitcherMap == null || batterMap == null) continue;

                        Long pitcherId =
                                Long.valueOf(pitcherMap.get("id").toString());

                        Long batterId =
                                Long.valueOf(batterMap.get("id").toString());

                        Player pitcher =
                                playerRepository.findById(pitcherId).orElse(null);

                        Player batter =
                                playerRepository.findById(batterId).orElse(null);

                        if (pitcher == null || batter == null) continue;

                        List<Map<String, Object>> playEvents =
                                (List<Map<String, Object>>) play.get("playEvents");

                        if (playEvents == null) continue;

                        for (Map<String, Object> event : playEvents) {

                            try {

                                Boolean isPitch =
                                        (Boolean) event.get("isPitch");

                                if (isPitch == null || !isPitch) continue;

                                Map<String, Object> details =
                                        (Map<String, Object>) event.get("details");

                                Map<String, Object> pitchDataMap =
                                        (Map<String, Object>) event.get("pitchData");

                                if (details == null || pitchDataMap == null)
                                    continue;

                                // 구종
                                String pitchType = null;

                                if (details.get("type") != null) {

                                    Map<String, Object> typeMap =
                                            (Map<String, Object>) details.get("type");

                                    pitchType =
                                            typeMap.getOrDefault("code", "")
                                                    .toString();
                                }

                                // 구속
                                Double velocity =
                                        parseDoubleSafe(
                                                pitchDataMap.get("startSpeed")
                                        );

                                // 좌표
                                Map<String, Object> coordinates =
                                        (Map<String, Object>)
                                                pitchDataMap.get("coordinates");

                                Double plateX = null;
                                Double plateZ = null;

                                if (coordinates != null) {

                                    plateX =
                                            parseDoubleSafe(
                                                    coordinates.get("pX")
                                            );

                                    plateZ =
                                            parseDoubleSafe(
                                                    coordinates.get("pZ")
                                            );
                                }

                                // 결과
                                String result =
                                        details.get("description") != null
                                                ? details.get("description")
                                                        .toString()
                                                : null;

                                // inning
                                Integer inning = null;

                                if (play.get("about") != null) {

                                    Map<String, Object> aboutMap =
                                            (Map<String, Object>) play.get("about");

                                    inning =
                                            parseIntSafe(
                                                    aboutMap.get("inning")
                                            );
                                }

                                // count
                                Integer balls = 0;
                                Integer strikes = 0;
                                Integer outs = 0;

                                if (event.get("count") != null) {

                                    Map<String, Object> countMap =
                                            (Map<String, Object>) event.get("count");

                                    balls =
                                            parseIntSafe(countMap.get("balls"));

                                    strikes =
                                            parseIntSafe(countMap.get("strikes"));

                                    outs =
                                            parseIntSafe(countMap.get("outs"));
                                }

                                // 타구 데이터
                                Double launchSpeed = null;
                                Double launchAngle = null;
                                Double totalDistance = null;

                                if (event.get("hitData") != null) {

                                    Map<String, Object> hitData =
                                            (Map<String, Object>) event.get("hitData");

                                    launchSpeed =
                                            parseDoubleSafe(
                                                    hitData.get("launchSpeed")
                                            );

                                    launchAngle =
                                            parseDoubleSafe(
                                                    hitData.get("launchAngle")
                                            );

                                    totalDistance =
                                            parseDoubleSafe(
                                                    hitData.get("totalDistance")
                                            );
                                }

                                PitchData pitchData = PitchData.builder()
                                        .game(game)
                                        .pitcher(pitcher)
                                        .batter(batter)
                                        .pitchType(pitchType)
                                        .velocity(velocity)
                                        .plateX(plateX)
                                        .plateZ(plateZ)
                                        .result(result)
                                        .inning(inning)
                                        .balls(balls)
                                        .strikes(strikes)
                                        .outs(outs)
                                        .exitVelocity(launchSpeed)
                                        .launchAngle(launchAngle)
                                        .distance(totalDistance)
                                        .build();

                                pitchDataRepository.save(pitchData);

                            } catch (Exception e) {

                                log.warn(
                                        "Pitch Event 저장 실패: {}",
                                        e.getMessage()
                                );
                            }
                        }

                    } catch (Exception e) {

                        log.warn(
                                "Play 파싱 실패: {}",
                                e.getMessage()
                        );
                    }
                }

                Thread.sleep(100);

            } catch (Exception e) {

                log.warn(
                        "게임 {} PitchData 조회 실패: {}",
                        game.getId(),
                        e.getMessage()
                );
            }
        }

        log.info("{} 시즌 PitchData 수집 완료", season);
    }



    // ================================================
    // 유틸
    // ================================================
    private Integer parseIntSafe(Object obj) {
        if (obj == null) return 0;
        try { return Integer.parseInt(obj.toString()); }
        catch (Exception e) { return 0; }
    }

    private Double parseDoubleSafe(Object obj) {
        if (obj == null) return null;
        try { return Double.parseDouble(obj.toString()); }
        catch (Exception e) { return null; }
    }
}