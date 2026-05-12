package com.mlb.mlb_back.Domain.player.entity;

import com.mlb.mlb_back.Domain.team.entity.Team;
import com.mlb.mlb_back.Global.common.BaseEntity;
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
    private Long id;           // MLB Stats API 선수 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    @Column(nullable = false, length = 100)
    private String fullName;   // 전체 이름

    @Column(length = 50)
    private String firstName;

    @Column(length = 50)
    private String lastName;

    @Column(length = 20)
    private String position;   // SP, RP, C, 1B, 2B, 3B, SS, LF, CF, RF, DH

    @Column(name = "shirt_number", length = 5)
    private String shirtNumber; // 등번호

    @Column(length = 10)
    private String batSide;    // R, L, S (스위치)

    @Column(length = 10)
    private String pitchHand;  // R, L

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(length = 50)
    private String nationality; // 국적

    @Column(name = "photo_url", length = 300)
    private String photoUrl;   // 선수 사진 URL

    @Column(name = "is_active")
    private Boolean isActive = true;

    public void updateTeam(Team team) {
        this.team = team;
    }
}
