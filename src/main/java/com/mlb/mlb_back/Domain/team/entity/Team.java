package com.mlb.mlb_back.Domain.team.entity;

import com.mlb.mlb_back.Global.common.BaseEntity;
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
    private Long id;           // MLB Stats API 팀 ID

    @Column(nullable = false, length = 100)
    private String name;       // 팀 전체명 (예: New York Yankees)

    @Column(length = 10)
    private String abbreviation; // 약어 (예: NYY)

    @Column(name = "team_name", length = 50)
    private String teamName;   // 팀명 (예: Yankees)

    @Column(name = "location_name", length = 50)
    private String locationName; // 도시명 (예: New York)

    @Column(length = 50)
    private String league;     // 리그 (American League / National League)

    @Column(length = 50)
    private String division;   // 지구 (예: AL East)

    @Column(length = 200)
    private String venue;      // 구장명

    @Column(name = "logo_url", length = 300)
    private String logoUrl;    // 팀 로고 URL
}
