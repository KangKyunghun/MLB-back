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
     * 스트라이크존 내부 9구역 타율 — [행][열] (row 0=상단, col 0=좌측)
     *
     *  [0][0] [0][1] [0][2]   좌상 | 중상 | 우상
     *  [1][0] [1][1] [1][2]   좌중 | 중앙 | 우중
     *  [2][0] [2][1] [2][2]   좌하 | 중하 | 우하
     *
     * Baseball Savant zone ID 대응:
     *  zone 1→[0][0], 2→[0][1], 3→[0][2]
     *  zone 4→[1][0], 5→[1][1], 6→[1][2]
     *  zone 7→[2][0], 8→[2][1], 9→[2][2]
     *
     * DB: JSONB 컬럼 [[0.312,0.285,0.271],[0.334,0.356,0.298],[0.245,0.278,0.231]]
     */
    @Column(name = "inner_zones", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private Double[][] innerZones;   // [3][3]

    /**
     * 스트라이크존 바깥 4구역 타율
     *
     * Baseball Savant zone ID 대응:
     *  zone 11 → top, 12 → bottom, 13 → left, 14 → right
     *
     * DB: JSONB 컬럼 {"top":0.198,"bottom":0.187,"left":0.223,"right":0.241}
     */
    @Column(name = "outer_zones", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private OuterZones outerZones;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OuterZones {
        private Double top;
        private Double bottom;
        private Double left;
        private Double right;
    }
}