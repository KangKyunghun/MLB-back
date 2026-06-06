package com.mlb.mlb_back.Domain.stat.dto;

import com.mlb.mlb_back.Domain.stat.entity.PlayerMonthlyStat;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PlayerMonthlyStatResponse {

    private Long id;
    private Long playerId;
    private String playerName;
    private Integer season;
    private Integer month;
    private Integer gamesPlayed;
    private Integer plateAppearances;
    private Integer atBats;
    private Integer hits;
    private Integer doubles;
    private Integer triples;
    private Integer homeRuns;
    private Integer runs;
    private Integer rbi;
    private Integer strikeOuts;
    private Integer baseOnBalls;
    private Integer intentionalWalks;
    private Integer stolenBases;
    private Integer caughtStealing;
    private Integer groundIntoDoublePlay;
    private Integer totalBases;
    private Integer leftOnBase;
    private Double avg;
    private Double obp;
    private Double slg;
    private Double ops;
    private Double babip;
    private Double stolenBasePercentage;

    public static PlayerMonthlyStatResponse from(PlayerMonthlyStat stat) {
        return PlayerMonthlyStatResponse.builder()
                .id(stat.getId())
                .playerId(stat.getPlayer().getId())
                .playerName(stat.getPlayer().getFullName())
                .season(stat.getSeason())
                .month(stat.getMonth())
                .gamesPlayed(stat.getGamesPlayed())
                .plateAppearances(stat.getPlateAppearances())
                .atBats(stat.getAtBats())
                .hits(stat.getHits())
                .doubles(stat.getDoubles())
                .triples(stat.getTriples())
                .homeRuns(stat.getHomeRuns())
                .runs(stat.getRuns())
                .rbi(stat.getRbi())
                .strikeOuts(stat.getStrikeOuts())
                .baseOnBalls(stat.getBaseOnBalls())
                .intentionalWalks(stat.getIntentionalWalks())
                .stolenBases(stat.getStolenBases())
                .caughtStealing(stat.getCaughtStealing())
                .groundIntoDoublePlay(stat.getGroundIntoDoublePlay())
                .totalBases(stat.getTotalBases())
                .leftOnBase(stat.getLeftOnBase())
                .avg(stat.getAvg())
                .obp(stat.getObp())
                .slg(stat.getSlg())
                .ops(stat.getOps())
                .babip(stat.getBabip())
                .stolenBasePercentage(stat.getStolenBasePercentage())
                .build();
    }
}
