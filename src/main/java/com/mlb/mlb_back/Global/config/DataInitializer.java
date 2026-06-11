package com.mlb.mlb_back.Global.config;

import com.mlb.mlb_back.Domain.game.entity.BoxScore;
import com.mlb.mlb_back.Domain.game.entity.Game;
import com.mlb.mlb_back.Domain.game.entity.LineScore;
import com.mlb.mlb_back.Domain.player.entity.Player;
import com.mlb.mlb_back.Domain.standing.entity.Standing;
import com.mlb.mlb_back.Domain.stat.entity.BatterStat;
import com.mlb.mlb_back.Domain.stat.entity.HotColdZone;
import com.mlb.mlb_back.Domain.stat.entity.PitchData;
import com.mlb.mlb_back.Domain.stat.entity.PitcherStat;
import com.mlb.mlb_back.Domain.stat.entity.SprayData;
import com.mlb.mlb_back.Domain.stat.entity.BatterSituationStat;
import com.mlb.mlb_back.Domain.stat.entity.BatterVsPitcher;
import com.mlb.mlb_back.Domain.stat.entity.PlayerMonthlyStat;
import com.mlb.mlb_back.Domain.team.entity.Team;
import com.mlb.mlb_back.Domain.game.repository.BoxScoreRepository;
import com.mlb.mlb_back.Domain.game.repository.GameRepository;
import com.mlb.mlb_back.Domain.game.repository.LineScoreRepository;
import com.mlb.mlb_back.Domain.player.repository.PlayerRepository;
import com.mlb.mlb_back.Domain.standing.repository.StandingRepository;
import com.mlb.mlb_back.Domain.stat.repository.BatterStatRepository;
import com.mlb.mlb_back.Domain.stat.repository.HotColdZoneRepository;
import com.mlb.mlb_back.Domain.stat.repository.PitchDataRepository;
import com.mlb.mlb_back.Domain.stat.repository.PitcherStatRepository;
import com.mlb.mlb_back.Domain.stat.repository.SprayDataRepository;
import com.mlb.mlb_back.Domain.stat.repository.BatterSituationStatRepository;
import com.mlb.mlb_back.Domain.stat.repository.BatterVsPitcherRepository;
import com.mlb.mlb_back.Domain.stat.repository.PlayerMonthlyStatRepository;
import com.mlb.mlb_back.Domain.team.repository.TeamRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
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
    private final BoxScoreRepository boxScoreRepository;
    private final HotColdZoneRepository hotColdZoneRepository;
    private final SprayDataRepository sprayDataRepository;
    private final BatterSituationStatRepository batterSituationStatRepository;
    private final BatterVsPitcherRepository batterVsPitcherRepository;
    private final PlayerMonthlyStatRepository playerMonthlyStatRepository;

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

    private static final List<Integer> SEASONS = List.of(2026); // 2024, 2025 시즌 수집 완료 & 채팅방은 gamepk 기준으로 진행이므로 굳이 추가 안해도 됨

    @Override
    public void run(ApplicationArguments args) {
        log.info("===== MLB DataInitializer 시작 =====");

        initTeams();
        initPlayers();
        // 포스트시즌 game_type 코드 목록 (W=월드시리즈, D=디비전, L=챔피언십, F=와일드카드)
        List<String> postSeasonTypes = List.of("W", "D", "L", "F");

        for (int season : SEASONS) {
            // ── 경기 데이터 (정규시즌 + 포스트시즌 모두 initGames에서 game_type별 저장)
            initGames(season);
            initStandings(season);
            initLineScores(season);
            initBoxScores(season);
            initPitchData(season);

            // ── 정규시즌 선수 스탯
            initBatterStats(season, "R");
            initPitcherStats(season, "R");
            initHotColdZones(season, "R");
            initSprayData(season);                          // game FK로 구분 가능
            initBatterSituationStats(season, "R");
            initBatterSplitStats(season, "R");
            initPlayerMonthlyStats(season);                 // 정규시즌 전용
            initBatterVsPitcher(season, "R");

            // 포스트시즌은 시리즈 별로 구현하기
            for (String psType : postSeasonTypes) {
                initBatterStats(season, psType);
                initPitcherStats(season, psType);
                initHotColdZones(season, psType);
                initBatterSituationStats(season, psType);
                initBatterSplitStats(season, psType);
                initBatterVsPitcher(season, psType);
            }
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

        Map<String, Object> response = fetchJson("/teams?sportId=1&activeStatus=Y");

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
                Map<String, Object> response = fetchJson(
                        "/teams/" + team.getId() + "/roster?rosterType=fullRoster&season=2025");

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
                        Map<String, Object> detailResponse = fetchJson("/people/" + playerId);

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
        long existingCount = gameRepository.countBySeason(season);
        log.info("{} 시즌 경기 데이터 수집 시작... (기존 {}개)", season, existingCount);

        Map<String, Object> response = fetchJson("/schedule?sportId=1&season=" + season);

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

                    String gameType = g.getOrDefault("gameType", "").toString();

                    // 시범경기 제외
                    if (gameType.equals("S")) {
                        continue;
                    }

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
                        } catch (Exception e1) {
                            try {
                                gameDate = OffsetDateTime.parse(gameDateStr, DateTimeFormatter.ISO_DATE_TIME)
                                        .toLocalDateTime();
                            } catch (Exception ignored) {
                            }
                        }
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
                            .gameType(gameType)
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
                Map<String, Object> response = fetchJson(
                        "/standings?leagueId=" + leagueId + "&season=" + season + "&standingsTypes=regularSeason");

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
    private void initBatterStats(int season, String gameType) {
        long count = batterStatRepository.countBySeasonAndGameType(season, gameType);
        if (count > 0) {
            log.info("{}시즌 타자 스탯({}) 이미 존재, 스킵", season, gameType);
            return;
        }

        log.info("{}시즌 타자 스탯({}) 수집 시작...", season, gameType);

        // 정규시즌(R) vs 포스트시즌 전체(PS) API 파라미터
        // PS는 postseason 파라미터로 전체 포스트시즌 집계를 가져옴
       String statsParam = "season&group=hitting&season=" + season + "&gameType=" + gameType + "&playerPool=All";

        int offset = 0;
        int limit = 500;
        int savedCount = 0;

        while (true) {
            try {
                Map<String, Object> response = fetchJson(
                        "/stats?stats=" + statsParam + "&limit=" + limit + "&offset=" + offset);

                if (response == null) break;

                List<Map<String, Object>> statsList = (List<Map<String, Object>>) response.get("stats");
                if (statsList == null || statsList.isEmpty()) break;

                Map<String, Object> statsObj = statsList.get(0);
                List<Map<String, Object>> splits = (List<Map<String, Object>>) statsObj.get("splits");
                if (splits == null || splits.isEmpty()) break;

                int totalSplits = parseIntSafe(statsObj.get("totalSplits"));

                for (Map<String, Object> split : splits) {
                    try {
                        Map<String, Object> playerMap = (Map<String, Object>) split.get("player");
                        Map<String, Object> teamMap = (Map<String, Object>) split.get("team");
                        if (playerMap == null || teamMap == null) continue;

                        Long playerId = Long.valueOf(playerMap.get("id").toString());
                        Long teamId = Long.valueOf(teamMap.get("id").toString());

                        Player player = playerRepository.findById(playerId).orElse(null);
                        Team team = teamRepository.findById(teamId).orElse(null);
                        if (player == null || team == null) continue;

                        if (batterStatRepository.existsByPlayerIdAndSeasonAndTeamIdAndGameType(playerId, season, teamId, gameType)) continue;

                        Map<String, Object> stat = (Map<String, Object>) split.get("stat");
                        if (stat == null) continue;

                        BatterStat batterStat = BatterStat.builder()
                                .player(player)
                                .team(team)
                                .season(season)
                                .gameType(gameType)
                                // 출전 기록
                                .gamesPlayed(parseIntSafe(stat.get("gamesPlayed")))
                                .plateAppearances(parseIntSafe(stat.get("plateAppearances")))
                                .atBats(parseIntSafe(stat.get("atBats")))
                                // 타격 기록
                                .hits(parseIntSafe(stat.get("hits")))
                                .doubles(parseIntSafe(stat.get("doubles")))
                                .triples(parseIntSafe(stat.get("triples")))
                                .homeRuns(parseIntSafe(stat.get("homeRuns")))
                                .totalBases(parseIntSafe(stat.get("totalBases")))
                                .runs(parseIntSafe(stat.get("runs")))
                                .rbi(parseIntSafe(stat.get("rbi")))
                                // 출루 기록
                                .walks(parseIntSafe(stat.get("baseOnBalls")))
                                .intentionalWalks(parseIntSafe(stat.get("intentionalWalks")))
                                .hitByPitch(parseIntSafe(stat.get("hitByPitch")))
                                .catchersInterference(parseIntSafe(stat.get("catchersInterference")))
                                // 주루 기록
                                .stolenBases(parseIntSafe(stat.get("stolenBases")))
                                .caughtStealing(parseIntSafe(stat.get("caughtStealing")))
                                .stolenBasePercentage(parseDoubleSafe(stat.get("stolenBasePercentage")))
                                // 삼진 / 병살
                                .strikeOuts(parseIntSafe(stat.get("strikeOuts")))
                                .groundIntoDoublePlay(parseIntSafe(stat.get("groundIntoDoublePlay")))
                                // 희생 기록
                                .sacBunts(parseIntSafe(stat.get("sacBunts")))
                                .sacFlies(parseIntSafe(stat.get("sacFlies")))
                                .leftOnBase(parseIntSafe(stat.get("leftOnBase")))
                                // 타구 기록
                                .groundOuts(parseIntSafe(stat.get("groundOuts")))
                                .airOuts(parseIntSafe(stat.get("airOuts")))
                                .groundOutsToAirOuts(parseDoubleSafe(stat.get("groundOutsToAirouts")))
                                .numberOfPitches(parseIntSafe(stat.get("numberOfPitches")))
                                .atBatsPerHomeRun(parseDoubleSafe(stat.get("atBatsPerHomeRun")))
                                // 비율 스탯
                                .avg(parseDoubleSafe(stat.get("avg")))
                                .obp(parseDoubleSafe(stat.get("obp")))
                                .slg(parseDoubleSafe(stat.get("slg")))
                                .ops(parseDoubleSafe(stat.get("ops")))
                                .babip(parseDoubleSafe(stat.get("babip")))
                                .build();

                        batterStatRepository.save(batterStat);
                        savedCount++;

                    } catch (Exception e) {
                        log.warn("타자 스탯({}) 저장 실패: {}", gameType, e.getMessage());
                    }
                }

                offset += limit;
                if (offset >= totalSplits) break;

                Thread.sleep(300);

            } catch (Exception e) {
                log.warn("{}시즌 타자 스탯 조회 실패 (offset {}): {}", season, offset, e.getMessage());
                break;
            }
        }

        log.info("{}시즌 타자 스탯({}) 수집 완료: {}건", season, gameType, savedCount);
    }

    // ================================================
    // 투수 시즌 스탯 수집 
    // ================================================
    @SuppressWarnings("unchecked")
    private void initPitcherStats(int season, String gameType) {
        long count = pitcherStatRepository.countBySeasonAndGameType(season, gameType);
        if (count > 0) {
            log.info("{}시즌 투수 스탯({}) 이미 존재, 스킵", season, gameType);
            return;
        }

        log.info("{}시즌 투수 스탯({}) 수집 시작...", season, gameType);

        String statsParam = "season&group=pitching&season=" + season + "&gameType=" + gameType + "&playerPool=All";

        int offset = 0;
        int limit = 500;
        int savedCount = 0;

        while (true) {
            try {
                Map<String, Object> response = fetchJson(
                        "/stats?stats=" + statsParam + "&limit=" + limit + "&offset=" + offset);

                if (response == null) break;

                List<Map<String, Object>> statsList = (List<Map<String, Object>>) response.get("stats");
                if (statsList == null || statsList.isEmpty()) break;

                Map<String, Object> statsObj = statsList.get(0);
                List<Map<String, Object>> splits = (List<Map<String, Object>>) statsObj.get("splits");
                if (splits == null || splits.isEmpty()) break;

                int totalSplits = parseIntSafe(statsObj.get("totalSplits"));

                for (Map<String, Object> split : splits) {
                    try {
                        Map<String, Object> playerMap = (Map<String, Object>) split.get("player");
                        Map<String, Object> teamMap = (Map<String, Object>) split.get("team");
                        if (playerMap == null || teamMap == null) continue;

                        Long playerId = Long.valueOf(playerMap.get("id").toString());
                        Long teamId = Long.valueOf(teamMap.get("id").toString());

                        Player player = playerRepository.findById(playerId).orElse(null);
                        Team team = teamRepository.findById(teamId).orElse(null);
                        if (player == null || team == null) continue;

                        if (pitcherStatRepository.existsByPlayerIdAndSeasonAndTeamIdAndGameType(playerId, season, teamId, gameType)) continue;

                        Map<String, Object> stat = (Map<String, Object>) split.get("stat");
                        if (stat == null) continue;

                        PitcherStat pitcherStat = PitcherStat.builder()
                                .player(player)
                                .team(team)
                                .season(season)
                                .gameType(gameType)
                                // 출전 기록
                                .gamesPlayed(parseIntSafe(stat.get("gamesPlayed")))
                                .gamesStarted(parseIntSafe(stat.get("gamesStarted")))
                                .gamesPitched(parseIntSafe(stat.get("gamesPitched")))
                                .gamesFinished(parseIntSafe(stat.get("gamesFinished")))
                                .completeGames(parseIntSafe(stat.get("completeGames")))
                                .shutouts(parseIntSafe(stat.get("shutouts")))
                                // 승패 기록
                                .wins(parseIntSafe(stat.get("wins")))
                                .losses(parseIntSafe(stat.get("losses")))
                                .saves(parseIntSafe(stat.get("saves")))
                                .saveOpportunities(parseIntSafe(stat.get("saveOpportunities")))
                                .blownSaves(parseIntSafe(stat.get("blownSaves")))
                                .holds(parseIntSafe(stat.get("holds")))
                                .winPercentage(parseDoubleSafe(stat.get("winPercentage")))
                                // 이닝 / 아웃 기록
                                .inningsPitched(parseDoubleSafe(stat.get("inningsPitched")))
                                .outs(parseIntSafe(stat.get("outs")))
                                .battersFaced(parseIntSafe(stat.get("battersFaced")))
                                // 피타격 기록
                                .hitsAllowed(parseIntSafe(stat.get("hits")))
                                .homeRunsAllowed(parseIntSafe(stat.get("homeRuns")))
                                .doubles(parseIntSafe(stat.get("doubles")))
                                .triples(parseIntSafe(stat.get("triples")))
                                .totalBasesAllowed(parseIntSafe(stat.get("totalBases")))
                                .groundOuts(parseIntSafe(stat.get("groundOuts")))
                                .airOuts(parseIntSafe(stat.get("airOuts")))
                                .groundOutsToAirOuts(parseDoubleSafe(stat.get("groundOutsToAirouts")))
                                // 출루 허용 기록
                                .walks(parseIntSafe(stat.get("baseOnBalls")))
                                .intentionalWalks(parseIntSafe(stat.get("intentionalWalks")))
                                .hitBatsmen(parseIntSafe(stat.get("hitBatsmen")))
                                .catchersInterference(parseIntSafe(stat.get("catchersInterference")))
                                // 주루 허용 기록
                                .stolenBasesAllowed(parseIntSafe(stat.get("stolenBases")))
                                .caughtStealing(parseIntSafe(stat.get("caughtStealing")))
                                .inheritedRunners(parseIntSafe(stat.get("inheritedRunners")))
                                .inheritedRunnersScored(parseIntSafe(stat.get("inheritedRunnersScored")))
                                // 실점 기록
                                .runs(parseIntSafe(stat.get("runs")))
                                .earnedRuns(parseIntSafe(stat.get("earnedRuns")))
                                // 삼진 기록
                                .strikeOuts(parseIntSafe(stat.get("strikeOuts")))
                                // 투구 기록
                                .strikes(parseIntSafe(stat.get("strikes")))
                                .strikePercentage(parseDoubleSafe(stat.get("strikePercentage")))
                                .numberOfPitches(parseIntSafe(stat.get("numberOfPitches")))
                                .pitchesPerInning(parseDoubleSafe(stat.get("pitchesPerInning")))
                                .balks(parseIntSafe(stat.get("balks")))
                                .wildPitches(parseIntSafe(stat.get("wildPitches")))
                                .pickoffs(parseIntSafe(stat.get("pickoffs")))
                                // 희생 허용
                                .sacBunts(parseIntSafe(stat.get("sacBunts")))
                                .sacFlies(parseIntSafe(stat.get("sacFlies")))
                                .groundIntoDoublePlay(parseIntSafe(stat.get("groundIntoDoublePlay")))
                                // 비율 스탯
                                .era(parseDoubleSafe(stat.get("era")))
                                .whip(parseDoubleSafe(stat.get("whip")))
                                .avgAllowed(parseDoubleSafe(stat.get("avg")))
                                .obpAllowed(parseDoubleSafe(stat.get("obp")))
                                .slgAllowed(parseDoubleSafe(stat.get("slg")))
                                .opsAllowed(parseDoubleSafe(stat.get("ops")))
                                .babip(parseDoubleSafe(stat.get("babip")))
                                // 9이닝 환산 스탯
                                .strikeOutPer9(parseDoubleSafe(stat.get("strikeoutsPer9Inn")))
                                .walkPer9(parseDoubleSafe(stat.get("walksPer9Inn")))
                                .hitsPer9(parseDoubleSafe(stat.get("hitsPer9Inn")))
                                .homeRunsPer9(parseDoubleSafe(stat.get("homeRunsPer9")))
                                .runsScoredPer9(parseDoubleSafe(stat.get("runsScoredPer9")))
                                // 기타 비율
                                .strikeoutWalkRatio(parseDoubleSafe(stat.get("strikeoutWalkRatio")))
                                .build();

                        pitcherStatRepository.save(pitcherStat);
                        savedCount++;

                    } catch (Exception e) {
                        log.warn("투수 스탯 저장 실패: {}", e.getMessage());
                    }
                }

                offset += limit;
                if (offset >= totalSplits) break;

                Thread.sleep(300);

            } catch (Exception e) {
                log.warn("{}시즌 투수 스탯 조회 실패 (offset {}): {}", season, offset, e.getMessage());
                break;
            }
        }

        log.info("{}시즌 투수 스탯({}) 수집 완료: {}건", season, gameType, savedCount);

    }

    // ================================================
    // PitchData 수집
    // ================================================
    @SuppressWarnings("unchecked")
    private void initPitchData(int season) {

        log.info("{} 시즌 PitchData 수집 시작...", season);

        List<Game> games = gameRepository.findBySeasonAndStatusIn(season, List.of("Final", "Completed Early"));

        for (Game game : games) {

            long pitchDataCount = pitchDataRepository.countByGameId(game.getId());
            if (pitchDataCount > 0) {
                log.info("게임 {}에 이미 PitchData가 존재합니다. 건너뜁니다.", game.getId());
                continue;
            }

            try {

                Map<String, Object> response = fetchJson("/game/" + game.getId() + "/playByPlay");

                if (response == null) continue;

                List<Map<String, Object>> allPlays =
                        (List<Map<String, Object>>) response.get("allPlays");

                if (allPlays == null) continue;

                for (Map<String, Object> play : allPlays) {

                    try {

                        // 타석 최종 결과
                        Map<String, Object> playResult =
                                (Map<String, Object>) play.get("result");

                        String eventResult = null;

                        if (playResult != null) {
                            eventResult = playResult.get("event") != null
                                    ? playResult.get("event").toString()
                                    : null;
                        }

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

                                // 회전수 (rpm) — pitchData.breaks.spinRate
                                Integer spinRate = null;
                                Map<String, Object> breaksMap =
                                        (Map<String, Object>) pitchDataMap.get("breaks");
                                if (breaksMap != null) {
                                    int sr = parseIntSafe(breaksMap.get("spinRate"));
                                    if (sr > 0) spinRate = sr;
                                }

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

                                // inning + scoringPlay — play.about
                                Integer inning = null;
                                Boolean scoringPlay = false;

                                if (play.get("about") != null) {

                                    Map<String, Object> aboutMap =
                                            (Map<String, Object>) play.get("about");

                                    inning =
                                            parseIntSafe(
                                                    aboutMap.get("inning")
                                            );

                                    Object isScoringObj = aboutMap.get("isScoringPlay");
                                    if (isScoringObj instanceof Boolean) {
                                        scoringPlay = (Boolean) isScoringObj;
                                    }
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
                                        .spinRate(spinRate)
                                        .plateX(plateX)
                                        .plateZ(plateZ)
                                        .result(result)
                                        .event(eventResult)
                                        .inning(inning)
                                        .balls(balls)
                                        .strikes(strikes)
                                        .outs(outs)
                                        .exitVelocity(launchSpeed)
                                        .launchAngle(launchAngle)
                                        .distance(totalDistance)
                                        .scoringPlay(scoringPlay)
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

    // ======================================================
    // LineScore 데이터 수집
    // ======================================================
    @SuppressWarnings("unchecked")
    private void initLineScores(Integer season) {

        log.info("{} 시즌 LineScore 수집 시작...", season);

        List<Game> games = gameRepository.findBySeasonAndStatusIn(season, List.of("Final", "Completed Early"));
        log.info("{} 시즌 Game 데이터: {}개", season, games.size());

        if (games.isEmpty()) {
            log.warn("{} season Game Data X initGames Check.", season);
            return;
        }

        int saved = 0;

        for (Game game : games) {

            List<LineScore> existingLineScores = lineScoreRepository.findByGameId(game.getId());
            log.info("게임 {} LineScore 기존 개수: {}", game.getId(), existingLineScores.size());
            if (!existingLineScores.isEmpty()) {
                log.info("게임 {}에 이미 LineScore가 존재합니다. 건너뜁니다.", game.getId());
                continue;
            }

            try {

                Map<String, Object> response = fetchJson("/game/" + game.getId() + "/linescore");

                if (response == null) {
                    log.warn("게임 {} LineScore 응답이 null입니다.", game.getId());
                    continue;
                }

                List<Map<String, Object>> innings =
                        (List<Map<String, Object>>) response.get("innings");

                if (innings == null) {
                    log.warn("게임 {} LineScore에 innings가 없습니다. 응답: {}", game.getId(), response);
                    continue;
                }

                for (Map<String, Object> inning : innings) {

                    try {

                        Integer inningNum = parseIntSafe(inning.get("num"));

                        // 홈팀
                        Map<String, Object> home =
                                (Map<String, Object>) inning.get("home");

                        if (home != null) {

                            LineScore homeScore = LineScore.builder()
                                    .game(game)
                                    .inning(inningNum)
                                    .isHome(true)
                                    .runs(parseIntSafe(home.get("runs")))
                                    .hits(parseIntSafe(home.get("hits")))
                                    .errors(parseIntSafe(home.get("errors")))
                                    .build();

                            lineScoreRepository.save(homeScore);

                            saved++;
                        }

                        // 원정팀
                        Map<String, Object> away =
                                (Map<String, Object>) inning.get("away");

                        if (away != null) {

                            LineScore awayScore = LineScore.builder()
                                    .game(game)
                                    .inning(inningNum)
                                    .isHome(false)
                                    .runs(parseIntSafe(away.get("runs")))
                                    .hits(parseIntSafe(away.get("hits")))
                                    .errors(parseIntSafe(away.get("errors")))
                                    .build();

                            lineScoreRepository.save(awayScore);

                            saved++;
                        }

                    } catch (Exception e) {
                        log.error("LineScore 이닝 저장 실패", e);
                    }
                }

                Thread.sleep(50);

            } catch (Exception e) {
                log.error("게임 {} LineScore 조회 실패", game.getId(), e);
            }
        }

        log.info("{} 시즌 LineScore 수집 완료: {}건", season, saved);
    }

    
    // ======================================================
    // BoxScore 데이터 수집
    // ======================================================
    @SuppressWarnings("unchecked")
    private void initBoxScores(int season) {

        log.info("{} 시즌 BoxScore 수집 시작...", season);

        List<Game> games = gameRepository.findBySeasonAndStatusIn(season, List.of("Final", "Completed Early"));

        int saved = 0;

        for (Game game : games) {

            try {

                Map<String, Object> response = fetchJson("/game/" + game.getId() + "/boxscore");

                if (response == null) continue;

                Map<String, Object> teams =
                        (Map<String, Object>) response.get("teams");

                if (teams == null) continue;

                for (String side : List.of("home", "away")) {

                    Map<String, Object> teamData =
                            (Map<String, Object>) teams.get(side);

                    if (teamData == null) continue;

                    Map<String, Object> teamInfo =
                            (Map<String, Object>) teamData.get("team");

                    if (teamInfo == null) continue;

                    Long teamId =
                            Long.valueOf(teamInfo.get("id").toString());

                    Team team =
                            teamRepository.findById(teamId).orElse(null);

                    if (team == null) continue;

                    Map<String, Object> players =
                            (Map<String, Object>) teamData.get("players");

                    if (players == null) continue;

                    for (Map.Entry<String, Object> entry : players.entrySet()) {

                        try {

                            Map<String, Object> playerData =
                                    (Map<String, Object>) entry.getValue();

                            Map<String, Object> personMap =
                                    (Map<String, Object>) playerData.get("person");

                            if (personMap == null) continue;

                            Long playerId =
                                    Long.valueOf(personMap.get("id").toString());

                            Player player =
                                    playerRepository.findById(playerId).orElse(null);

                            if (player == null) continue;

                            Map<String, Object> stats =
                                    (Map<String, Object>) playerData.get("stats");

                            if (stats == null) continue;

                            // 타자 기록
                            Map<String, Object> batting =
                                    (Map<String, Object>) stats.get("batting");

                            if (batting != null && !batting.isEmpty()) {

                                Integer battingOrder = null;

                                if (playerData.get("battingOrder") != null) {

                                    String orderStr =
                                            playerData.get("battingOrder").toString();

                                    battingOrder =
                                            parseIntSafe(orderStr.substring(0, 1));
                                }

                                if (boxScoreRepository.existsByGameIdAndPlayerIdAndPlayerType(
                                        game.getId(), player.getId(), "BATTER")) continue;

                                BoxScore boxScore = BoxScore.builder()
                                        .game(game)
                                        .player(player)
                                        .team(team)
                                        .playerType("BATTER")
                                        .atBats(parseIntSafe(batting.get("atBats")))
                                        .hits(parseIntSafe(batting.get("hits")))
                                        .homeRuns(parseIntSafe(batting.get("homeRuns")))
                                        .rbi(parseIntSafe(batting.get("rbi")))
                                        .runs(parseIntSafe(batting.get("runs")))
                                        .walks(parseIntSafe(batting.get("baseOnBalls")))
                                        .strikeOuts(parseIntSafe(batting.get("strikeOuts")))
                                        .battingOrder(battingOrder)
                                        .build();

                                boxScoreRepository.save(boxScore);

                                saved++;
                            }

                            // 투수 기록
                            Map<String, Object> pitching =
                                    (Map<String, Object>) stats.get("pitching");

                            if (pitching != null && !pitching.isEmpty()) {

                                Double ip = null;

                                if (pitching.get("inningsPitched") != null) {
                                    ip = parseDoubleSafe(
                                            pitching.get("inningsPitched"));
                                }

                                if (boxScoreRepository.existsByGameIdAndPlayerIdAndPlayerType(
                                        game.getId(), player.getId(), "PITCHER")) continue;

                                BoxScore boxScore = BoxScore.builder()
                                        .game(game)
                                        .player(player)
                                        .team(team)
                                        .playerType("PITCHER")
                                        .inningsPitched(ip)
                                        .earnedRuns(parseIntSafe(pitching.get("earnedRuns")))
                                        .hitsAllowed(parseIntSafe(pitching.get("hits")))
                                        .walksAllowed(parseIntSafe(pitching.get("baseOnBalls")))
                                        .strikeOutsPitched(parseIntSafe(pitching.get("strikeOuts")))
                                        .pitchCount(parseIntSafe(pitching.get("pitchesThrown")))
                                        .isWin(false)
                                        .isLoss(false)
                                        .isSave(false)
                                        .build();

                                boxScoreRepository.save(boxScore);

                                saved++;
                            }

                        } catch (Exception e) {
                            log.error("BoxScore 선수 저장 실패", e);
                        }
                    }
                }

                Thread.sleep(50);

            } catch (Exception e) {
                log.error("게임 {} BoxScore 조회 실패", game.getId(), e);
            }
        }

        log.info("{} 시즌 BoxScore 수집 완료: {}건", season, saved);
    }

    // ======================================================
    // HotColdZone 데이터 수집 (13존: 내부 9존 [3][3] + 외부 4존)
    //
    // Baseball Savant zone ID 체계:
    //   내부 9존 — zone 1~9  → innerZones[row][col]
    //     1→[0][0] 2→[0][1] 3→[0][2]
    //     4→[1][0] 5→[1][1] 6→[1][2]
    //     7→[2][0] 8→[2][1] 9→[2][2]
    //   외부 4존 — zone 11=top 12=bottom 13=left 14=right
    // ======================================================
    @SuppressWarnings("unchecked")
    private void initHotColdZones(int season, String gameType) {

        log.info("{} 시즌 HotColdZone({}) 수집 시작...", season, gameType);

        List<Player> players = playerRepository.findAll();

        int saved = 0;

        for (Player player : players) {

            try {

                if (hotColdZoneRepository.existsByPlayerIdAndSeasonAndGameType(
                        player.getId(), season, gameType)) {
                    continue;
                }

                Map<String, Object> response = fetchJson(
                        "/people/" + player.getId()
                                + "/stats?stats=hotColdZones&season=" + season
                                + "&gameType=" + gameType);

                if (response == null) continue;

                List<Map<String, Object>> statsList =
                        (List<Map<String, Object>>) response.get("stats");

                if (statsList == null || statsList.isEmpty()) continue;

                for (Map<String, Object> statGroup : statsList) {

                    Map<String, Object> typeMap =
                            (Map<String, Object>) statGroup.get("type");

                    if (typeMap == null) continue;

                    String typeName =
                            typeMap.getOrDefault("displayName", "").toString();

                    // API가 "hotColdZones" 단일 타입으로 내려줌 (타자/투수 구분 없음)
                    if (!typeName.equals("hotColdZones")) continue;

                    List<Map<String, Object>> splits =
                            (List<Map<String, Object>>) statGroup.get("splits");

                    if (splits == null || splits.isEmpty()) continue;

                    // innerZones[3][3] 초기화 (값, temp 각각)
                    Double[][] innerZones   = new Double[3][3];
                    String[][] innerTemps   = new String[3][3];

                    // outerZones 초기화
                    Double outerTop = null, outerBottom = null,
                           outerLeft = null, outerRight = null;
                    String outerTopTemp = null, outerBottomTemp = null,
                           outerLeftTemp = null, outerRightTemp = null;

                    // API 구조:
                    //   splits[] = stat 종류별 배열 (battingAverage, OPS 등)
                    //   각 split.stat.name 으로 종류 구분
                    //   split.stat.zones[].zone = "01"~"14" 문자열
                    //   split.stat.zones[].value = ".286" 또는 "-" (데이터 없음)
                    //   split.stat.zones[].temp  = "hot/warm/lukewarm/cool/cold"
                    // → battingAverage 기준으로 저장
                    for (Map<String, Object> split : splits) {

                        Map<String, Object> stat =
                                (Map<String, Object>) split.get("stat");

                        if (stat == null) continue;

                        // battingAverage 만 사용
                        String statName = stat.getOrDefault("name", "").toString();
                        if (!statName.equals("battingAverage")) continue;

                        List<Map<String, Object>> zones =
                                (List<Map<String, Object>>) stat.get("zones");

                        if (zones == null) continue;

                        for (Map<String, Object> zoneEntry : zones) {

                            Object zoneObj = zoneEntry.get("zone");
                            if (zoneObj == null) continue;

                            int zoneId;
                            try {
                                zoneId = Integer.parseInt(zoneObj.toString().trim());
                            } catch (NumberFormatException e) {
                                continue;
                            }

                            // "-" 는 데이터 없음 → null
                            Object rawValue = zoneEntry.get("value");
                            Double value = null;
                            if (rawValue != null && !rawValue.toString().equals("-")) {
                                value = parseDoubleSafe(rawValue);
                            }

                            String temp = zoneEntry.get("temp") != null
                                    ? zoneEntry.get("temp").toString() : null;

                            // 내부 9존 (01~09) → innerZones[row][col]
                            if (zoneId >= 1 && zoneId <= 9) {
                                int idx = zoneId - 1;
                                int row = idx / 3;
                                int col = idx % 3;
                                innerZones[row][col] = value;
                                innerTemps[row][col] = temp;

                            // 외부 4존 (11~14)
                            } else if (zoneId == 11) {
                                outerTop     = value; outerTopTemp    = temp;
                            } else if (zoneId == 12) {
                                outerBottom  = value; outerBottomTemp = temp;
                            } else if (zoneId == 13) {
                                outerLeft    = value; outerLeftTemp   = temp;
                            } else if (zoneId == 14) {
                                outerRight   = value; outerRightTemp  = temp;
                            }
                        }

                        break; // battingAverage 찾으면 종료
                    }

                    HotColdZone.OuterZones outerZones = HotColdZone.OuterZones.builder()
                            .top(outerTop)           .topTemp(outerTopTemp)
                            .bottom(outerBottom)     .bottomTemp(outerBottomTemp)
                            .left(outerLeft)         .leftTemp(outerLeftTemp)
                            .right(outerRight)       .rightTemp(outerRightTemp)
                            .build();

                    HotColdZone zone = HotColdZone.builder()
                            .player(player)
                            .season(season)
                            .gameType(gameType)
                            .innerZones(innerZones)
                            .innerTemps(innerTemps)
                            .outerZones(outerZones)
                            .build();

                    hotColdZoneRepository.save(zone);

                    saved++;

                    break;  // 타자 타입 1개만 저장
                }

                Thread.sleep(30);

            } catch (Exception e) {
                log.error("선수 {} HotColdZone 조회 실패", player.getId(), e);
            }
        }

        log.info("{} 시즌 HotColdZone({}) 수집 완료: {}건", season, gameType, saved);
    }

    // ======================================================
    // SprayData 데이터 수집
    // ======================================================
    @SuppressWarnings("unchecked")
    private void initSprayData(int season) {

        log.info("{} 시즌 SprayData 수집 시작...", season);

        List<Game> games = gameRepository.findBySeasonAndStatusIn(season, List.of("Final", "Completed Early"));

        int saved = 0;

        for (Game game : games) {

            long sprayDataCount = sprayDataRepository.countByGameId(game.getId());
            if (sprayDataCount > 0) {
                log.info("게임 {}에 이미 SprayData가 존재합니다. 건너뜁니다.", game.getId());
                continue;
            }

            try {

                Map<String, Object> response = fetchJson("/game/" + game.getId() + "/playByPlay");

                if (response == null) continue;

                List<Map<String, Object>> allPlays =
                        (List<Map<String, Object>>) response.get("allPlays");

                if (allPlays == null) continue;

                for (Map<String, Object> play : allPlays) {

                    try {

                        Map<String, Object> result =
                                (Map<String, Object>) play.get("result");

                        if (result == null) continue;

                        String event =
                                result.getOrDefault("event", "").toString();

                        if (!List.of(
                                "Single",
                                "Double",
                                "Triple",
                                "Home Run"
                        ).contains(event)) {
                            continue;
                        }

                        Map<String, Object> matchup =
                                (Map<String, Object>) play.get("matchup");

                        if (matchup == null) continue;

                        Map<String, Object> batterMap =
                                (Map<String, Object>) matchup.get("batter");

                        if (batterMap == null) continue;

                        Long batterId =
                                Long.valueOf(batterMap.get("id").toString());

                        Player batter =
                                playerRepository.findById(batterId).orElse(null);

                        if (batter == null) continue;

                        List<Map<String, Object>> playEvents =
                                (List<Map<String, Object>>) play.get("playEvents");

                        if (playEvents == null || playEvents.isEmpty()) continue;

                        Map<String, Object> hitData = null;

                        for (Map<String, Object> pe : playEvents) {

                            if (pe.get("hitData") != null) {

                                hitData = (Map<String, Object>) pe.get("hitData");
                                break;
                            }
                        }

                        if (hitData == null) continue;

                        Double exitVelocity = null;
                        Double launchAngle = null;
                        Double hitDistance = null;
                        Double hitCoordX = null;
                        Double hitCoordY = null;

                        exitVelocity =
                                parseDoubleSafe(hitData.get("launchSpeed"));

                        launchAngle =
                                parseDoubleSafe(hitData.get("launchAngle"));

                        hitDistance =
                                parseDoubleSafe(hitData.get("totalDistance"));

                        Map<String, Object> coordinates =
                                (Map<String, Object>) hitData.get("coordinates");

                        if (coordinates != null) {

                            hitCoordX =
                                    parseDoubleSafe(coordinates.get("coordX"));

                            hitCoordY =
                                    parseDoubleSafe(coordinates.get("coordY"));
                        }

                        String hitLocation = "center";

                        if (hitCoordX != null) {
                            if (hitCoordX < 95) hitLocation = "left";
                            else if (hitCoordX > 155) hitLocation = "right";
                        }

                        SprayData sprayData = SprayData.builder()
                                .game(game)
                                .player(batter)
                                .season(season)
                                .hitCoordX(hitCoordX)
                                .hitCoordY(hitCoordY)
                                .exitVelocity(exitVelocity)
                                .launchAngle(launchAngle)
                                .hitDistance(hitDistance)
                                .events(event.toLowerCase().replace(" ", "_"))
                                .hitLocation(hitLocation)
                                .build();

                        sprayDataRepository.save(sprayData);

                        saved++;

                    } catch (Exception e) {
                        log.error("SprayData play 저장 실패", e);
                    }
                }

                Thread.sleep(50);

            } catch (Exception e) {
                log.error("게임 {} SprayData 조회 실패", game.getId(), e);
            }
        }

        log.info("{} 시즌 SprayData 수집 완료: {}건", season, saved);
    }

    // ================================================
    // BatterSituationStat 데이터 수집 (득점권 타율 등 상황별 타격 스탯)
    //
    // API: /people/{id}/stats?stats=statSplits&season={season}&group=hitting&sitCodes={code}
    // 현재 수집 상황 코드:
    //   risp        — 득점권 (2루 또는 3루에 주자)
    // (추후 bases_loaded, two_outs, late_close 등 sitCodes 배열에 추가)
    // ================================================
    @SuppressWarnings("unchecked")
    private void initBatterSituationStats(int season, String gameType) {

        log.info("{} 시즌 BatterSituationStat 수집 시작...", season);

        // 수집할 상황 코드 목록 — 추후 여기에 추가
        List<String> sitCodes = List.of("risp");

        List<Player> players = playerRepository.findAll();

        int saved = 0;

        for (Player player : players) {
            for (String sitCode : sitCodes) {
                try {

                    if (batterSituationStatRepository.existsByPlayerIdAndSeasonAndSitCodeAndGameType(
                            player.getId(), season, sitCode, gameType)) {
                        continue;
                    }

                    String statsType = "statSplits";
                    Map<String, Object> response = fetchJson(
                            "/people/" + player.getId()
                                    + "/stats?stats=" + statsType + "&season=" + season
                                    + "&group=hitting&sitCodes=" + sitCode + "&gameType=" + gameType);

                    if (response == null) continue;

                    List<Map<String, Object>> statsList =
                            (List<Map<String, Object>>) response.get("stats");

                    if (statsList == null || statsList.isEmpty()) continue;

                    for (Map<String, Object> statGroup : statsList) {

                        List<Map<String, Object>> splits =
                                (List<Map<String, Object>>) statGroup.get("splits");

                        if (splits == null || splits.isEmpty()) continue;

                        // 첫 번째 split이 해당 시즌 전체 집계
                        Map<String, Object> split = splits.get(0);

                        Map<String, Object> stat =
                                (Map<String, Object>) split.get("stat");

                        if (stat == null) continue;

                        // split.code, split.description
                        Map<String, Object> splitInfo =
                                (Map<String, Object>) split.get("split");

                        String code = splitInfo != null
                                ? splitInfo.getOrDefault("code", sitCode).toString()
                                : sitCode;

                        String description = splitInfo != null
                                ? splitInfo.getOrDefault("description", "").toString()
                                : "";

                        BatterSituationStat situationStat = BatterSituationStat.builder()
                                .player(player)
                                .season(season)
                                .gameType(gameType)
                                .sitCode(code)
                                .sitDescription(description)
                                // 비율 스탯 — "-" 또는 null이면 null 저장
                                .avg(parseStatDouble(stat.get("avg")))
                                .obp(parseStatDouble(stat.get("obp")))
                                .slg(parseStatDouble(stat.get("slg")))
                                .ops(parseStatDouble(stat.get("ops")))
                                .babip(parseStatDouble(stat.get("babip")))
                                // 기본 스탯
                                .plateAppearances(parseIntSafe(stat.get("plateAppearances")))
                                .atBats(parseIntSafe(stat.get("atBats")))
                                .hits(parseIntSafe(stat.get("hits")))
                                .doubles(parseIntSafe(stat.get("doubles")))
                                .triples(parseIntSafe(stat.get("triples")))
                                .homeRuns(parseIntSafe(stat.get("homeRuns")))
                                .rbi(parseIntSafe(stat.get("rbi")))
                                // 볼넷 / 삼진
                                .strikeOuts(parseIntSafe(stat.get("strikeOuts")))
                                .baseOnBalls(parseIntSafe(stat.get("baseOnBalls")))
                                .intentionalWalks(parseIntSafe(stat.get("intentionalWalks")))
                                // 기타
                                .groundIntoDoublePlay(parseIntSafe(stat.get("groundIntoDoublePlay")))
                                .totalBases(parseIntSafe(stat.get("totalBases")))
                                .leftOnBase(parseIntSafe(stat.get("leftOnBase")))
                                .gamesPlayed(parseIntSafe(stat.get("gamesPlayed")))
                                .build();

                        batterSituationStatRepository.save(situationStat);
                        saved++;
                    }

                    Thread.sleep(30);

                } catch (Exception e) {
                    log.error("선수 {} sitCode={} BatterSituationStat 조회 실패",
                            player.getId(), sitCode, e);
                }
            }
        }

        log.info("{} 시즌 BatterSituationStat({}) 수집 완료: {}건", season, gameType, saved);
    }

    // ================================================
    // 좌투/우투 분할 스탯 — batter_situation_stat 통합 (sitCode: vl / vr)
    // API: /people/{id}/stats?stats=statSplits&season={s}&group=hitting&sitCodes=vl,vr
    // ================================================
    @SuppressWarnings("unchecked")
    private void initBatterSplitStats(int season, String gameType) {

        log.info("{} 시즌 BatterSplitStat(vl/vr) 수집 시작...", season);

        List<Player> players = playerRepository.findAll();
        int saved = 0;

        for (Player player : players) {
            try {
                // 이미 둘 다 있으면 스킵
                boolean hasVl = batterSituationStatRepository
                        .existsByPlayerIdAndSeasonAndSitCodeAndGameType(player.getId(), season, "vl", gameType);
                boolean hasVr = batterSituationStatRepository
                        .existsByPlayerIdAndSeasonAndSitCodeAndGameType(player.getId(), season, "vr", gameType);
                if (hasVl && hasVr) continue;

                String statsType = "statSplits";
                Map<String, Object> response = fetchJson(
                        "/people/" + player.getId()
                                + "/stats?stats=" + statsType + "&season=" + season
                                + "&group=hitting&sitCodes=vl,vr&gameType=" + gameType);

                if (response == null) continue;

                List<Map<String, Object>> statsList =
                        (List<Map<String, Object>>) response.get("stats");
                if (statsList == null || statsList.isEmpty()) continue;

                for (Map<String, Object> statGroup : statsList) {
                    List<Map<String, Object>> splits =
                            (List<Map<String, Object>>) statGroup.get("splits");
                    if (splits == null) continue;

                    for (Map<String, Object> split : splits) {
                        Map<String, Object> splitInfo =
                                (Map<String, Object>) split.get("split");
                        if (splitInfo == null) continue;

                        String code = splitInfo.getOrDefault("code", "").toString();
                        String description = splitInfo.getOrDefault("description", "").toString();

                        if ((!code.equals("vl") && !code.equals("vr"))
                                || batterSituationStatRepository
                                        .existsByPlayerIdAndSeasonAndSitCodeAndGameType(
                                                player.getId(), season, code, gameType)) {
                            continue;
                        }

                        Map<String, Object> stat = (Map<String, Object>) split.get("stat");
                        if (stat == null) continue;

                        BatterSituationStat situationStat = BatterSituationStat.builder()
                                .player(player).season(season)
                                .gameType(gameType)
                                .sitCode(code).sitDescription(description)
                                .avg(parseStatDouble(stat.get("avg")))
                                .obp(parseStatDouble(stat.get("obp")))
                                .slg(parseStatDouble(stat.get("slg")))
                                .ops(parseStatDouble(stat.get("ops")))
                                .babip(parseStatDouble(stat.get("babip")))
                                .plateAppearances(parseIntSafe(stat.get("plateAppearances")))
                                .atBats(parseIntSafe(stat.get("atBats")))
                                .hits(parseIntSafe(stat.get("hits")))
                                .doubles(parseIntSafe(stat.get("doubles")))
                                .triples(parseIntSafe(stat.get("triples")))
                                .homeRuns(parseIntSafe(stat.get("homeRuns")))
                                .rbi(parseIntSafe(stat.get("rbi")))
                                .strikeOuts(parseIntSafe(stat.get("strikeOuts")))
                                .baseOnBalls(parseIntSafe(stat.get("baseOnBalls")))
                                .intentionalWalks(parseIntSafe(stat.get("intentionalWalks")))
                                .groundIntoDoublePlay(parseIntSafe(stat.get("groundIntoDoublePlay")))
                                .totalBases(parseIntSafe(stat.get("totalBases")))
                                .leftOnBase(parseIntSafe(stat.get("leftOnBase")))
                                .gamesPlayed(parseIntSafe(stat.get("gamesPlayed")))
                                .build();

                        batterSituationStatRepository.save(situationStat);
                        saved++;
                    }
                }

                Thread.sleep(30);

            } catch (Exception e) {
                log.error("선수 {} BatterSplitStat 조회 실패", player.getId(), e);
            }
        }

        log.info("{} 시즌 BatterSplitStat({}) 수집 완료: {}건", season, gameType, saved);
    }

    // ================================================
    // 월별 타격 스탯
    // API: /people/{id}/stats?stats=byMonth&season={s}&group=hitting
    // ================================================
    @SuppressWarnings("unchecked")
    private void initPlayerMonthlyStats(int season) {

        log.info("{} 시즌 PlayerMonthlyStat 수집 시작...", season);

        List<Player> players = playerRepository.findAll();
        int saved = 0;

        for (Player player : players) {
            try {
                // 이미 해당 시즌 데이터가 1개라도 있으면 스킵
                if (playerMonthlyStatRepository
                        .findByPlayerIdAndSeasonOrderByMonth(player.getId(), season)
                        .size() > 0) continue;

                Map<String, Object> response = fetchJson(
                        "/people/" + player.getId()
                                + "/stats?stats=byMonth&season=" + season + "&group=hitting");

                if (response == null) continue;

                List<Map<String, Object>> statsList =
                        (List<Map<String, Object>>) response.get("stats");
                if (statsList == null || statsList.isEmpty()) continue;

                for (Map<String, Object> statGroup : statsList) {
                    List<Map<String, Object>> splits =
                            (List<Map<String, Object>>) statGroup.get("splits");
                    if (splits == null) continue;

                    for (Map<String, Object> split : splits) {
                        Object monthObj = split.get("month");
                        if (monthObj == null) continue;

                        Integer month = parseIntSafe(monthObj);
                        if (playerMonthlyStatRepository
                                .existsByPlayerIdAndSeasonAndMonth(
                                        player.getId(), season, month)) continue;

                        Map<String, Object> stat = (Map<String, Object>) split.get("stat");
                        if (stat == null) continue;

                        PlayerMonthlyStat monthlyStat = PlayerMonthlyStat.builder()
                                .player(player).season(season).month(month)
                                .gamesPlayed(parseIntSafe(stat.get("gamesPlayed")))
                                .plateAppearances(parseIntSafe(stat.get("plateAppearances")))
                                .atBats(parseIntSafe(stat.get("atBats")))
                                .hits(parseIntSafe(stat.get("hits")))
                                .doubles(parseIntSafe(stat.get("doubles")))
                                .triples(parseIntSafe(stat.get("triples")))
                                .homeRuns(parseIntSafe(stat.get("homeRuns")))
                                .runs(parseIntSafe(stat.get("runs")))
                                .rbi(parseIntSafe(stat.get("rbi")))
                                .strikeOuts(parseIntSafe(stat.get("strikeOuts")))
                                .baseOnBalls(parseIntSafe(stat.get("baseOnBalls")))
                                .intentionalWalks(parseIntSafe(stat.get("intentionalWalks")))
                                .stolenBases(parseIntSafe(stat.get("stolenBases")))
                                .caughtStealing(parseIntSafe(stat.get("caughtStealing")))
                                .groundIntoDoublePlay(parseIntSafe(stat.get("groundIntoDoublePlay")))
                                .totalBases(parseIntSafe(stat.get("totalBases")))
                                .leftOnBase(parseIntSafe(stat.get("leftOnBase")))
                                .avg(parseStatDouble(stat.get("avg")))
                                .obp(parseStatDouble(stat.get("obp")))
                                .slg(parseStatDouble(stat.get("slg")))
                                .ops(parseStatDouble(stat.get("ops")))
                                .babip(parseStatDouble(stat.get("babip")))
                                .stolenBasePercentage(parseStatDouble(stat.get("stolenBasePercentage")))
                                .build();

                        playerMonthlyStatRepository.save(monthlyStat);
                        saved++;
                    }
                }

                Thread.sleep(30);

            } catch (Exception e) {
                log.error("선수 {} PlayerMonthlyStat 조회 실패", player.getId(), e);
            }
        }

        log.info("{} 시즌 PlayerMonthlyStat 수집 완료: {}건", season, saved);
    }

    // ================================================
    // 타자 vs 투수 상대전적 — pitch_data 집계
    // MLB API가 vsPlayer 데이터를 미제공하므로 pitch_data 테이블에서 직접 집계
    // 최소 5구 이상 상대한 조합만 저장
    // ================================================
    @SuppressWarnings("unchecked")
    private void initBatterVsPitcher(int season, String gameType) {

        log.info("{} 시즌 BatterVsPitcher 집계 시작...", season);

        long existing = batterVsPitcherRepository
                .countBySeasonAndGameType(season, gameType);
        if (existing > 0) {
            log.info("BatterVsPitcher({}) 이미 데이터 존재 ({}건), 스킵", gameType, existing);
            return;
        }

        List<Object[]> rows = batterVsPitcherRepository.aggregateByGameType(season, gameType);
        int saved = 0;
        for (Object[] row : rows) {
            try {
                Long batterId   = ((Number) row[0]).longValue();
                Long pitcherId  = ((Number) row[1]).longValue();
                Integer total   = ((Number) row[3]).intValue();
                Integer ab      = ((Number) row[4]).intValue();
                Integer h       = ((Number) row[5]).intValue();
                Integer hr      = ((Number) row[6]).intValue();
                Integer so      = ((Number) row[7]).intValue();
                Integer bb      = ((Number) row[8]).intValue();
                Integer scoring = ((Number) row[9]).intValue();

                Double avg = (ab > 0) ? Math.round((double) h / ab * 1000.0) / 1000.0 : null;

                Player batter  = playerRepository.findById(batterId).orElse(null);
                Player pitcher = playerRepository.findById(pitcherId).orElse(null);
                if (batter == null || pitcher == null) continue;

                BatterVsPitcher record = BatterVsPitcher.builder()
                        .batter(batter).pitcher(pitcher).season(season)
                        .gameType(gameType)
                        .totalPitches(total).atBats(ab).hits(h)
                        .homeRuns(hr).strikeOuts(so).baseOnBalls(bb)
                        .avg(avg).pitchesInScoring(scoring)
                        .build();

                batterVsPitcherRepository.save(record);
                saved++;

            } catch (Exception e) {
                log.error("BatterVsPitcher row 처리 오류: {}", e.getMessage());
            }
        }

        log.info("{} 시즌 BatterVsPitcher({}) 집계 완료: {}건", season, gameType, saved);
    }

    // ================================================
    // 유틸
    // ================================================

    /**
     * WebClient GET 공통 헬퍼.
     * 4xx/5xx 포함 모든 에러를 삼키고 null 반환 → 호출부에서 null 체크만 하면 됨.
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> fetchJson(String uri) {
        try {
            return webClient.get()
                    .uri(uri)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .onErrorResume(e -> {
                        log.warn("[API 스킵] {} → {}", uri, e.getMessage());
                        return reactor.core.publisher.Mono.empty();
                    })
                    .block();
        } catch (Exception e) {
            log.warn("[API 스킵] {} → {}", uri, e.getMessage());
            return null;
        }
    }

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

    /** API stat 문자열 파싱 — "-" 또는 빈값이면 null */
    private Double parseStatDouble(Object obj) {
        if (obj == null) return null;
        String s = obj.toString().trim();
        if (s.isEmpty() || s.equals("-")) return null;
        try { return Double.parseDouble(s); }
        catch (Exception e) { return null; }
    }
}