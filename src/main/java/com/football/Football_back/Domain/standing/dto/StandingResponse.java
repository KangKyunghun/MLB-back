package com.football.Football_back.Domain.standing.dto;

import com.football.Football_back.Domain.standing.entity.Standing;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StandingResponse {

    private Long teamId;
    private String teamName;
    private String teamEmblemUrl;
    private Integer rank;
    private Integer played;
    private Integer won;
    private Integer drawn;
    private Integer lost;
    private Integer goalsFor;
    private Integer goalsAgainst;
    private Integer points;
    private String form;

    public static StandingResponse from(Standing standing) {
        return StandingResponse.builder()
                .teamId(standing.getTeam().getId())
                .teamName(standing.getTeam().getName())
                .teamEmblemUrl(standing.getTeam().getEmblemUrl())
                .rank(standing.getRank())
                .played(standing.getPlayed())
                .won(standing.getWon())
                .drawn(standing.getDrawn())
                .lost(standing.getLost())
                .goalsFor(standing.getGoalsFor())
                .goalsAgainst(standing.getGoalsAgainst())
                .points(standing.getPoints())
                .form(standing.getForm())
                .build();
    }
    
}
