package com.mlb.mlb_back.Domain.community.repository;

import com.mlb.mlb_back.Domain.community.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    // 제목 검색 (페이징)
    Page<Post> findByTitleContainingIgnoreCase(String keyword, Pageable pageable);

    // 카테고리별 조회 (페이징)
    Page<Post> findByCategory(String category, Pageable pageable);

    // 전체 목록 (페이징)
    Page<Post> findAll(Pageable pageable);

    // 내가 쓴 글
    List<Post> findByUserId(Long userId);

    // 선수 관련 글
    List<Post> findByRelatedPlayerId(Long playerId);

    // 팀 관련 글
    List<Post> findByRelatedTeamId(Long teamId);
}