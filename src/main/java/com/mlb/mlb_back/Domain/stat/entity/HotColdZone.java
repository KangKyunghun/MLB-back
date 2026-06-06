package com.mlb.mlb_back.Domain.stat.entity;

import com.mlb.mlb_back.Domain.player.entity.Player;
import com.mlb.mlb_back.Global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "hot_cold_zone", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"player_id", "season"})
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class HotColdZone extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id", nullable = false)
    private Player player;

    @Column(nullable = false)
    private Integer season;

    /**
     * 스트라이크존 내부 9구역 타율 [3][3] (battingAverage 기준)
     *
     *  [0][0] [0][1] [0][2]   좌상 | 중상 | 우상   (zone 01~03)
     *  [1][0] [1][1] [1][2]   좌중 | 중앙 | 우중   (zone 04~06)
     *  [2][0] [2][1] [2][2]   좌하 | 중하 | 우하   (zone 07~09)
     *
     * DB: JSONB [[0.286, 0.167, 0.100], [...], [...]]
     */
    @Column(name = "inner_zones", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private Double[][] innerZones;

    /**
     * 스트라이크존 내부 9구역 온도 [3][3]
     * 값: hot | warm | lukewarm | cool | cold
     *
     * DB: JSONB [["warm","cold","cold"], [...], [...]]
     */
    @Column(name = "inner_temps", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private String[][] innerTemps;

    /**
     * 스트라이크존 바깥 4구역 (zone 11~14)
     *
     *  11 = top, 12 = bottom, 13 = left, 14 = right
     *
     * DB: JSONB {"top":0.000,"topTemp":"cold", ...}
     */
    @Column(name = "outer_zones", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private OuterZones outerZones;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OuterZones {
        private Double top;        private String topTemp;
        private Double bottom;     private String bottomTemp;
        private Double left;       private String leftTemp;
        private Double right;      private String rightTemp;
    }
}