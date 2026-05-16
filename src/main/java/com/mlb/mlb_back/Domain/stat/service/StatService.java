package com.mlb.mlb_back.Domain.stat.service;

import com.mlb.mlb_back.Domain.stat.dto.BatterStatResponse;
import com.mlb.mlb_back.Domain.stat.dto.PitcherStatResponse;
import com.mlb.mlb_back.Domain.stat.entity.BatterStat;
import com.mlb.mlb_back.Domain.stat.entity.PitcherStat;
import com.mlb.mlb_back.Domain.stat.repository.BatterStatRepository;
import com.mlb.mlb_back.Domain.stat.repository.PitcherStatRepository;
import com.mlb.mlb_back.Global.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatService {

    private final BatterStatRepository batterStatRepository;
    private final PitcherStatRepository pitcherStatRepository;

    // ── 타자 스탯 ─────────────────────────────────────────────

    // 특정 선수 타자 스탯 전체 (시즌별)
    public List<BatterStatResponse> getBatterStats(Long playerId) {
        return batterStatRepository.findByPlayerId(playerId)
                .stream()
                .sorted(Comparator.comparing(s -> -s.getSeason()))
                .map(BatterStatResponse::from)
                .collect(Collectors.toList());
    }

    // 특정 선수 특정 시즌 타자 스탯
    public BatterStatResponse getBatterStatBySeason(Long playerId, Integer season) {
        return batterStatRepository.findByPlayerId(playerId)
                .stream()
                .filter(s -> s.getSeason().equals(season))
                .findFirst()
                .map(BatterStatResponse::from)
                .orElseThrow(() -> ApiException.notFound(
                        "타자 스탯을 찾을 수 없습니다: playerId=" + playerId + ", season=" + season));
    }

    // 시즌 타자 리더보드
    public List<BatterStatResponse> getBatterLeaderboard(Integer season, String statType, int limit) {
        return batterStatRepository.findBySeason(season)
                .stream()
                .filter(s -> s.getAtBats() != null && s.getAtBats() >= 100) // 최소 타수 기준
                .sorted(getBatterLeaderboardComparator(statType))
                .limit(limit)
                .map(BatterStatResponse::from)
                .collect(Collectors.toList());
    }

    // 팀별 타자 스탯
    public List<BatterStatResponse> getBatterStatsByTeam(Long teamId, Integer season) {
        return batterStatRepository.findByTeamIdAndSeason(teamId, season)
                .stream()
                .map(BatterStatResponse::from)
                .collect(Collectors.toList());
    }

    private Comparator<BatterStat> getBatterLeaderboardComparator(String statType) {
        return switch (statType) {
            case "obp" -> Comparator.comparing((BatterStat s) -> s.getObp() != null ? s.getObp() : 0.0).reversed();
            case "slg" -> Comparator.comparing((BatterStat s) -> s.getSlg() != null ? s.getSlg() : 0.0).reversed();
            case "ops" -> Comparator.comparing((BatterStat s) -> s.getOps() != null ? s.getOps() : 0.0).reversed();
            case "babip" -> Comparator.comparing((BatterStat s) -> s.getBabip() != null ? s.getBabip() : 0.0).reversed();
            case "hits" -> Comparator.comparing((BatterStat s) -> s.getHits() != null ? s.getHits() : 0).reversed();
            case "doubles" -> Comparator.comparing((BatterStat s) -> s.getDoubles() != null ? s.getDoubles() : 0).reversed();
            case "triples" -> Comparator.comparing((BatterStat s) -> s.getTriples() != null ? s.getTriples() : 0).reversed();
            case "homeRuns" -> Comparator.comparing((BatterStat s) -> s.getHomeRuns() != null ? s.getHomeRuns() : 0).reversed();
            case "totalBases" -> Comparator.comparing((BatterStat s) -> s.getTotalBases() != null ? s.getTotalBases() : 0).reversed();
            case "runs" -> Comparator.comparing((BatterStat s) -> s.getRuns() != null ? s.getRuns() : 0).reversed();
            case "rbi" -> Comparator.comparing((BatterStat s) -> s.getRbi() != null ? s.getRbi() : 0).reversed();
            case "walks" -> Comparator.comparing((BatterStat s) -> s.getWalks() != null ? s.getWalks() : 0).reversed();
            case "intentionalWalks" -> Comparator.comparing((BatterStat s) -> s.getIntentionalWalks() != null ? s.getIntentionalWalks() : 0).reversed();
            case "hitByPitch" -> Comparator.comparing((BatterStat s) -> s.getHitByPitch() != null ? s.getHitByPitch() : 0).reversed();
            case "stolenBases" -> Comparator.comparing((BatterStat s) -> s.getStolenBases() != null ? s.getStolenBases() : 0).reversed();
            case "caughtStealing" -> Comparator.comparing((BatterStat s) -> s.getCaughtStealing() != null ? s.getCaughtStealing() : 0).reversed();
            case "strikeOuts" -> Comparator.comparing((BatterStat s) -> s.getStrikeOuts() != null ? s.getStrikeOuts() : 0).reversed();
            case "groundIntoDoublePlay" -> Comparator.comparing((BatterStat s) -> s.getGroundIntoDoublePlay() != null ? s.getGroundIntoDoublePlay() : 0).reversed();
            case "sacBunts" -> Comparator.comparing((BatterStat s) -> s.getSacBunts() != null ? s.getSacBunts() : 0).reversed();
            case "sacFlies" -> Comparator.comparing((BatterStat s) -> s.getSacFlies() != null ? s.getSacFlies() : 0).reversed();
            case "leftOnBase" -> Comparator.comparing((BatterStat s) -> s.getLeftOnBase() != null ? s.getLeftOnBase() : 0).reversed();
            case "groundOuts" -> Comparator.comparing((BatterStat s) -> s.getGroundOuts() != null ? s.getGroundOuts() : 0).reversed();
            case "airOuts" -> Comparator.comparing((BatterStat s) -> s.getAirOuts() != null ? s.getAirOuts() : 0).reversed();
            case "groundOutsToAirOuts" -> Comparator.comparing((BatterStat s) -> s.getGroundOutsToAirOuts() != null ? s.getGroundOutsToAirOuts() : 0.0).reversed();
            case "numberOfPitches" -> Comparator.comparing((BatterStat s) -> s.getNumberOfPitches() != null ? s.getNumberOfPitches() : 0).reversed();
            case "atBatsPerHomeRun" -> Comparator.comparing((BatterStat s) -> s.getAtBatsPerHomeRun() != null ? s.getAtBatsPerHomeRun() : Double.MAX_VALUE);
            case "plateAppearances" -> Comparator.comparing((BatterStat s) -> s.getPlateAppearances() != null ? s.getPlateAppearances() : 0).reversed();
            case "atBats" -> Comparator.comparing((BatterStat s) -> s.getAtBats() != null ? s.getAtBats() : 0).reversed();
            default -> Comparator.comparing((BatterStat s) -> s.getAvg() != null ? s.getAvg() : 0.0).reversed();
        };
    }

    // ── 투수 스탯 ─────────────────────────────────────────────

    // 특정 선수 투수 스탯 전체 (시즌별)
    public List<PitcherStatResponse> getPitcherStats(Long playerId) {
        return pitcherStatRepository.findByPlayerId(playerId)
                .stream()
                .sorted(Comparator.comparing(s -> -s.getSeason()))
                .map(PitcherStatResponse::from)
                .collect(Collectors.toList());
    }

    // 특정 선수 특정 시즌 투수 스탯
    public PitcherStatResponse getPitcherStatBySeason(Long playerId, Integer season) {
        return pitcherStatRepository.findByPlayerId(playerId)
                .stream()
                .filter(s -> s.getSeason().equals(season))
                .findFirst()
                .map(PitcherStatResponse::from)
                .orElseThrow(() -> ApiException.notFound(
                        "투수 스탯을 찾을 수 없습니다: playerId=" + playerId + ", season=" + season));
    }

    // 시즌 투수 리더보드
    public List<PitcherStatResponse> getPitcherLeaderboard(Integer season, String statType, int limit) {
        return pitcherStatRepository.findBySeason(season)
                .stream()
                .filter(s -> s.getInningsPitched() != null && s.getInningsPitched() >= 20) // 최소 이닝 기준
                .sorted(getPitcherLeaderboardComparator(statType))
                .limit(limit)
                .map(PitcherStatResponse::from)
                .collect(Collectors.toList());
    }

    // 팀별 투수 스탯
    public List<PitcherStatResponse> getPitcherStatsByTeam(Long teamId, Integer season) {
        return pitcherStatRepository.findByTeamIdAndSeason(teamId, season)
                .stream()
                .map(PitcherStatResponse::from)
                .collect(Collectors.toList());
    }

    private Comparator<PitcherStat> getPitcherLeaderboardComparator(String statType) {
        return switch (statType) {
            case "wins" -> Comparator.comparing((PitcherStat s) -> s.getWins() != null ? s.getWins() : 0).reversed();
            case "losses" -> Comparator.comparing((PitcherStat s) -> s.getLosses() != null ? s.getLosses() : 0);
            case "saves" -> Comparator.comparing((PitcherStat s) -> s.getSaves() != null ? s.getSaves() : 0).reversed();
            case "saveOpportunities" -> Comparator.comparing((PitcherStat s) -> s.getSaveOpportunities() != null ? s.getSaveOpportunities() : 0).reversed();
            case "blownSaves" -> Comparator.comparing((PitcherStat s) -> s.getBlownSaves() != null ? s.getBlownSaves() : 0).reversed();
            case "holds" -> Comparator.comparing((PitcherStat s) -> s.getHolds() != null ? s.getHolds() : 0).reversed();
            case "winPercentage" -> Comparator.comparing((PitcherStat s) -> s.getWinPercentage() != null ? s.getWinPercentage() : 0.0).reversed();
            case "inningsPitched" -> Comparator.comparing((PitcherStat s) -> s.getInningsPitched() != null ? s.getInningsPitched() : 0.0).reversed();
            case "outs" -> Comparator.comparing((PitcherStat s) -> s.getOuts() != null ? s.getOuts() : 0).reversed();
            case "battersFaced" -> Comparator.comparing((PitcherStat s) -> s.getBattersFaced() != null ? s.getBattersFaced() : 0).reversed();
            case "hitsAllowed" -> Comparator.comparing((PitcherStat s) -> s.getHitsAllowed() != null ? s.getHitsAllowed() : 0).reversed();
            case "homeRunsAllowed" -> Comparator.comparing((PitcherStat s) -> s.getHomeRunsAllowed() != null ? s.getHomeRunsAllowed() : 0).reversed();
            case "doubles" -> Comparator.comparing((PitcherStat s) -> s.getDoubles() != null ? s.getDoubles() : 0).reversed();
            case "triples" -> Comparator.comparing((PitcherStat s) -> s.getTriples() != null ? s.getTriples() : 0).reversed();
            case "groundOuts" -> Comparator.comparing((PitcherStat s) -> s.getGroundOuts() != null ? s.getGroundOuts() : 0).reversed();
            case "airOuts" -> Comparator.comparing((PitcherStat s) -> s.getAirOuts() != null ? s.getAirOuts() : 0).reversed();
            case "groundOutsToAirOuts" -> Comparator.comparing((PitcherStat s) -> s.getGroundOutsToAirOuts() != null ? s.getGroundOutsToAirOuts() : 0.0).reversed();
            case "walks" -> Comparator.comparing((PitcherStat s) -> s.getWalks() != null ? s.getWalks() : 0).reversed();
            case "intentionalWalks" -> Comparator.comparing((PitcherStat s) -> s.getIntentionalWalks() != null ? s.getIntentionalWalks() : 0).reversed();
            case "hitBatsmen" -> Comparator.comparing((PitcherStat s) -> s.getHitBatsmen() != null ? s.getHitBatsmen() : 0).reversed();
            case "runs" -> Comparator.comparing((PitcherStat s) -> s.getRuns() != null ? s.getRuns() : 0).reversed();
            case "earnedRuns" -> Comparator.comparing((PitcherStat s) -> s.getEarnedRuns() != null ? s.getEarnedRuns() : 0).reversed();
            case "strikeOuts" -> Comparator.comparing((PitcherStat s) -> s.getStrikeOuts() != null ? s.getStrikeOuts() : 0).reversed();
            case "numberOfPitches" -> Comparator.comparing((PitcherStat s) -> s.getNumberOfPitches() != null ? s.getNumberOfPitches() : 0).reversed();
            case "strikePercentage" -> Comparator.comparing((PitcherStat s) -> s.getStrikePercentage() != null ? s.getStrikePercentage() : 0.0).reversed();
            case "pitchesPerInning" -> Comparator.comparing((PitcherStat s) -> s.getPitchesPerInning() != null ? s.getPitchesPerInning() : 0.0).reversed();
            case "wildPitches" -> Comparator.comparing((PitcherStat s) -> s.getWildPitches() != null ? s.getWildPitches() : 0).reversed();
            case "sacBunts" -> Comparator.comparing((PitcherStat s) -> s.getSacBunts() != null ? s.getSacBunts() : 0).reversed();
            case "sacFlies" -> Comparator.comparing((PitcherStat s) -> s.getSacFlies() != null ? s.getSacFlies() : 0).reversed();
            case "groundIntoDoublePlay" -> Comparator.comparing((PitcherStat s) -> s.getGroundIntoDoublePlay() != null ? s.getGroundIntoDoublePlay() : 0).reversed();
            case "era" -> Comparator.comparing((PitcherStat s) -> s.getEra() != null ? s.getEra() : 99.0);
            case "whip" -> Comparator.comparing((PitcherStat s) -> s.getWhip() != null ? s.getWhip() : 99.0);
            case "avgAllowed" -> Comparator.comparing((PitcherStat s) -> s.getAvgAllowed() != null ? s.getAvgAllowed() : 99.0);
            case "obpAllowed" -> Comparator.comparing((PitcherStat s) -> s.getObpAllowed() != null ? s.getObpAllowed() : 99.0);
            case "slgAllowed" -> Comparator.comparing((PitcherStat s) -> s.getSlgAllowed() != null ? s.getSlgAllowed() : 99.0);
            case "opsAllowed" -> Comparator.comparing((PitcherStat s) -> s.getOpsAllowed() != null ? s.getOpsAllowed() : 99.0);
            case "babip" -> Comparator.comparing((PitcherStat s) -> s.getBabip() != null ? s.getBabip() : 99.0);
            case "strikeOutPer9" -> Comparator.comparing((PitcherStat s) -> s.getStrikeOutPer9() != null ? s.getStrikeOutPer9() : 0.0).reversed();
            case "walkPer9" -> Comparator.comparing((PitcherStat s) -> s.getWalkPer9() != null ? s.getWalkPer9() : 99.0);
            case "hitsPer9" -> Comparator.comparing((PitcherStat s) -> s.getHitsPer9() != null ? s.getHitsPer9() : 99.0);
            case "homeRunsPer9" -> Comparator.comparing((PitcherStat s) -> s.getHomeRunsPer9() != null ? s.getHomeRunsPer9() : 99.0);
            case "runsScoredPer9" -> Comparator.comparing((PitcherStat s) -> s.getRunsScoredPer9() != null ? s.getRunsScoredPer9() : 99.0);
            case "strikeoutWalkRatio" -> Comparator.comparing((PitcherStat s) -> s.getStrikeoutWalkRatio() != null ? s.getStrikeoutWalkRatio() : 0.0).reversed();
            default -> Comparator.comparing((PitcherStat s) -> s.getEra() != null ? s.getEra() : 99.0);
        };
    }
}