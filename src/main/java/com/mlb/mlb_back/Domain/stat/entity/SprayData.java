package com.mlb.mlb_back.Domain.stat.entity;

import com.mlb.mlb_back.Domain.player.entity.Player;
import com.mlb.mlb_back.Global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "spray_data")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class SprayData extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id", nullable = false)
    private Player player;      // 타자

    @Column(nullable = false)
    private Integer season;

    @Column(name = "hit_coord_x")
    private Double hitCoordX;   // 타구 X 좌표

    @Column(name = "hit_coord_y")
    private Double hitCoordY;   // 타구 Y 좌표

    @Column(name = "exit_velocity")
    private Double exitVelocity; // 타구 속도 (mph)

    @Column(name = "launch_angle")
    private Double launchAngle; // 발사각 (도)

    @Column(name = "hit_distance")
    private Double hitDistance; // 타구 거리 (ft)

    @Column(name = "events", length = 30)
    private String events;      // 타격 결과 (single, double, home_run, out 등)

    @Column(name = "hit_location", length = 10)
    private String hitLocation; // 방향 (left, center, right)
}
