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
     * 내부 9구역 타율 [3][3] — battingAverage
     *
     *  [0][0~2] = zone 01~03 (상단)
     *  [1][0~2] = zone 04~06 (중단)
     *  [2][0~2] = zone 07~09 (하단)
     */
    private Double[][] innerZones;

    /**
     * 내부 9구역 온도 [3][3]
     * hot | warm | lukewarm | cool | cold
     */
    private String[][] innerTemps;

    /** 외부 4구역 타율 + 온도 */
    private HotColdZone.OuterZones outerZones;

    public static HotColdZoneResponse from(HotColdZone zone) {
        return HotColdZoneResponse.builder()
                .id(zone.getId())
                .playerId(zone.getPlayer().getId())
                .playerName(zone.getPlayer().getFullName())
                .season(zone.getSeason())
                .innerZones(zone.getInnerZones())
                .innerTemps(zone.getInnerTemps())
                .outerZones(zone.getOuterZones())
                .build();
    }
}