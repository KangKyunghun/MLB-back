package com.football.Football_back.Domain.match.entity;

import com.football.Football_back.Domain.player.entity.Player;
import com.football.Football_back.Domain.team.entity.Team;
import com.football.Football_back.Global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "lineup", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"match_id", "player_id"})
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder

public class Lineup extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_id", nullable = false)
    private Match match;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id", nullable = false)
    private Player player;

    @Column(name = "shirt_number")
    private Integer shirtNumber;   // 해당 경기 등번호

    @Column(length = 20)
    private String position;       // 해당 경기 포지션

    @Column(name = "pos_x")
    private Double posX;           // 포메이션 X 좌표 (선발만)

    @Column(name = "pos_y")
    private Double posY;           // 포메이션 Y 좌표 (선발만)

    @Column(name = "is_starter", nullable = false)
    private Boolean isStarter = true; // TRUE: 선발 / FALSE: 후보
    
}
