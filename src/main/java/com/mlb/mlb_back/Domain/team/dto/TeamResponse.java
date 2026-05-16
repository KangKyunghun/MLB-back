package com.mlb.mlb_back.Domain.team.dto;

import com.mlb.mlb_back.Domain.team.entity.Team;
import lombok.Builder;
import lombok.Getter;
 
@Getter
@Builder
public class TeamResponse {

    private Long id;
    private String name;
    private String abbreviation;
    private String teamName;
    private String locationName;
    private String league;
    private String division;
    private String venue;
    private String logoUrl;

    public static TeamResponse fromEntity(Team team) {
        return TeamResponse.builder()
                .id(team.getId())
                .name(team.getName())
                .abbreviation(team.getAbbreviation())
                .teamName(team.getTeamName())
                .locationName(team.getLocationName())
                .league(team.getLeague())
                .division(team.getDivision())
                .venue(team.getVenue())
                .logoUrl(team.getLogoUrl())
                .build();
    }
    
}
