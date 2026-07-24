package com.mlb.mlb_back.Domain.user.service;

import com.mlb.mlb_back.Domain.user.dto.FavoriteResponse;
import com.mlb.mlb_back.Domain.user.entity.Favorite;
import com.mlb.mlb_back.Domain.user.entity.User;
import com.mlb.mlb_back.Domain.user.repository.FavoriteRepository;
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
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;

    // 즐겨찾기 추가
    @Transactional
    public FavoriteResponse addFavorite(String email, Long targetId, String targetType) {
        User user = getUser(email);

        if (favoriteRepository.existsByUser_IdAndTargetIdAndTargetType(user.getId(), targetId, targetType)) {
            throw new ApiException(HttpStatus.CONFLICT, "이미 즐겨찾기에 추가된 항목입니다");
        }

        Favorite favorite = Favorite.builder()
                .user(user)
                .targetId(targetId)
                .targetType(targetType.toUpperCase())
                .build();

        return FavoriteResponse.from(favoriteRepository.save(favorite));
    }

    // 즐겨찾기 삭제
    @Transactional
    public void removeFavorite(String email, Long targetId, String targetType) {
        User user = getUser(email);

        Favorite favorite = favoriteRepository
                .findByUser_IdAndTargetIdAndTargetType(user.getId(), targetId, targetType.toUpperCase())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "즐겨찾기 항목을 찾을 수 없습니다"));

        favoriteRepository.delete(favorite);
    }

    // 내 즐겨찾기 전체 조회
    public List<FavoriteResponse> getMyFavorites(String email) {
        User user = getUser(email);
        return favoriteRepository.findByUser_Id(user.getId()).stream()
                .map(FavoriteResponse::from)
                .collect(Collectors.toList());
    }

    // 내 즐겨찾기 타입별 조회 (PLAYER / TEAM)
    public List<FavoriteResponse> getMyFavoritesByType(String email, String targetType) {
        User user = getUser(email);
        return favoriteRepository.findByUser_Id(user.getId()).stream()
                .filter(f -> f.getTargetType().equalsIgnoreCase(targetType))
                .map(FavoriteResponse::from)
                .collect(Collectors.toList());
    }

    // 즐겨찾기 여부 확인
    public boolean isFavorite(String email, Long targetId, String targetType) {
        User user = getUser(email);
        return favoriteRepository.existsByUser_IdAndTargetIdAndTargetType(user.getId(), targetId, targetType.toUpperCase());
    }

    // 특정 선수/팀의 즐겨찾기 수
    public long getFavoriteCount(Long targetId, String targetType) {
        return favoriteRepository.countByTargetIdAndTargetType(targetId, targetType.toUpperCase());
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다"));
    }
}