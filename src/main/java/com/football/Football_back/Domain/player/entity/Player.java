package com.football.Football_back.Domain.player.entity;

import com.football.Football_back.Domain.team.entity.Team;
import com.football.Football_back.Global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "player")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder

public class Player extends BaseEntity {

        @Id
    @Column(name = "id")
    private Long id;           // football-data.org 선수 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "first_name", length = 100)
    private String firstName;

    @Column(name = "last_name", length = 100)
    private String lastName;

    @Column(name = "shirt_number")
    private Integer shirtNumber;   // 등번호

    @Column(length = 20)
    private String position;       // Goalkeeper / Defender / Midfielder / Forward

    @Column(length = 100)
    private String nationality;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "espn_id", length = 50)
    private String espnId;         // ESPN 사진 URL 생성용
    
}
