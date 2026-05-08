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

2026-05-02T17:37:15.345+09:00 ERROR 22784 --- [  restartedMain] c.f.F.Global.config.DataInitializer      : 팀/선수 저장 실패: leagueId=2019, year=2022 - 403 Forbidden from GET https://api.football-data.org/v4/competitions/2019/teams

200 OK from GET https://api.football-data.org/v4/competitions/2021/matches, but response failed with cause: org.springframework.core.io.buffer.DataBufferLimitException: Exceeded limit on max bytes to buffer : 262144

2026-05-02T17:36:26.450+09:00 ERROR 22784 --- [  restartedMain] c.f.F.Global.config.DataInitializer      : 경기 저장 실패: leagueId=2002, year=2024 - 200 OK from GET https://api.football-data.org/v4/competitions/2002/matches, but response failed with cause: org.springframework.core.io.buffer.DataBufferLimitException: Exceeded limit on max bytes to buffer : 262144

2026-05-02T17:42:45.106+09:00 ERROR 22784 --- [  restartedMain] c.f.F.Global.config.DataInitializer      : 경기 저장 실패: leagueId=2001, year=2025 - The given id must not be null