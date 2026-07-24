package com.mlb.mlb_back.Domain.user.repository;

import com.mlb.mlb_back.Domain.user.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    boolean existsByUser_IdAndTargetIdAndTargetType(
            Long userId,
            Long targetId,
            String targetType
    );

    List<Favorite> findByUser_Id(Long userId);

    Optional<Favorite> findByUser_IdAndTargetIdAndTargetType(
            Long userId,
            Long targetId,
            String targetType
    );

    long countByTargetIdAndTargetType(
            Long targetId,
            String targetType
    );
}