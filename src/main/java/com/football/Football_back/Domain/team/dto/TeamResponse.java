package com.football.Football_back.Domain.team.dto;

import com.football.Football_back.Domain.team.entity.Team;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TeamResponse {

    private Long id;
    private String name;
    private String shortName;
    private String tla;
    private String emblemUrl;
    private Integer founded;
    private String venue;
    private Long leagueId;
    private String leagueName;

    public static TeamResponse from(Team team) {
        return TeamResponse.builder()
                .id(team.getId())
                .name(team.getName())
                .shortName(team.getShortName())
                .tla(team.getTla())
                .emblemUrl(team.getEmblemUrl())
                .founded(team.getFounded())
                .venue(team.getVenue())
                .leagueId(team.getLeague().getId())
                .leagueName(team.getLeague().getName())
                .build();
    }
}