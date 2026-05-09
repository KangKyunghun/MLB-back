package com.football.Football_back.Domain.stat.dto;

import com.football.Football_back.Domain.stat.entity.PlayerStat;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PlayerStatResponse {

    private Long id;
    private Long matchId;
    private LocalDateTime matchDate;
    private String homeTeamName;
    private String awayTeamName;
    private String homeTeamEmblemUrl;
    private String awayTeamEmblemUrl;
    private Integer homeScore;
    private Integer awayScore;

    private Long playerId;
    private String playerName;
    private String position;

    // 공통
    private Integer minutesPlayed;

    // Summary - 필드 플레이어
    private Integer goals;
    private Integer assists;
    private Integer shots;
    private Integer shotsOnTarget;
    private Integer foulsCommitted;
    private Integer yellowCards;
    private Integer redCards;

    // Defensive
    private Integer tackles;
    private Integer interceptions;
    private Integer aerialsWon;
    private Integer clearances;
    private Integer blocks;

    // Offensive
    private Integer dribblesWon;

    // Passing
    private Integer passes;
    private Double passAccuracy;
    private Integer keyPasses;
    private Integer crosses;
    private Integer longBalls;

    // xG
    private Double xg;
    private Double xa;
    private Double xgot;

    // 골키퍼 전용
    private Integer saves;
    private Double savePercentage;
    private Integer goalsConceded;
    private Double xga;
    private Double psxg;

    public static PlayerStatResponse from(PlayerStat stat) {
        return PlayerStatResponse.builder()
                .id(stat.getId())
                .matchId(stat.getMatch().getId())
                .matchDate(stat.getMatch().getMatchDate())
                .homeTeamName(stat.getMatch().getHomeTeam().getName())
                .awayTeamName(stat.getMatch().getAwayTeam().getName())
                .homeTeamEmblemUrl(stat.getMatch().getHomeTeam().getEmblemUrl())
                .awayTeamEmblemUrl(stat.getMatch().getAwayTeam().getEmblemUrl())
                .homeScore(stat.getMatch().getHomeScore())
                .awayScore(stat.getMatch().getAwayScore())
                .playerId(stat.getPlayer().getId())
                .playerName(stat.getPlayer().getName())
                .position(stat.getPlayer().getPosition())
                .minutesPlayed(stat.getMinutesPlayed())
                .goals(stat.getGoals())
                .assists(stat.getAssists())
                .shots(stat.getShots())
                .shotsOnTarget(stat.getShotsOnTarget())
                .foulsCommitted(stat.getFoulsCommitted())
                .yellowCards(stat.getYellowCards())
                .redCards(stat.getRedCards())
                .tackles(stat.getTackles())
                .interceptions(stat.getInterceptions())
                .aerialsWon(stat.getAerialsWon())
                .clearances(stat.getClearances())
                .blocks(stat.getBlocks())
                .dribblesWon(stat.getDribblesWon())
                .passes(stat.getPasses())
                .passAccuracy(stat.getPassAccuracy())
                .keyPasses(stat.getKeyPasses())
                .crosses(stat.getCrosses())
                .longBalls(stat.getLongBalls())
                .xg(stat.getXg())
                .xa(stat.getXa())
                .xgot(stat.getXgot())
                .saves(stat.getSaves())
                .savePercentage(stat.getSavePercentage())
                .goalsConceded(stat.getGoalsConceded())
                .xga(stat.getXga())
                .psxg(stat.getPsxg())
                .build();
    }
}