package com.football.Football_back.Domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.football.Football_back.Domain.user.entity.Favorite;
import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    
    // 유저 즐겨찾기 목록
    List<Favorite> findByUserId(Long userId);

    // 타입별 즐겨찾기 목록 (TEAM / PLAYER)
    List<Favorite> findByUserIdAndTargetType(Long userId, String targetType);

    // 즐겨찾기 여부 확인
    Optional<Favorite> findByUserIdAndTargetIdAndTargetType(
        Long userId, Long targetId, String targetType
    );

    // 즐겨찾기 존재 여부
    boolean existsByUserIdAndTargetIdAndTargetType(
        Long userId, Long targetId, String targetType
    );
    
}
