package com.football.Football_back.Domain.player.service;

import com.football.Football_back.Domain.player.dto.PlayerResponse;
import com.football.Football_back.Domain.player.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlayerService {

    private final PlayerRepository playerRepository;

    // 특정 선수 조회
    public PlayerResponse getPlayer(Long playerId) {
        return playerRepository.findById(playerId)
                .map(PlayerResponse::from)
                .orElseThrow(() -> new RuntimeException("선수를 찾을 수 없습니다: " + playerId));
    }

    // 팀별 선수 목록
    public List<PlayerResponse> getPlayersByTeam(Long teamId) {
        return playerRepository.findByTeamId(teamId)
                .stream()
                .map(PlayerResponse::from)
                .collect(Collectors.toList());
    }

    // 선수 이름 검색
    public List<PlayerResponse> searchPlayers(String name) {
        return playerRepository.findByNameContainingIgnoreCase(name)
                .stream()
                .map(PlayerResponse::from)
                .collect(Collectors.toList());
    }

    // 선수 필터 검색 (포지션 + 국적 + 팀)
    public List<PlayerResponse> filterPlayers(String position, String nationality, Long teamId) {
        return playerRepository.findByFilter(position, nationality, teamId)
                .stream()
                .map(PlayerResponse::from)
                .collect(Collectors.toList());
    }

    // 포지션별 선수 목록
    public List<PlayerResponse> getPlayersByPosition(String position) {
        return playerRepository.findByPosition(position)
                .stream()
                .map(PlayerResponse::from)
                .collect(Collectors.toList());
    }

    // 국적별 선수 목록
    public List<PlayerResponse> getPlayersByNationality(String nationality) {
        return playerRepository.findByNationality(nationality)
                .stream()
                .map(PlayerResponse::from)
                .collect(Collectors.toList());
    }
}