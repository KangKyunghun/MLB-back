package com.mlb.mlb_back.Domain.chat.repository;

import com.mlb.mlb_back.Domain.chat.entity.ChatMessage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    // 채팅방 메시지 전체 (오래된 순)
    List<ChatMessage> findByChatRoomIdOrderByCreatedAtAsc(Long chatRoomId);

    // 최근 N개 (입장 시 이전 메시지 로드용)
    List<ChatMessage> findByChatRoomIdOrderByIdDesc(Long chatRoomId, Pageable pageable);

    // 커서 기반 페이지네이션 (cursorId보다 오래된 메시지 N개)
    List<ChatMessage> findByChatRoomIdAndIdLessThanOrderByIdDesc(
            Long chatRoomId, Long cursorId, Pageable pageable);
}