package com.mlb.mlb_back.Domain.community.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommentRequest {

    @NotBlank(message = "내용을 입력해주세요")
    private String content;

    private Long parentId;  // null이면 댓글, 있으면 대댓글
}