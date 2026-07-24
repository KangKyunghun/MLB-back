package com.mlb.mlb_back.Domain.community.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PostRequest {

    @NotBlank(message = "제목을 입력해주세요")
    @Size(max = 200, message = "제목은 200자 이하여야 합니다")
    private String title;

    @NotBlank(message = "내용을 입력해주세요")
    private String content;

    private String category = "FREE";   // FREE, ANALYSIS

    private Long relatedPlayerId;       // 선수 분석 글일 경우
    private Long relatedTeamId;         // 팀 분석 글일 경우
}