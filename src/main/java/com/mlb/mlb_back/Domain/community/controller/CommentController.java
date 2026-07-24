package com.mlb.mlb_back.Domain.community.controller;

import com.mlb.mlb_back.Domain.community.dto.CommentRequest;
import com.mlb.mlb_back.Domain.community.dto.CommentResponse;
import com.mlb.mlb_back.Domain.community.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    // ──────────────────────────────────────────────
    // 댓글 CRUD
    // ──────────────────────────────────────────────

    /**
     * 댓글 작성 (parentId 없으면 댓글, 있으면 대댓글)
     * POST /api/comments/{postId}
     * Body: { "content": "...", "parentId": null }
     */
    @PostMapping("/{postId}")
    public ResponseEntity<CommentResponse> create(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long postId,
            @Valid @RequestBody CommentRequest request) {
        return ResponseEntity.ok(commentService.create(userDetails.getUsername(), postId, request));
    }

    /**
     * 게시글 댓글 목록 (트리 구조: 댓글 + 대댓글)
     * GET /api/comments/{postId}
     */
    @GetMapping("/{postId}")
    public ResponseEntity<List<CommentResponse>> getComments(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long postId) {
        String email = userDetails != null ? userDetails.getUsername() : null;
        return ResponseEntity.ok(commentService.getComments(email, postId));
    }

    /**
     * 내가 쓴 댓글
     * GET /api/comments/my
     */
    @GetMapping("/my")
    public ResponseEntity<List<CommentResponse>> getMyComments(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(commentService.getMyComments(userDetails.getUsername()));
    }

    /**
     * 댓글 수정
     * PUT /api/comments/{commentId}
     */
    @PutMapping("/{commentId}")
    public ResponseEntity<CommentResponse> update(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long commentId,
            @Valid @RequestBody CommentRequest request) {
        return ResponseEntity.ok(commentService.update(userDetails.getUsername(), commentId, request));
    }

    /**
     * 댓글 삭제
     * DELETE /api/comments/{commentId}
     */
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long commentId) {
        commentService.delete(userDetails.getUsername(), commentId);
        return ResponseEntity.noContent().build();
    }

    // ──────────────────────────────────────────────
    // 좋아요
    // ──────────────────────────────────────────────

    /**
     * 댓글 좋아요 토글
     * POST /api/comments/{commentId}/like
     */
    @PostMapping("/{commentId}/like")
    public ResponseEntity<CommentResponse> toggleLike(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long commentId) {
        return ResponseEntity.ok(commentService.toggleLike(userDetails.getUsername(), commentId));
    }
}