package com.mlb.mlb_back.Domain.stat.entity;

import com.mlb.mlb_back.Domain.player.entity.Player;
import com.mlb.mlb_back.Global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

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

    // 스트라이크존 9구역 타율 (좌상단부터 우하단 순서)
    // zone1 zone2 zone3
    // zone4 zone5 zone6
    // zone7 zone8 zone9
    @Column(name = "zone1")
    private Double zone1;   // 좌상단

    @Column(name = "zone2")
    private Double zone2;   // 중상단

    @Column(name = "zone3")
    private Double zone3;   // 우상단

    @Column(name = "zone4")
    private Double zone4;   // 좌중단

    @Column(name = "zone5")
    private Double zone5;   // 중앙

    @Column(name = "zone6")
    private Double zone6;   // 우중단

    @Column(name = "zone7")
    private Double zone7;   // 좌하단

    @Column(name = "zone8")
    private Double zone8;   // 중하단

    @Column(name = "zone9")
    private Double zone9;   // 우하단
}
