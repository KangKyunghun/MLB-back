package com.football.Football_back.Domain.community.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.football.Football_back.Domain.community.entity.ChatRoom;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    // 경기별 채팅방
    Optional<ChatRoom> findByMatchId(Long matchId);

    // 활성화된 채팅방 목록 (진행중인 경기)
    java.util.List<ChatRoom> findByIsActiveTrue();
    
}
