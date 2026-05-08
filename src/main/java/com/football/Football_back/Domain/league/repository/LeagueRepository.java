package com.football.Football_back.Domain.league.repository;

import com.football.Football_back.Domain.league.entity.League;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LeagueRepository extends JpaRepository<League, Long> {
}