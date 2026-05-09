package com.football.Football_back.Domain.player.dto;

import com.football.Football_back.Domain.player.entity.Player;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class PlayerResponse {

    private Long id;
    private String name;
    private String firstName;
    private String lastName;
    private Integer shirtNumber;
    private String position;
    private String nationality;
    private LocalDate dateOfBirth;
    private String espnId;
    private String photoUrl;    // ESPN 사진 URL
    private Long teamId;
    private String teamName;
    private String teamEmblemUrl;

    public static PlayerResponse from(Player player) {
        String photoUrl = player.getEspnId() != null
                ? "https://a.espncdn.com/i/headshots/soccer/players/full/" + player.getEspnId() + ".png"
                : null;

        return PlayerResponse.builder()
                .id(player.getId())
                .name(player.getName())
                .firstName(player.getFirstName())
                .lastName(player.getLastName())
                .shirtNumber(player.getShirtNumber())
                .position(player.getPosition())
                .nationality(player.getNationality())
                .dateOfBirth(player.getDateOfBirth())
                .espnId(player.getEspnId())
                .photoUrl(photoUrl)
                .teamId(player.getTeam() != null ? player.getTeam().getId() : null)
                .teamName(player.getTeam() != null ? player.getTeam().getName() : null)
                .teamEmblemUrl(player.getTeam() != null ? player.getTeam().getEmblemUrl() : null)
                .build();
    }
}