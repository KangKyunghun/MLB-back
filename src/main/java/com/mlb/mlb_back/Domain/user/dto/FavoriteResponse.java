package com.mlb.mlb_back.Domain.user.dto;

import com.mlb.mlb_back.Domain.user.entity.Favorite;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class FavoriteResponse {

    private Long id;
    private Long targetId;
    private String targetType;
    private LocalDateTime createdAt;

    public static FavoriteResponse from(Favorite favorite) {
        return FavoriteResponse.builder()
                .id(favorite.getId())
                .targetId(favorite.getTargetId())
                .targetType(favorite.getTargetType())
                .createdAt(favorite.getCreatedAt())
                .build();
    }
}