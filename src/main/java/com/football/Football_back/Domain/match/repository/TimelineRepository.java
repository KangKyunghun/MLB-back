package com.football.Football_back.Domain.match.repository;

import com.football.Football_back.Domain.match.entity.Timeline;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TimelineRepository extends JpaRepository<Timeline, Long> {

    // 경기별 타임라인 (시간순)
    List<Timeline> findByMatchIdOrderByMinuteAsc(Long matchId);

    // 경기별 특정 이벤트만
    List<Timeline> findByMatchIdAndEventType(Long matchId, String eventType);
    
}
