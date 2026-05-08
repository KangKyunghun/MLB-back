package com.football.Football_back.Domain.best.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.football.Football_back.Domain.best.entity.BestElevenPlayer;
import java.util.List;

public interface BestElevenPlayerRepository extends JpaRepository<BestElevenPlayer, Long> {
    
    // 베스트 11 선수 목록
    List<BestElevenPlayer> findByBestElevenId(Long bestElevenId);
    
}
