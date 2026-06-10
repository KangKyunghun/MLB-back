package com.mlb.mlb_back.Domain.community.service;

import com.mlb.mlb_back.Domain.community.dto.PostRequest;
import com.mlb.mlb_back.Domain.community.dto.PostResponse;
import com.mlb.mlb_back.Domain.community.entity.Post;
import com.mlb.mlb_back.Domain.community.entity.PostLike;
import com.mlb.mlb_back.Domain.community.repository.PostLikeRepository;
import com.mlb.mlb_back.Domain.community.repository.PostRepository;
import com.mlb.mlb_back.Domain.user.entity.User;
import com.mlb.mlb_back.Domain.user.repository.UserRepository;
import com.mlb.mlb_back.Global.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final UserRepository userRepository;

    // ──────────────────────────────────────────────
    // 게시글 CRUD
    // ──────────────────────────────────────────────

    /** 게시글 작성 */
    @Transactional
    public PostResponse create(String email, PostRequest request) {
        User user = getUser(email);

        Post post = Post.builder()
                .user(user)
                .title(request.getTitle())
                .content(request.getContent())
                .category(request.getCategory() != null ? request.getCategory().toUpperCase() : "FREE")
                .relatedPlayerId(request.getRelatedPlayerId())
                .relatedTeamId(request.getRelatedTeamId())
                .likeCount(0)
                .viewCount(0)
                .build();

        return PostResponse.from(postRepository.save(post), false);
    }

    /** 게시글 목록 (페이징) */
    public Page<PostResponse> getList(String email, Pageable pageable) {
        User user = email != null ? getUser(email) : null;
        return postRepository.findAll(pageable)
                .map(post -> PostResponse.summary(post, isLiked(user, post.getId())));
    }

    /** 카테고리별 목록 (페이징) */
    public Page<PostResponse> getListByCategory(String email, String category, Pageable pageable) {
        User user = email != null ? getUser(email) : null;
        return postRepository.findByCategory(category.toUpperCase(), pageable)
                .map(post -> PostResponse.summary(post, isLiked(user, post.getId())));
    }

    /** 제목 검색 (페이징) */
    public Page<PostResponse> search(String email, String keyword, Pageable pageable) {
        User user = email != null ? getUser(email) : null;
        return postRepository.findByTitleContainingIgnoreCase(keyword, pageable)
                .map(post -> PostResponse.summary(post, isLiked(user, post.getId())));
    }

    /** 게시글 상세 조회 (조회수 증가) */
    @Transactional
    public PostResponse getDetail(String email, Long postId) {
        Post post = getPost(postId);
        post.incrementView();
        User user = email != null ? getUser(email) : null;
        return PostResponse.from(post, isLiked(user, post.getId()));
    }

    /** 내가 쓴 글 */
    public List<PostResponse> getMyPosts(String email) {
        User user = getUser(email);
        return postRepository.findByUserId(user.getId()).stream()
                .map(post -> PostResponse.summary(post, isLiked(user, post.getId())))
                .collect(Collectors.toList());
    }

    /** 선수 관련 글 */
    public List<PostResponse> getPostsByPlayer(String email, Long playerId) {
        User user = email != null ? getUser(email) : null;
        return postRepository.findByRelatedPlayerId(playerId).stream()
                .map(post -> PostResponse.summary(post, isLiked(user, post.getId())))
                .collect(Collectors.toList());
    }

    /** 팀 관련 글 */
    public List<PostResponse> getPostsByTeam(String email, Long teamId) {
        User user = email != null ? getUser(email) : null;
        return postRepository.findByRelatedTeamId(teamId).stream()
                .map(post -> PostResponse.summary(post, isLiked(user, post.getId())))
                .collect(Collectors.toList());
    }

    /** 게시글 수정 */
    @Transactional
    public PostResponse update(String email, Long postId, PostRequest request) {
        Post post = getPost(postId);
        checkAuthor(email, post.getUser().getEmail());
        post.update(request.getTitle(), request.getContent());
        User user = getUser(email);
        return PostResponse.from(post, isLiked(user, post.getId()));
    }

    /** 게시글 삭제 */
    @Transactional
    public void delete(String email, Long postId) {
        Post post = getPost(postId);
        checkAuthor(email, post.getUser().getEmail());
        postRepository.delete(post);
    }

    // ──────────────────────────────────────────────
    // 좋아요
    // ──────────────────────────────────────────────

    /** 게시글 좋아요 토글 */
    @Transactional
    public PostResponse toggleLike(String email, Long postId) {
        User user = getUser(email);
        Post post = getPost(postId);

        if (postLikeRepository.existsByPost_IdAndUser_Id(postId, user.getId())) {
            // 좋아요 취소
            PostLike like = postLikeRepository.findByPost_IdAndUser_Id(postId, user.getId())
                    .orElseThrow();
            postLikeRepository.delete(like);
            post.decrementLike();
            return PostResponse.from(post, false);
        } else {
            // 좋아요 추가
            postLikeRepository.save(PostLike.builder().post(post).user(user).build());
            post.incrementLike();
            return PostResponse.from(post, true);
        }
    }

    // ──────────────────────────────────────────────
    // 내부 헬퍼
    // ──────────────────────────────────────────────

    private Post getPost(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다"));
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다"));
    }

    private void checkAuthor(String email, String authorEmail) {
        if (!email.equals(authorEmail)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "본인의 게시글만 수정/삭제할 수 있습니다");
        }
    }

    private boolean isLiked(User user, Long postId) {
        if (user == null) return false;
        return postLikeRepository.existsByPost_IdAndUser_Id(postId, user.getId());
    }
}