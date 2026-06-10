package com.mlb.mlb_back.Domain.community.service;

import com.mlb.mlb_back.Domain.community.dto.CommentRequest;
import com.mlb.mlb_back.Domain.community.dto.CommentResponse;
import com.mlb.mlb_back.Domain.community.entity.Comment;
import com.mlb.mlb_back.Domain.community.entity.CommentLike;
import com.mlb.mlb_back.Domain.community.entity.Post;
import com.mlb.mlb_back.Domain.community.repository.CommentLikeRepository;
import com.mlb.mlb_back.Domain.community.repository.CommentRepository;
import com.mlb.mlb_back.Domain.community.repository.PostRepository;
import com.mlb.mlb_back.Domain.user.entity.User;
import com.mlb.mlb_back.Domain.user.repository.UserRepository;
import com.mlb.mlb_back.Global.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    // ──────────────────────────────────────────────
    // 댓글 CRUD
    // ──────────────────────────────────────────────

    /** 댓글 작성 (parentId 없으면 댓글, 있으면 대댓글) */
    @Transactional
    public CommentResponse create(String email, Long postId, CommentRequest request) {
        User user = getUser(email);
        Post post = getPost(postId);

        Comment parent = null;
        if (request.getParentId() != null) {
            parent = getComment(request.getParentId());
            // 대댓글의 대댓글은 허용하지 않음
            if (parent.getParent() != null) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "대댓글에는 답글을 달 수 없습니다");
            }
        }

        Comment comment = Comment.builder()
                .post(post)
                .user(user)
                .parent(parent)
                .content(request.getContent())
                .likeCount(0)
                .build();

        return CommentResponse.from(commentRepository.save(comment), false);
    }

    /** 게시글의 댓글 목록 (댓글 + 대댓글 트리 구조) */
    public List<CommentResponse> getComments(String email, Long postId) {
        User user = email != null ? getUser(email) : null;

        // 최상위 댓글
        List<Comment> roots = commentRepository.findByPostIdAndParentIsNullOrderByCreatedAtAsc(postId);

        return roots.stream().map(root -> {
            CommentResponse rootResponse = CommentResponse.from(root, isLiked(user, root.getId()));

            // 대댓글
            List<CommentResponse> replies = commentRepository
                    .findByParentIdOrderByCreatedAtAsc(root.getId())
                    .stream()
                    .map(reply -> CommentResponse.from(reply, isLiked(user, reply.getId())))
                    .collect(Collectors.toList());

            rootResponse.setReplies(replies);
            return rootResponse;
        }).collect(Collectors.toList());
    }

    /** 내가 쓴 댓글 */
    public List<CommentResponse> getMyComments(String email) {
        User user = getUser(email);
        return commentRepository.findByUserId(user.getId()).stream()
                .map(c -> CommentResponse.from(c, isLiked(user, c.getId())))
                .collect(Collectors.toList());
    }

    /** 댓글 수정 */
    @Transactional
    public CommentResponse update(String email, Long commentId, CommentRequest request) {
        Comment comment = getComment(commentId);
        checkAuthor(email, comment.getUser().getEmail());
        comment.update(request.getContent());
        User user = getUser(email);
        return CommentResponse.from(comment, isLiked(user, comment.getId()));
    }

    /** 댓글 삭제 */
    @Transactional
    public void delete(String email, Long commentId) {
        Comment comment = getComment(commentId);
        checkAuthor(email, comment.getUser().getEmail());
        commentRepository.delete(comment);
    }

    // ──────────────────────────────────────────────
    // 좋아요
    // ──────────────────────────────────────────────

    /** 댓글 좋아요 토글 */
    @Transactional
    public CommentResponse toggleLike(String email, Long commentId) {
        User user = getUser(email);
        Comment comment = getComment(commentId);

        if (commentLikeRepository.existsByComment_IdAndUser_Id(commentId, user.getId())) {
            // 좋아요 취소
            CommentLike like = commentLikeRepository.findByComment_IdAndUser_Id(commentId, user.getId())
                    .orElseThrow();
            commentLikeRepository.delete(like);
            comment.decrementLike();
            return CommentResponse.from(comment, false);
        } else {
            // 좋아요 추가
            commentLikeRepository.save(CommentLike.builder().comment(comment).user(user).build());
            comment.incrementLike();
            return CommentResponse.from(comment, true);
        }
    }

    // ──────────────────────────────────────────────
    // 내부 헬퍼
    // ──────────────────────────────────────────────

    private Comment getComment(Long id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "댓글을 찾을 수 없습니다"));
    }

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
            throw new ApiException(HttpStatus.FORBIDDEN, "본인의 댓글만 수정/삭제할 수 있습니다");
        }
    }

    private boolean isLiked(User user, Long commentId) {
        if (user == null) return false;
        return commentLikeRepository.existsByComment_IdAndUser_Id(commentId, user.getId());
    }
}