package com.mlb.mlb_back.Domain.stat.dto;

import com.mlb.mlb_back.Domain.stat.entity.BatterVsPitcher;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BatterVsPitcherResponse {

    private Long id;
    private Long batterId;
    private String batterName;
    private Long pitcherId;
    private String pitcherName;
    private Integer season;
    private Integer totalPitches;
    private Integer atBats;
    private Integer hits;
    private Integer homeRuns;
    private Integer strikeOuts;
    private Integer baseOnBalls;
    private Double avg;
    private Integer pitchesInScoring;

    public static BatterVsPitcherResponse from(BatterVsPitcher record) {
        return BatterVsPitcherResponse.builder()
                .id(record.getId())
                .batterId(record.getBatter().getId())
                .batterName(record.getBatter().getFullName())
                .pitcherId(record.getPitcher().getId())
                .pitcherName(record.getPitcher().getFullName())
                .season(record.getSeason())
                .totalPitches(record.getTotalPitches())
                .atBats(record.getAtBats())
                .hits(record.getHits())
                .homeRuns(record.getHomeRuns())
                .strikeOuts(record.getStrikeOuts())
                .baseOnBalls(record.getBaseOnBalls())
                .avg(record.getAvg())
                .pitchesInScoring(record.getPitchesInScoring())
                .build();
    }
}
