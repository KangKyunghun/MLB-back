package com.mlb.mlb_back.Domain.community.entity;

import com.mlb.mlb_back.Domain.user.entity.User;
import com.mlb.mlb_back.Global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "post")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Post extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(length = 20)
    private String category;    // FREE, ANALYSIS

    @Column(name = "related_player_id")
    private Long relatedPlayerId; // 선수 분석 글일 경우 선수 ID

    @Column(name = "related_team_id")
    private Long relatedTeamId;   // 팀 분석 글일 경우 팀 ID

    @Column(name = "like_count")
    private Integer likeCount = 0;

    @Column(name = "view_count")
    private Integer viewCount = 0;

    public void incrementLike() {
        this.likeCount++;
    }

    public void decrementLike() {
        if (this.likeCount > 0) this.likeCount--;
    }

    public void incrementView() {
        this.viewCount++;
    }

    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
