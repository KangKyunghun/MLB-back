package com.mlb.mlb_back.Domain.player.service;

import com.mlb.mlb_back.Domain.player.dto.PlayerResponse;
import com.mlb.mlb_back.Domain.player.entity.Player;
import com.mlb.mlb_back.Domain.player.repository.PlayerRepository;
import com.mlb.mlb_back.Global.exception.ApiException;
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

    // 선수 상세
    public PlayerResponse getPlayer(Long playerId) {
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> ApiException.notFound("선수를 찾을 수 없습니다: " + playerId));
        return PlayerResponse.fromEntity(player);
    }

    // 팀별 선수 목록
    public List<PlayerResponse> getPlayersByTeam(Long teamId) {
        return playerRepository.findByTeamId(teamId).stream()
                .map(PlayerResponse::fromEntity)
                .collect(Collectors.toList());
    }

    // 포지션별 선수 목록
    public List<PlayerResponse> getPlayersByPosition(String position) {
        return playerRepository.findByPosition(position).stream()
                .map(PlayerResponse::fromEntity)
                .collect(Collectors.toList());
    }

    // 선수 검색
    public List<PlayerResponse> searchPlayers(String keyword) {
        return playerRepository.findByFullNameContainingIgnoreCase(keyword).stream()
                .map(PlayerResponse::fromEntity)
                .collect(Collectors.toList());
    }

    // 활성 선수 목록
    public List<PlayerResponse> getActivePlayers() {
        return playerRepository.findByIsActiveTrue().stream()
                .map(PlayerResponse::fromEntity)
                .collect(Collectors.toList());
    }
}