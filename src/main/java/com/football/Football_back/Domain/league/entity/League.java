package com.football.Football_back.Domain.league.entity;

import com.football.Football_back.Global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "league")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder

public class League extends BaseEntity {

    @Id
    @Column(name = "id")
    private Long id; // football-data.org에서 제공하는 리그 ID 사용

    @Column(nullable = false, unique = true)
    private String name; // 리그 이름 ex) Premier League, La Liga

    @Column(length = 100)
    private String country; // 국가

    @Column(name = "emblem_url", length = 500)
    private String emblemUrl; // 리그 엠블럼 이미지 URL
    
}
