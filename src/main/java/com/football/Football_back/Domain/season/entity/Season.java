package com.football.Football_back.Domain.season.entity;

import com.football.Football_back.Domain.league.entity.League;
import com.football.Football_back.Global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "season", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"league_id", "year"})
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder

public class Season extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "league_id", nullable = false)
    private League league; // 시즌이 속한 리그

    @Column(nullable = false, length = 10)
    private String year; // 시즌 연도 ex) 2023, 2023/2024

    @Column(name = "display_name", nullable = false, length = 20)
    private String displayName; // 시즌 표시 이름 ex) 2023/24, 2023-2024

    @Column(name = "is_current")
    private boolean isCurrent; // 현재 시즌 여부
}
