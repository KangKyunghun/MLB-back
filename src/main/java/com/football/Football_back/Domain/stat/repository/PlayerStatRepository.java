package com.football.Football_back.Domain.stat.repository;

import com.football.Football_back.Domain.stat.entity.PlayerStat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface PlayerStatRepository extends JpaRepository<PlayerStat, Long> {

    // 경기별 선수 스탯
    List<PlayerStat> findByMatchId(Long matchId);

    // 경기별 특정 선수 스탯
    Optional<PlayerStat> findByMatchIdAndPlayerId(Long matchId, Long playerId);

    // 선수 시즌별 전체 경기 스탯 (경기별 기록 테이블용)
    List<PlayerStat> findByPlayerIdAndSeasonIdOrderByMatchMatchDateDesc(
        Long playerId, Long seasonId
    );

    // 시즌별 팀 전체 선수 스탯
    List<PlayerStat> findByTeamIdAndSeasonId(Long teamId, Long seasonId);

    // 시즌 누적 xG (선수별)
    @Query("SELECT SUM(ps.xg) FROM PlayerStat ps WHERE " +
           "ps.player.id = :playerId AND ps.season.id = :seasonId")
    Double sumXgByPlayerAndSeason(
        @Param("playerId") Long playerId,
        @Param("seasonId") Long seasonId
    );

    // 시즌 누적 득점 (선수별)
    @Query("SELECT SUM(ps.goals) FROM PlayerStat ps WHERE " +
           "ps.player.id = :playerId AND ps.season.id = :seasonId")
    Integer sumGoalsByPlayerAndSeason(
        @Param("playerId") Long playerId,
        @Param("seasonId") Long seasonId
    );

}