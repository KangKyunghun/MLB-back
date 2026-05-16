package com.mlb.mlb_back.Domain.stat.dto;

import com.mlb.mlb_back.Domain.stat.entity.BatterStat;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BatterStatResponse {

    private Long id;
    private Long playerId;
    private String playerName;
    private String playerPhoto;
    private Long teamId;
    private String teamName;
    private Integer season;

    // 출전
    private Integer gamesPlayed;
    private Integer plateAppearances;
    private Integer atBats;

    // 타격
    private Integer hits;
    private Integer doubles;
    private Integer triples;
    private Integer homeRuns;
    private Integer totalBases;
    private Integer runs;
    private Integer rbi;

    // 출루
    private Integer walks;
    private Integer intentionalWalks;
    private Integer hitByPitch;

    // 주루
    private Integer stolenBases;
    private Integer caughtStealing;
    private Double stolenBasePercentage;

    // 삼진/병살
    private Integer strikeOuts;
    private Integer groundIntoDoublePlay;

    // 희생
    private Integer sacBunts;
    private Integer sacFlies;
    private Integer leftOnBase;

    // 타구
    private Integer groundOuts;
    private Integer airOuts;
    private Double groundOutsToAirOuts;
    private Integer numberOfPitches;
    private Double atBatsPerHomeRun;

    // 비율
    private Double avg;
    private Double obp;
    private Double slg;
    private Double ops;
    private Double babip;

    public static BatterStatResponse from(BatterStat stat) {
        String photoUrl = stat.getPlayer().getId() != null
                ? "https://img.mlbstatic.com/mlb-photos/image/upload/d_people:generic:headshot:67:current.png/w_213,q_auto:best/v1/people/"
                + stat.getPlayer().getId() + "/headshot/67/current"
                : null;

        return BatterStatResponse.builder()
                .id(stat.getId())
                .playerId(stat.getPlayer().getId())
                .playerName(stat.getPlayer().getFullName())
                .playerPhoto(photoUrl)
                .teamId(stat.getTeam().getId())
                .teamName(stat.getTeam().getName())
                .season(stat.getSeason())
                .gamesPlayed(stat.getGamesPlayed())
                .plateAppearances(stat.getPlateAppearances())
                .atBats(stat.getAtBats())
                .hits(stat.getHits())
                .doubles(stat.getDoubles())
                .triples(stat.getTriples())
                .homeRuns(stat.getHomeRuns())
                .totalBases(stat.getTotalBases())
                .runs(stat.getRuns())
                .rbi(stat.getRbi())
                .walks(stat.getWalks())
                .intentionalWalks(stat.getIntentionalWalks())
                .hitByPitch(stat.getHitByPitch())
                .stolenBases(stat.getStolenBases())
                .caughtStealing(stat.getCaughtStealing())
                .stolenBasePercentage(stat.getStolenBasePercentage())
                .strikeOuts(stat.getStrikeOuts())
                .groundIntoDoublePlay(stat.getGroundIntoDoublePlay())
                .sacBunts(stat.getSacBunts())
                .sacFlies(stat.getSacFlies())
                .leftOnBase(stat.getLeftOnBase())
                .groundOuts(stat.getGroundOuts())
                .airOuts(stat.getAirOuts())
                .groundOutsToAirOuts(stat.getGroundOutsToAirOuts())
                .numberOfPitches(stat.getNumberOfPitches())
                .atBatsPerHomeRun(stat.getAtBatsPerHomeRun())
                .avg(stat.getAvg())
                .obp(stat.getObp())
                .slg(stat.getSlg())
                .ops(stat.getOps())
                .babip(stat.getBabip())
                .build();
    }
}