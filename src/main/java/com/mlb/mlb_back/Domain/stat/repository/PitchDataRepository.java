package com.mlb.mlb_back.Domain.stat.repository;

import com.mlb.mlb_back.Domain.stat.entity.PitchData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PitchDataRepository extends JpaRepository<PitchData, Long> {

    List<PitchData> findByPitcherId(Long pitcherId);

    List<PitchData> findByGameId(Long gameId);
}