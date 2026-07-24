package com.mlb.mlb_back.Domain.player.repository;

import com.mlb.mlb_back.Domain.player.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlayerRepository extends JpaRepository<Player, Long> {

    List<Player> findByTeamId(Long teamId);

    List<Player> findByPosition(String position);

    List<Player> findByFullNameContainingIgnoreCase(String keyword);

    List<Player> findByIsActiveTrue();
}