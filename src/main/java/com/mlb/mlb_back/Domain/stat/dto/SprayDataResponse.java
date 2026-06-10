package com.mlb.mlb_back.Domain.stat.dto;

import com.mlb.mlb_back.Domain.stat.entity.SprayData;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SprayDataResponse {

    private Long id;
    private Long playerId;
    private String playerName;
    private Long gameId;
    private Integer season;
    private Double hitCoordX;
    private Double hitCoordY;
    private Double exitVelocity;
    private Double launchAngle;
    private Double hitDistance;
    private String events;
    private String hitLocation;

    public static SprayDataResponse from(SprayData data) {
        return SprayDataResponse.builder()
                .id(data.getId())
                .playerId(data.getPlayer().getId())
                .playerName(data.getPlayer().getFullName())
                .gameId(data.getGame() != null ? data.getGame().getId() : null)
                .season(data.getSeason())
                .hitCoordX(data.getHitCoordX())
                .hitCoordY(data.getHitCoordY())
                .exitVelocity(data.getExitVelocity())
                .launchAngle(data.getLaunchAngle())
                .hitDistance(data.getHitDistance())
                .events(data.getEvents())
                .hitLocation(data.getHitLocation())
                .build();
    }
}