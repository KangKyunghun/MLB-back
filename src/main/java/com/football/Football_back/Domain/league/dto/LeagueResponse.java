package com.football.Football_back.Domain.league.dto;

import com.football.Football_back.Domain.league.entity.League;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LeagueResponse {

    private Long id;
    private String name;
    private String country;
    private String emblemUrl;

    public static LeagueResponse from(League league) {
        return LeagueResponse.builder()
                .id(league.getId())
                .name(league.getName())
                .country(league.getCountry())
                .emblemUrl(league.getEmblemUrl())
                .build();
    }

}