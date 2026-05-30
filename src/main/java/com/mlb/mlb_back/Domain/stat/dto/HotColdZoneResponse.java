package com.mlb.mlb_back.Domain.stat.dto;

import com.mlb.mlb_back.Domain.stat.entity.HotColdZone;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class HotColdZoneResponse {

    private Long id;
    private Long playerId;
    private String playerName;
    private Integer season;

    /**
     * 내부 9구역 타율 [3][3]
     *
     *  [0][0] [0][1] [0][2]   좌상 | 중상 | 우상
     *  [1][0] [1][1] [1][2]   좌중 | 중앙 | 우중
     *  [2][0] [2][1] [2][2]   좌하 | 중하 | 우하
     */
    private Double[][] innerZones;

    /** 외부 4구역 타율 */
    private HotColdZone.OuterZones outerZones;

    public static HotColdZoneResponse from(HotColdZone zone) {
        return HotColdZoneResponse.builder()
                .id(zone.getId())
                .playerId(zone.getPlayer().getId())
                .playerName(zone.getPlayer().getFullName())
                .season(zone.getSeason())
                .innerZones(zone.getInnerZones())
                .outerZones(zone.getOuterZones())
                .build();
    }
}