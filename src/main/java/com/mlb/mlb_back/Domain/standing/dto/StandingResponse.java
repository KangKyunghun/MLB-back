package com.mlb.mlb_back.Domain.standing.dto;

import com.mlb.mlb_back.Domain.standing.entity.Standing;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StandingResponse {

    private Long id;
    private Integer season;

    // 팀 정보
    private Long teamId;
    private String teamName;
    private String teamAbbreviation;
    private String teamLogoUrl;
    private String league;
    private String division;

    // 순위
    private Integer divisionRank;
    private Integer leagueRank;

    // 승패
    private Integer wins;
    private Integer losses;
    private Double winPct;
    private Double gamesBack;

    // 득실
    private Integer runsScored;
    private Integer runsAllowed;
    private Integer runDifferential;

    // 최근 10경기
    private Integer lastTenWins;
    private Integer lastTenLosses;

    // 연승/연패
    private String streak;

    public static StandingResponse fromEntity(Standing standing) {
        return StandingResponse.builder()
                .id(standing.getId())
                .season(standing.getSeason())
                .teamId(standing.getTeam().getId())
                .teamName(standing.getTeam().getName())
                .teamAbbreviation(standing.getTeam().getAbbreviation())
                .teamLogoUrl(standing.getTeam().getLogoUrl())
                .league(standing.getTeam().getLeague())
                .division(standing.getTeam().getDivision())
                .divisionRank(standing.getDivisionRank())
                .leagueRank(standing.getLeagueRank())
                .wins(standing.getWins())
                .losses(standing.getLosses())
                .winPct(standing.getWinPct())
                .gamesBack(standing.getGamesBack())
                .runsScored(standing.getRunsScored())
                .runsAllowed(standing.getRunsAllowed())
                .runDifferential(standing.getRunDifferential())
                .lastTenWins(standing.getLastTenWins())
                .lastTenLosses(standing.getLastTenLosses())
                .streak(standing.getStreak())
                .build();
    }
}