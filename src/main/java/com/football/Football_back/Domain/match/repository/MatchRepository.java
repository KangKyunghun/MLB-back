package com.football.Football_back.Domain.match.repository;

import com.football.Football_back.Domain.match.entity.Match;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface MatchRepository extends JpaRepository<Match, Long> {

    // 시즌별 경기 목록
    List<Match> findBySeasonIdOrderByMatchDateDesc(Long seasonId);

    // 팀별 경기 목록 (홈 or 원정)
    @Query("SELECT m FROM Match m WHERE " +
           "m.season.id = :seasonId AND " +
           "(m.homeTeam.id = :teamId OR m.awayTeam.id = :teamId) " +
           "ORDER BY m.matchDate DESC")
    List<Match> findByTeamAndSeason(
        @Param("teamId") Long teamId,
        @Param("seasonId") Long seasonId
    );

    // 진행중인 경기 조회 (폴링용)
    List<Match> findByStatus(String status);

    // 라운드별 경기 조회
    List<Match> findBySeasonIdAndMatchday(Long seasonId, Integer matchday);

}