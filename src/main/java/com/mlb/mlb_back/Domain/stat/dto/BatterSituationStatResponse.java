package com.mlb.mlb_back.Domain.stat.dto;

import com.mlb.mlb_back.Domain.stat.entity.BatterSituationStat;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BatterSituationStatResponse {

    private Long id;
    private Long playerId;
    private String playerName;
    private Integer season;
    private String sitCode;
    private String sitDescription;

    // 비율 스탯
    private Double avg;
    private Double obp;
    private Double slg;
    private Double ops;
    private Double babip;

    // 기본 스탯
    private Integer plateAppearances;
    private Integer atBats;
    private Integer hits;
    private Integer doubles;
    private Integer triples;
    private Integer homeRuns;
    private Integer rbi;

    // 볼넷 / 삼진
    private Integer strikeOuts;
    private Integer baseOnBalls;
    private Integer intentionalWalks;

    // 기타
    private Integer groundIntoDoublePlay;
    private Integer totalBases;
    private Integer leftOnBase;
    private Integer gamesPlayed;

    public static BatterSituationStatResponse from(BatterSituationStat stat) {
        return BatterSituationStatResponse.builder()
                .id(stat.getId())
                .playerId(stat.getPlayer().getId())
                .playerName(stat.getPlayer().getFullName())
                .season(stat.getSeason())
                .sitCode(stat.getSitCode())
                .sitDescription(stat.getSitDescription())
                .avg(stat.getAvg())
                .obp(stat.getObp())
                .slg(stat.getSlg())
                .ops(stat.getOps())
                .babip(stat.getBabip())
                .plateAppearances(stat.getPlateAppearances())
                .atBats(stat.getAtBats())
                .hits(stat.getHits())
                .doubles(stat.getDoubles())
                .triples(stat.getTriples())
                .homeRuns(stat.getHomeRuns())
                .rbi(stat.getRbi())
                .strikeOuts(stat.getStrikeOuts())
                .baseOnBalls(stat.getBaseOnBalls())
                .intentionalWalks(stat.getIntentionalWalks())
                .groundIntoDoublePlay(stat.getGroundIntoDoublePlay())
                .totalBases(stat.getTotalBases())
                .leftOnBase(stat.getLeftOnBase())
                .gamesPlayed(stat.getGamesPlayed())
                .build();
    }
}
