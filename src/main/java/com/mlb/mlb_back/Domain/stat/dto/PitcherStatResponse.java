package com.mlb.mlb_back.Domain.stat.dto;

import com.mlb.mlb_back.Domain.stat.entity.PitcherStat;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PitcherStatResponse {

    private Long id;
    private Long playerId;
    private String playerName;
    private String playerPhoto;
    private Long teamId;
    private String teamName;
    private Integer season;

    // 출전
    private Integer gamesPlayed;
    private Integer gamesStarted;
    private Integer gamesPitched;
    private Integer gamesFinished;
    private Integer completeGames;
    private Integer shutouts;

    // 승패
    private Integer wins;
    private Integer losses;
    private Integer saves;
    private Integer saveOpportunities;
    private Integer blownSaves;
    private Integer holds;
    private Double winPercentage;

    // 이닝
    private Double inningsPitched;
    private Integer outs;
    private Integer battersFaced;

    // 피타격
    private Integer hitsAllowed;
    private Integer homeRunsAllowed;
    private Integer doubles;
    private Integer triples;
    private Integer groundOuts;
    private Integer airOuts;
    private Double groundOutsToAirOuts;

    // 출루 허용
    private Integer walks;
    private Integer intentionalWalks;
    private Integer hitBatsmen;

    // 실점
    private Integer runs;
    private Integer earnedRuns;

    // 삼진
    private Integer strikeOuts;

    // 투구
    private Integer numberOfPitches;
    private Double strikePercentage;
    private Double pitchesPerInning;
    private Integer wildPitches;

    // 희생
    private Integer sacBunts;
    private Integer sacFlies;
    private Integer groundIntoDoublePlay;

    // 비율
    private Double era;
    private Double whip;
    private Double avgAllowed;
    private Double obpAllowed;
    private Double slgAllowed;
    private Double opsAllowed;
    private Double babip;

    // 9이닝 환산
    private Double strikeOutPer9;
    private Double walkPer9;
    private Double hitsPer9;
    private Double homeRunsPer9;
    private Double runsScoredPer9;
    private Double strikeoutWalkRatio;

    public static PitcherStatResponse from(PitcherStat stat) {
        String photoUrl = stat.getPlayer().getId() != null
                ? "https://img.mlbstatic.com/mlb-photos/image/upload/d_people:generic:headshot:67:current.png/w_213,q_auto:best/v1/people/"
                + stat.getPlayer().getId() + "/headshot/67/current"
                : null;

        return PitcherStatResponse.builder()
                .id(stat.getId())
                .playerId(stat.getPlayer().getId())
                .playerName(stat.getPlayer().getFullName())
                .playerPhoto(photoUrl)
                .teamId(stat.getTeam().getId())
                .teamName(stat.getTeam().getName())
                .season(stat.getSeason())
                .gamesPlayed(stat.getGamesPlayed())
                .gamesStarted(stat.getGamesStarted())
                .gamesPitched(stat.getGamesPitched())
                .gamesFinished(stat.getGamesFinished())
                .completeGames(stat.getCompleteGames())
                .shutouts(stat.getShutouts())
                .wins(stat.getWins())
                .losses(stat.getLosses())
                .saves(stat.getSaves())
                .saveOpportunities(stat.getSaveOpportunities())
                .blownSaves(stat.getBlownSaves())
                .holds(stat.getHolds())
                .winPercentage(stat.getWinPercentage())
                .inningsPitched(stat.getInningsPitched())
                .outs(stat.getOuts())
                .battersFaced(stat.getBattersFaced())
                .hitsAllowed(stat.getHitsAllowed())
                .homeRunsAllowed(stat.getHomeRunsAllowed())
                .doubles(stat.getDoubles())
                .triples(stat.getTriples())
                .groundOuts(stat.getGroundOuts())
                .airOuts(stat.getAirOuts())
                .groundOutsToAirOuts(stat.getGroundOutsToAirOuts())
                .walks(stat.getWalks())
                .intentionalWalks(stat.getIntentionalWalks())
                .hitBatsmen(stat.getHitBatsmen())
                .runs(stat.getRuns())
                .earnedRuns(stat.getEarnedRuns())
                .strikeOuts(stat.getStrikeOuts())
                .numberOfPitches(stat.getNumberOfPitches())
                .strikePercentage(stat.getStrikePercentage())
                .pitchesPerInning(stat.getPitchesPerInning())
                .wildPitches(stat.getWildPitches())
                .sacBunts(stat.getSacBunts())
                .sacFlies(stat.getSacFlies())
                .groundIntoDoublePlay(stat.getGroundIntoDoublePlay())
                .era(stat.getEra())
                .whip(stat.getWhip())
                .avgAllowed(stat.getAvgAllowed())
                .obpAllowed(stat.getObpAllowed())
                .slgAllowed(stat.getSlgAllowed())
                .opsAllowed(stat.getOpsAllowed())
                .babip(stat.getBabip())
                .strikeOutPer9(stat.getStrikeOutPer9())
                .walkPer9(stat.getWalkPer9())
                .hitsPer9(stat.getHitsPer9())
                .homeRunsPer9(stat.getHomeRunsPer9())
                .runsScoredPer9(stat.getRunsScoredPer9())
                .strikeoutWalkRatio(stat.getStrikeoutWalkRatio())
                .build();
    }
}