package com.football.Football_back.Domain.community.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.football.Football_back.Domain.community.entity.Post;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    
    // 카테고리별 게시글 (최신순)
    List<Post> findByCategoryOrderByCreatedAtDesc(String category);

    // 특정 선수/팀 분석 게시글
    List<Post> findByTargetIdAndTargetTypeOrderByCreatedAtDesc(
        Long targetId, String targetType
    );

    // 유저별 게시글
    List<Post> findByUserIdOrderByCreatedAtDesc(Long userId);
    
}
