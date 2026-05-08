package com.football.Football_back.Domain.community.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.football.Football_back.Domain.community.entity.ChatMessage;
import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

     // 채팅방별 메시지 (시간순)
    List<ChatMessage> findByRoomIdOrderByCreatedAtAsc(Long roomId);

    // 채팅방별 최근 메시지 50개
    List<ChatMessage> findTop50ByRoomIdOrderByCreatedAtDesc(Long roomId);

}
