package com.mlb.mlb_back.Domain.stat.repository;

import com.mlb.mlb_back.Domain.stat.entity.SprayData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SprayDataRepository extends JpaRepository<SprayData, Long> {

    List<SprayData> findByPlayerId(Long playerId);

    List<SprayData> findBySeason(Integer season);
}