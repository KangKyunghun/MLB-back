package com.mlb.mlb_back.Domain.game.dto;

import com.mlb.mlb_back.Domain.game.entity.LineScore;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LineScoreResponse {

    private Long id;
    private Long gameId;
    private Integer inning;
    private Boolean isHome;
    private Integer runs;
    private Integer hits;
    private Integer errors;

    public static LineScoreResponse fromEntity(LineScore lineScore) {
        return LineScoreResponse.builder()
                .id(lineScore.getId())
                .gameId(lineScore.getGame().getId())
                .inning(lineScore.getInning())
                .isHome(lineScore.getIsHome())
                .runs(lineScore.getRuns())
                .hits(lineScore.getHits())
                .errors(lineScore.getErrors())
                .build();
    }
}
