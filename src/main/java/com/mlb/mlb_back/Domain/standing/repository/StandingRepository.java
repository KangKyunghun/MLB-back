package com.mlb.mlb_back.Domain.standing.repository;

import com.mlb.mlb_back.Domain.standing.entity.Standing;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StandingRepository extends JpaRepository<Standing, Long> {

    long countBySeason(Integer season);

    void deleteBySeason(Integer season);

    List<Standing> findBySeason(Integer season);

    List<Standing> findBySeasonAndTeamLeague(Integer season, String league);

    List<Standing> findBySeasonAndTeamDivision(Integer season, String division);
}