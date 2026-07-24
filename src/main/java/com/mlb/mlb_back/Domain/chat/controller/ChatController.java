package com.mlb.mlb_back.Domain.chat.controller;

import com.mlb.mlb_back.Domain.chat.dto.ChatMessageRequest;
import com.mlb.mlb_back.Domain.chat.dto.ChatMessageResponse;
import com.mlb.mlb_back.Domain.chat.dto.ChatRoomResponse;
import com.mlb.mlb_back.Domain.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

@RestController
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    // ──────────────────────────────────────────────
    // REST API
    // ──────────────────────────────────────────────

    /**
     * 경기별 채팅방 조회 (없으면 자동 생성)
     * GET /api/chat/rooms/game/{gamePk}
     */
    @GetMapping("/api/chat/rooms/game/{gamePk}")
    public ResponseEntity<ChatRoomResponse> getChatRoom(@PathVariable Long gamePk) {
        return ResponseEntity.ok(chatService.getOrCreateChatRoom(gamePk));
    }

    /**
     * Live 중인 채팅방 목록 (Live 배지 표시용)
     * GET /api/chat/rooms/active
     */
    @GetMapping("/api/chat/rooms/active")
    public ResponseEntity<List<ChatRoomResponse>> getActiveChatRooms() {
        return ResponseEntity.ok(chatService.getActiveChatRooms());
    }

    /**
     * 메시지 조회
     * - cursorId 없음: 입장 시 최근 50개
     * - cursorId 있음: 해당 id 이전 메시지 20개 (스크롤 업 시 추가 로드)
     *
     * GET /api/chat/rooms/{chatRoomId}/messages          → 최근 50개
     * GET /api/chat/rooms/{chatRoomId}/messages?cursorId=123 → id < 123인 메시지 20개
     */
    @GetMapping("/api/chat/rooms/{chatRoomId}/messages")
    public ResponseEntity<List<ChatMessageResponse>> getMessages(
            @PathVariable Long chatRoomId,
            @RequestParam(required = false) Long cursorId) {

        if (cursorId == null) {
            return ResponseEntity.ok(chatService.getRecentMessages(chatRoomId));
        }
        return ResponseEntity.ok(chatService.getMessagesBefore(chatRoomId, cursorId));
    }

    // ──────────────────────────────────────────────
    // STOMP WebSocket
    // ──────────────────────────────────────────────

    /**
     * 채팅 메시지 전송
     * 클라이언트: stompClient.send("/app/chat/{chatRoomId}", {}, JSON.stringify({content: "..."}))
     * 브로드캐스트: /topic/chat/{chatRoomId}
     */
    @MessageMapping("/chat/{chatRoomId}")
    public void sendMessage(
            @DestinationVariable Long chatRoomId,
            ChatMessageRequest request,
            Principal principal) {

        if (principal == null) {
            throw new IllegalStateException("로그인이 필요합니다");
        }

        chatService.sendMessage(chatRoomId, principal.getName(), request.getContent());
    }

    // ──────────────────────────────────────────────
    // 임시 테스트용 (프론트 연동 후 삭제)
    // ──────────────────────────────────────────────

    /**
     * REST로 채팅 메시지 전송 (WebSocket 테스트용)
     * POST /api/chat/rooms/{chatRoomId}/messages/test
     */
    @PostMapping("/api/chat/rooms/{chatRoomId}/messages/test")
    public ResponseEntity<ChatMessageResponse> sendMessageTest(
            @PathVariable Long chatRoomId,
            @RequestBody ChatMessageRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                chatService.sendMessage(chatRoomId, userDetails.getUsername(), request.getContent()));
    }
}