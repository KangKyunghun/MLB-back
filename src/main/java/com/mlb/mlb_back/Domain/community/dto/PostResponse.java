package com.mlb.mlb_back.Domain.community.dto;

import com.mlb.mlb_back.Domain.community.entity.Post;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PostResponse {

    private Long id;
    private Long userId;
    private String nickname;
    private String title;
    private String content;
    private String category;
    private Long relatedPlayerId;
    private Long relatedTeamId;
    private Integer likeCount;
    private Integer viewCount;
    private boolean liked;          // 현재 유저의 좋아요 여부
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static PostResponse from(Post post, boolean liked) {
        return PostResponse.builder()
                .id(post.getId())
                .userId(post.getUser().getId())
                .nickname(post.getUser().getNickname())
                .title(post.getTitle())
                .content(post.getContent())
                .category(post.getCategory())
                .relatedPlayerId(post.getRelatedPlayerId())
                .relatedTeamId(post.getRelatedTeamId())
                .likeCount(post.getLikeCount())
                .viewCount(post.getViewCount())
                .liked(liked)
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }

    // 목록 조회용 (content 제외 버전)
    public static PostResponse summary(Post post, boolean liked) {
        return PostResponse.builder()
                .id(post.getId())
                .userId(post.getUser().getId())
                .nickname(post.getUser().getNickname())
                .title(post.getTitle())
                .category(post.getCategory())
                .relatedPlayerId(post.getRelatedPlayerId())
                .relatedTeamId(post.getRelatedTeamId())
                .likeCount(post.getLikeCount())
                .viewCount(post.getViewCount())
                .liked(liked)
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }
}