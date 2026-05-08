package com.football.Football_back.Domain.community.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.football.Football_back.Domain.community.entity.Comment;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    
    // 게시글별 댓글 (부모 댓글만, 시간순)
    List<Comment> findByPostIdAndParentIsNullOrderByCreatedAtAsc(Long postId);

    // 대댓글 조회
    List<Comment> findByParentIdOrderByCreatedAtAsc(Long parentId);

    // 유저별 댓글
    List<Comment> findByUserIdOrderByCreatedAtDesc(Long userId);

}
