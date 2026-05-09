package com.football.Football_back.Domain.league.service;

import com.football.Football_back.Domain.league.dto.LeagueResponse;
import com.football.Football_back.Domain.league.repository.LeagueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LeagueService {

    private final LeagueRepository leagueRepository;

    // 전체 리그 목록
    public List<LeagueResponse> getAllLeagues() {
        return leagueRepository.findAll()
                .stream()
                .map(LeagueResponse::from)
                .collect(Collectors.toList());
    }

    // 특정 리그 조회
    public LeagueResponse getLeagueById(Long leagueId) {
        return leagueRepository.findById(leagueId)
                .map(LeagueResponse::from)
                .orElseThrow(() -> new RuntimeException("리그 조회 불가: " + leagueId));
    }
    
}
