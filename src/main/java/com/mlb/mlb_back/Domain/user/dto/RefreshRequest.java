package com.mlb.mlb_back.Domain.user.dto;

import lombok.Getter;

@Getter
public class RefreshRequest {
    private String refreshToken;
}