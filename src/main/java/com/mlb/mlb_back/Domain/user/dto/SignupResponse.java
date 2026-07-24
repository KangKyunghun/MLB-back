package com.mlb.mlb_back.Domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class SignupResponse {
    private Long id;
    private String email;
    private String nickname;
}