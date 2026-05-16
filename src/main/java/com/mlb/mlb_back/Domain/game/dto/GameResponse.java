package com.mlb.mlb_back.Domain.game.dto;

import com.mlb.mlb_back.Domain.game.entity.Game;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class GameResponse {

    private Long id;
    private Integer season;
    private String status;
    private String gameType;
    private Integer gameNumber;
    private String seriesDescription;
    private LocalDateTime gameDate;
    private String venue;

    // 홈팀
    private Long homeTeamId;
    private String homeTeamName;
    private String homeTeamAbbreviation;
    private String homeTeamLogoUrl;
    private Integer homeScore;

    // 원정팀
    private Long awayTeamId;
    private String awayTeamName;
    private String awayTeamAbbreviation;
    private String awayTeamLogoUrl;
    private Integer awayScore;

    public static GameResponse fromEntity(Game game) {
        return GameResponse.builder()
                .id(game.getId())
                .season(game.getSeason())
                .status(game.getStatus())
                .gameType(game.getGameType())
                .gameNumber(game.getGameNumber())
                .seriesDescription(game.getSeriesDescription())
                .gameDate(game.getGameDate())
                .venue(game.getVenue())
                .homeTeamId(game.getHomeTeam().getId())
                .homeTeamName(game.getHomeTeam().getName())
                .homeTeamAbbreviation(game.getHomeTeam().getAbbreviation())
                .homeTeamLogoUrl(game.getHomeTeam().getLogoUrl())
                .homeScore(game.getHomeScore())
                .awayTeamId(game.getAwayTeam().getId())
                .awayTeamName(game.getAwayTeam().getName())
                .awayTeamAbbreviation(game.getAwayTeam().getAbbreviation())
                .awayTeamLogoUrl(game.getAwayTeam().getLogoUrl())
                .awayScore(game.getAwayScore())
                .build();
    }
}