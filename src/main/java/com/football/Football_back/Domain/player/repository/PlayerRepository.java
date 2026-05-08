package com.football.Football_back.Domain.player.repository;

import com.football.Football_back.Domain.player.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PlayerRepository extends JpaRepository<Player, Long> {

     // 팀별 선수 목록
    List<Player> findByTeamId(Long teamId);

    // 선수명 검색
    List<Player> findByNameContainingIgnoreCase(String name);

    // 포지션으로 필터
    List<Player> findByPosition(String position);

    // 국적으로 필터
    List<Player> findByNationality(String nationality);

    // 복합 필터 (포지션 + 국적 + 팀)
    @Query("SELECT p FROM Player p WHERE " +
           "(:position IS NULL OR p.position = :position) AND " +
           "(:nationality IS NULL OR p.nationality = :nationality) AND " +
           "(:teamId IS NULL OR p.team.id = :teamId)")
    List<Player> findByFilter(
        @Param("position") String position,
        @Param("nationality") String nationality,
        @Param("teamId") Long teamId
    );

}
