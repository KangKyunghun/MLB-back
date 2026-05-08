package com.football.Football_back.Domain.team.entity;

import com.football.Football_back.Domain.league.entity.League;
import com.football.Football_back.Global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "team")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder

public class Team extends BaseEntity {

    @Id
    @Column(name = "id")
    private Long id; // football-data.org에서 제공하는 팀 ID 사용

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "league_id", nullable = false)
    private League league; // 팀이 속한 리그

    @Column(nullable = false, length = 100)
    private String name; // 팀 이름 ex) Manchester United, Real Madrid

    @Column(name = "short_name", length = 50)
    private String shortName; // 팀 약칭 ex) MAN UTD, R.MADRID

    @Column(length = 10)
    private String tla; // 팀 TLA (Three Letter Abbreviation) ex) MUN, RMD

    @Column(name = "emblem_url", length = 500)
    private String emblemUrl; // 팀 엠블럼 이미지 URL

    private Integer founded; // 창단 연도

    @Column(length = 100)
    private String venue; // 홈 경기장
    
}
