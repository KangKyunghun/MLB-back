package com.football.Football_back.Domain.community.entity;

import com.football.Football_back.Domain.user.entity.Users;
import com.football.Football_back.Global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "comment")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder

public class Comment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Comment parent;        // NULL: 댓글 / 값 있음: 대댓글

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    private Integer likes = 0;

    // 좋아요 증가
    public void increaseLikes() {
        this.likes++;
    }

    // 좋아요 감소
    public void decreaseLikes() {
        if (this.likes > 0) this.likes--;
    }

    // 댓글 수정
    public void update(String content) {
        this.content = content;
    }
}
