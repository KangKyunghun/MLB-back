package com.mlb.mlb_back.Global.scheduler;

import com.mlb.mlb_back.Global.config.DataInitializer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
@RequiredArgsConstructor
public class MlbSeasonDataScheduler {

    private final DataInitializer dataInitializer;

    private final AtomicBoolean gamesSyncRunning = new AtomicBoolean(false);
    private final AtomicBoolean statsSyncRunning = new AtomicBoolean(false);
    private final AtomicBoolean deepStatsSyncRunning = new AtomicBoolean(false);

    // 경기 상태/스코어 + 종료된 경기 상세 데이터 — 5분 간격
    @Scheduled(initialDelay = 10_000, fixedDelay = 5 * 60 * 1000)
    public void syncGames() {
        if (!gamesSyncRunning.compareAndSet(false, true)) {
            log.warn("[스케줄러] 경기 동기화가 아직 실행 중, 이번 회차 스킵");
            return;
        }
        try {
            log.info("[스케줄러] 경기 데이터 동기화 시작");
            dataInitializer.syncGamesAndLiveData();
            log.info("[스케줄러] 경기 데이터 동기화 완료");
        } catch (Exception e) {
            log.error("[스케줄러] 경기 데이터 동기화 실패", e);
        } finally {
            gamesSyncRunning.set(false);
        }
    }

    // 순위 + 시즌 스탯 — 30분 간격
    @Scheduled(initialDelay = 30_000, fixedDelay = 30 * 60 * 1000)
    public void syncStandingsAndSeasonStats() {
        if (!statsSyncRunning.compareAndSet(false, true)) {
            log.warn("[스케줄러] 순위/스탯 동기화가 아직 실행 중, 이번 회차 스킵");
            return;
        }
        try {
            log.info("[스케줄러] 순위/시즌 스탯 동기화 시작");
            dataInitializer.syncStandingsAndSeasonStats();
            log.info("[스케줄러] 순위/시즌 스탯 동기화 완료");
        } catch (Exception e) {
            log.error("[스케줄러] 순위/시즌 스탯 동기화 실패", e);
        } finally {
            statsSyncRunning.set(false);
        }
    }

    // 핫콜드존/상황별/월별/상대전적 — 6시간 간격 (선수 750명 순회, 무거움)
    @Scheduled(initialDelay = 60_000, fixedDelay = 6 * 60 * 60 * 1000)
    public void syncDeepPlayerStats() {
        if (!deepStatsSyncRunning.compareAndSet(false, true)) {
            log.warn("[스케줄러] 심화 선수 스탯 동기화가 아직 실행 중, 이번 회차 스킵");
            return;
        }
        try {
            log.info("[스케줄러] 심화 선수 스탯 동기화 시작");
            dataInitializer.syncDeepPlayerStats();
            log.info("[스케줄러] 심화 선수 스탯 동기화 완료");
        } catch (Exception e) {
            log.error("[스케줄러] 심화 선수 스탯 동기화 실패", e);
        } finally {
            deepStatsSyncRunning.set(false);
        }
    }
}