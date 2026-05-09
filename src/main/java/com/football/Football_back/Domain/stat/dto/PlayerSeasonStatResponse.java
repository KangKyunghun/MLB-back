package com.football.Football_back.Domain.stat.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PlayerSeasonStatResponse {

    private Long playerId;
    private String playerName;
    private String position;
    private String seasonYear;

    // 공통
    private Integer appearances;
    private Integer startingAppearances;
    private Integer totalMinutesPlayed;

    // 필드 플레이어 누적
    private Integer totalGoals;
    private Integer totalAssists;
    private Integer totalShots;
    private Integer totalShotsOnTarget;
    private Integer totalYellowCards;
    private Integer totalRedCards;
    private Integer totalTackles;
    private Integer totalInterceptions;
    private Integer totalDribblesWon;
    private Integer totalKeyPasses;
    private Double averagePassAccuracy;
    private Double totalXg;
    private Double totalXa;

    // 골키퍼 누적
    private Integer totalSaves;
    private Double averageSavePercentage;
    private Integer totalGoalsConceded;
    private Double totalXga;
    private Double totalPsxg;
}
