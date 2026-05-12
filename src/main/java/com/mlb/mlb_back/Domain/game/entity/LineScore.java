package com.mlb.mlb_back.Domain.game.entity;

import com.mlb.mlb_back.Global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "line_score", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"game_id", "inning", "is_home"})
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class LineScore extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;

    @Column(nullable = false)
    private Integer inning;     // 이닝 번호

    @Column(name = "is_home", nullable = false)
    private Boolean isHome;     // true: 홈팀, false: 원정팀

    private Integer runs;       // 득점

    private Integer hits;       // 안타

    private Integer errors;     // 실책
}
