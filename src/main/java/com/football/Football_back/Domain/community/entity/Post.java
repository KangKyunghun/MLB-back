package com.football.Football_back.Domain.community.entity;

import com.football.Football_back.Domain.user.entity.Users;
import com.football.Football_back.Global.common.BaseEntity;
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
    private Users user;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false, length = 20)
    private String category;       // FREE / ANALYSIS

    @Column(name = "target_id")
    private Long targetId;         // 분석 글일 때 연결된 팀/선수 ID

    @Column(name = "target_type", length = 20)
    private String targetType;     // TEAM / PLAYER

    private Integer likes = 0;
    private Integer views = 0;

    // 조회수 증가
    public void increaseViews() {
        this.views++;
    }

    // 좋아요 증가
    public void increaseLikes() {
        this.likes++;
    }

    // 좋아요 감소
    public void decreaseLikes() {
        if (this.likes > 0) this.likes--;
    }

    // 게시글 수정
    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
