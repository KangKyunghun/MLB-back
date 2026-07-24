package com.mlb.mlb_back.Domain.player.dto;

import com.mlb.mlb_back.Domain.player.entity.Player;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class PlayerResponse {

    private Long id;
    private Long teamId;
    private String teamName;
    private String teamLogoUrl;
    private String fullName;
    private String firstName;
    private String lastName;
    private String position;
    private String shirtNumber;
    private String batSide;
    private String pitchHand;
    private LocalDate dateOfBirth;
    private String nationality;
    private String photoUrl;
    private Boolean isActive;

    public static PlayerResponse fromEntity(Player player) {
        return PlayerResponse.builder()
                .id(player.getId())
                .teamId(player.getTeam() != null ? player.getTeam().getId() : null)
                .teamName(player.getTeam() != null ? player.getTeam().getName() : null)
                .teamLogoUrl(player.getTeam() != null ? player.getTeam().getLogoUrl() : null)
                .fullName(player.getFullName())
                .firstName(player.getFirstName())
                .lastName(player.getLastName())
                .position(player.getPosition())
                .shirtNumber(player.getShirtNumber())
                .batSide(player.getBatSide())
                .pitchHand(player.getPitchHand())
                .dateOfBirth(player.getDateOfBirth())
                .nationality(player.getNationality())
                .photoUrl(player.getPhotoUrl())
                .isActive(player.getIsActive())
                .build();
    }
}