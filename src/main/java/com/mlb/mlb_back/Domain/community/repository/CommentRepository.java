package com.mlb.mlb_back.Domain.community.repository;

import com.mlb.mlb_back.Domain.community.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    // 게시글의 최상위 댓글 (parent 없는 것)
    List<Comment> findByPostIdAndParentIsNullOrderByCreatedAtAsc(Long postId);

    // 특정 댓글의 대댓글
    List<Comment> findByParentIdOrderByCreatedAtAsc(Long parentId);

    // 유저가 쓴 댓글
    List<Comment> findByUserId(Long userId);

    // 게시글의 댓글 전체 (삭제 등 용도)
    List<Comment> findByPostId(Long postId);
}