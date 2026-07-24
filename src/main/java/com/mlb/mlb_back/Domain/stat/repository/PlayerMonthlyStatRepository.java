package com.mlb.mlb_back.Domain.stat.repository;

import com.mlb.mlb_back.Domain.stat.entity.PlayerMonthlyStat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlayerMonthlyStatRepository extends JpaRepository<PlayerMonthlyStat, Long> {

    List<PlayerMonthlyStat> findByPlayerIdAndSeasonOrderByMonth(Long playerId, Integer season);

    void deleteBySeason(Integer season);
    
    boolean existsByPlayerIdAndSeasonAndMonth(Long playerId, Integer season, Integer month);

    long countBySeason(Integer season);
}
