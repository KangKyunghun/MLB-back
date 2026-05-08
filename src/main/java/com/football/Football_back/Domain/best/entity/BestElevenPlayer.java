package com.football.Football_back.Domain.best.entity;

import com.football.Football_back.Domain.player.entity.Player;
import com.football.Football_back.Global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "best_eleven_player", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"best_eleven_id", "player_id"})
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class BestElevenPlayer extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "best_eleven_id", nullable = false)
    private BestEleven bestEleven;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id", nullable = false)
    private Player player;

    @Column(length = 20)
    private String position;       // 포메이션 내 포지션

    @Column(name = "pos_x")
    private Double posX;           // 포메이션 X 좌표

    @Column(name = "pos_y")
    private Double posY;           // 포메이션 Y 좌표

    private Integer goals = 0;     // 해당 시즌 득점
    private Integer assists = 0;   // 해당 시즌 어시스트
    private Double rating;         // 해당 시즌 평균 평점
}
