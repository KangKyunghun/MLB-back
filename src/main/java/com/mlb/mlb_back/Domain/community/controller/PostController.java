package com.mlb.mlb_back.Domain.community.controller;

import com.mlb.mlb_back.Domain.community.dto.PostRequest;
import com.mlb.mlb_back.Domain.community.dto.PostResponse;
import com.mlb.mlb_back.Domain.community.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    // ──────────────────────────────────────────────
    // 게시글 CRUD
    // ──────────────────────────────────────────────

    /**
     * 게시글 작성
     * POST /api/posts
     */
    @PostMapping
    public ResponseEntity<PostResponse> create(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody PostRequest request) {
        return ResponseEntity.ok(postService.create(userDetails.getUsername(), request));
    }

    /**
     * 게시글 전체 목록 (페이징)
     * GET /api/posts?page=0&size=20&sort=createdAt,desc
     */
    @GetMapping
    public ResponseEntity<Page<PostResponse>> getList(
            @AuthenticationPrincipal UserDetails userDetails,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        String email = userDetails != null ? userDetails.getUsername() : null;
        return ResponseEntity.ok(postService.getList(email, pageable));
    }

    /**
     * 카테고리별 목록 (페이징)
     * GET /api/posts/category/FREE?page=0&size=20
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<Page<PostResponse>> getListByCategory(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String category,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        String email = userDetails != null ? userDetails.getUsername() : null;
        return ResponseEntity.ok(postService.getListByCategory(email, category, pageable));
    }

    /**
     * 제목 검색 (페이징)
     * GET /api/posts/search?keyword=야구&page=0&size=20
     */
    @GetMapping("/search")
    public ResponseEntity<Page<PostResponse>> search(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam String keyword,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        String email = userDetails != null ? userDetails.getUsername() : null;
        return ResponseEntity.ok(postService.search(email, keyword, pageable));
    }

    /**
     * 게시글 상세 (조회수 증가)
     * GET /api/posts/{postId}
     */
    @GetMapping("/{postId}")
    public ResponseEntity<PostResponse> getDetail(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long postId) {
        String email = userDetails != null ? userDetails.getUsername() : null;
        return ResponseEntity.ok(postService.getDetail(email, postId));
    }

    /**
     * 내가 쓴 글
     * GET /api/posts/my
     */
    @GetMapping("/my")
    public ResponseEntity<List<PostResponse>> getMyPosts(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(postService.getMyPosts(userDetails.getUsername()));
    }

    /**
     * 선수 관련 글
     * GET /api/posts/player/{playerId}
     */
    @GetMapping("/player/{playerId}")
    public ResponseEntity<List<PostResponse>> getPostsByPlayer(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long playerId) {
        String email = userDetails != null ? userDetails.getUsername() : null;
        return ResponseEntity.ok(postService.getPostsByPlayer(email, playerId));
    }

    /**
     * 팀 관련 글
     * GET /api/posts/team/{teamId}
     */
    @GetMapping("/team/{teamId}")
    public ResponseEntity<List<PostResponse>> getPostsByTeam(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long teamId) {
        String email = userDetails != null ? userDetails.getUsername() : null;
        return ResponseEntity.ok(postService.getPostsByTeam(email, teamId));
    }

    /**
     * 게시글 수정
     * PUT /api/posts/{postId}
     */
    @PutMapping("/{postId}")
    public ResponseEntity<PostResponse> update(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long postId,
            @Valid @RequestBody PostRequest request) {
        return ResponseEntity.ok(postService.update(userDetails.getUsername(), postId, request));
    }

    /**
     * 게시글 삭제
     * DELETE /api/posts/{postId}
     */
    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long postId) {
        postService.delete(userDetails.getUsername(), postId);
        return ResponseEntity.noContent().build();
    }

    // ──────────────────────────────────────────────
    // 좋아요
    // ──────────────────────────────────────────────

    /**
     * 게시글 좋아요 토글
     * POST /api/posts/{postId}/like
     */
    @PostMapping("/{postId}/like")
    public ResponseEntity<PostResponse> toggleLike(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long postId) {
        return ResponseEntity.ok(postService.toggleLike(userDetails.getUsername(), postId));
    }
}