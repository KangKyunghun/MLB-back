package com.mlb.mlb_back.Domain.community.repository;

import com.mlb.mlb_back.Domain.community.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findByTitleContainingIgnoreCase(String keyword);

    List<Post> findByUserId(Long userId);
}