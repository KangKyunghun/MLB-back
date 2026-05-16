package com.mlb.mlb_back.Domain.standing.service;

import com.mlb.mlb_back.Domain.standing.dto.StandingResponse;
import com.mlb.mlb_back.Domain.standing.repository.StandingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StandingService {

    private final StandingRepository standingRepository;

    // 시즌 전체 순위 (지구 순위 기준 정렬)
    public List<StandingResponse> getStandings(Integer season) {
        return standingRepository.findBySeason(season).stream()
                .map(StandingResponse::fromEntity)
                .sorted(Comparator.comparing(StandingResponse::getDivision)
                        .thenComparing(StandingResponse::getDivisionRank))
                .collect(Collectors.toList());
    }

    // 리그별 순위 (American League / National League)
    public List<StandingResponse> getStandingsByLeague(Integer season, String league) {
        return standingRepository.findBySeasonAndTeamLeague(season, league).stream()
                .map(StandingResponse::fromEntity)
                .sorted(Comparator.comparing(StandingResponse::getDivision)
                        .thenComparing(StandingResponse::getDivisionRank))
                .collect(Collectors.toList());
    }

    // 지구별 순위 (AL East, AL Central, AL West, NL East, NL Central, NL West)
    public List<StandingResponse> getStandingsByDivision(Integer season, String division) {
        return standingRepository.findBySeasonAndTeamDivision(season, division).stream()
                .map(StandingResponse::fromEntity)
                .sorted(Comparator.comparing(StandingResponse::getDivisionRank))
                .collect(Collectors.toList());
    }
}