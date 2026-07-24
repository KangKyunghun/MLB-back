package com.mlb.mlb_back.Domain.team.repository;

import com.mlb.mlb_back.Domain.team.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TeamRepository extends JpaRepository<Team, Long> {

    Optional<Team> findByAbbreviation(String abbreviation);

    List<Team> findByLeague(String league);

    List<Team> findByDivision(String division);

    List<Team> findByNameContainingIgnoreCase(String keyword);
}