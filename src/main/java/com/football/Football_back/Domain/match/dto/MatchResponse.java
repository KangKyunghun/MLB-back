package com.football.Football_back.Domain.match.dto;

import com.football.Football_back.Domain.match.entity.Match;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class MatchResponse {

    private Long id;
    private Integer matchday;
    private String stage;
    private LocalDateTime matchDate;
    private String status;

    private Long homeTeamId;
    private String homeTeamName;
    private String homeTeamEmblemUrl;
    private Integer homeScore;

    private Long awayTeamId;
    private String awayTeamName;
    private String awayTeamEmblemUrl;
    private Integer awayScore;

    private Long leagueId;
    private String leagueName;
    private String seasonYear;

    public static MatchResponse from(Match match) {
        return MatchResponse.builder()
                .id(match.getId())
                .matchday(match.getMatchday())
                .stage(match.getStage())
                .matchDate(match.getMatchDate())
                .status(match.getStatus())
                .homeTeamId(match.getHomeTeam().getId())
                .homeTeamName(match.getHomeTeam().getName())
                .homeTeamEmblemUrl(match.getHomeTeam().getEmblemUrl())
                .homeScore(match.getHomeScore())
                .awayTeamId(match.getAwayTeam().getId())
                .awayTeamName(match.getAwayTeam().getName())
                .awayTeamEmblemUrl(match.getAwayTeam().getEmblemUrl())
                .awayScore(match.getAwayScore())
                .leagueId(match.getLeague().getId())
                .leagueName(match.getLeague().getName())
                .seasonYear(match.getSeason().getDisplayName())
                .build();
    }
}