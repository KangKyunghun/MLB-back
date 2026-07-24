package com.mlb.mlb_back.Domain.chat.repository;

import com.mlb.mlb_back.Domain.chat.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    Optional<ChatRoom> findByRoomName(String roomName);

    // gamePk(game.id) 기준으로 채팅방 조회
    Optional<ChatRoom> findByGame_Id(Long gameId);

    // 활성화된 채팅방 전체 (진행중인 경기)
    List<ChatRoom> findByIsActiveTrue();
}