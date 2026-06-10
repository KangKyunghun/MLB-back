package com.mlb.mlb_back.Domain.community.dto;

import com.mlb.mlb_back.Domain.community.entity.Comment;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class CommentResponse {

    private Long id;
    private Long postId;
    private Long userId;
    private String nickname;
    private Long parentId;          // null이면 최상위 댓글
    private String content;
    private Integer likeCount;
    private boolean liked;          // 현재 유저의 좋아요 여부
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Setter
    private List<CommentResponse> replies;  // 대댓글 (최상위 댓글일 때만)

    public static CommentResponse from(Comment comment, boolean liked) {
        return CommentResponse.builder()
                .id(comment.getId())
                .postId(comment.getPost().getId())
                .userId(comment.getUser().getId())
                .nickname(comment.getUser().getNickname())
                .parentId(comment.getParent() != null ? comment.getParent().getId() : null)
                .content(comment.getContent())
                .likeCount(comment.getLikeCount())
                .liked(liked)
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }
}