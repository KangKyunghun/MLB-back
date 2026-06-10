package com.mlb.mlb_back.Domain.chat.service;

import com.mlb.mlb_back.Domain.chat.dto.ChatMessageResponse;
import com.mlb.mlb_back.Domain.chat.dto.ChatRoomResponse;
import com.mlb.mlb_back.Domain.chat.entity.ChatMessage;
import com.mlb.mlb_back.Domain.chat.entity.ChatRoom;
import com.mlb.mlb_back.Domain.chat.repository.ChatMessageRepository;
import com.mlb.mlb_back.Domain.chat.repository.ChatRoomRepository;
import com.mlb.mlb_back.Domain.game.entity.Game;
import com.mlb.mlb_back.Domain.game.repository.GameRepository;
import com.mlb.mlb_back.Domain.user.entity.User;
import com.mlb.mlb_back.Domain.user.repository.UserRepository;
import com.mlb.mlb_back.Global.exception.ApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final GameRepository gameRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    // ──────────────────────────────────────────────
    // 채팅방
    // ──────────────────────────────────────────────

    /**
     * gamePk로 채팅방 조회 (없으면 자동 생성)
     * 과거/예정/Live 경기 모두 채팅방 생성 및 채팅 가능
     * isActive는 Live 배지 표시 목적으로만 사용
     */
    @Transactional
    public ChatRoomResponse getOrCreateChatRoom(Long gamePk) {
        return chatRoomRepository.findByGame_Id(gamePk)
                .map(ChatRoomResponse::from)
                .orElseGet(() -> {
                    Game game = gameRepository.findById(gamePk)
                            .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "경기를 찾을 수 없습니다"));

                    String roomName = game.getAwayTeam().getAbbreviation()
                            + " vs " + game.getHomeTeam().getAbbreviation()
                            + " (" + game.getGameDate().toLocalDate() + ")";

                    ChatRoom chatRoom = ChatRoom.builder()
                            .game(game)
                            .roomName(roomName)
                            .build();

                    if ("Live".equalsIgnoreCase(game.getStatus())) {
                        chatRoom.activate();
                    }

                    return ChatRoomResponse.from(chatRoomRepository.save(chatRoom));
                });
    }

    /**
     * Live 중인 채팅방 목록 (Live 배지 표시용)
     * GET /api/chat/rooms/active
     */
    public List<ChatRoomResponse> getActiveChatRooms() {
        return chatRoomRepository.findByIsActiveTrue().stream()
                .map(ChatRoomResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 채팅방 단건 조회
     */
    public ChatRoomResponse getChatRoom(Long chatRoomId) {
        return chatRoomRepository.findById(chatRoomId)
                .map(ChatRoomResponse::from)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "채팅방을 찾을 수 없습니다"));
    }

    // ──────────────────────────────────────────────
    // 메시지
    // ──────────────────────────────────────────────

    /**
     * 채팅 메시지 전송
     * 과거/예정/Live 경기 모두 채팅 가능 (isActive 체크 없음)
     */
    @Transactional
    public ChatMessageResponse sendMessage(Long chatRoomId, String email, String content) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "채팅방을 찾을 수 없습니다"));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다"));

        if (content == null || content.isBlank()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "메시지 내용을 입력해주세요");
        }

        ChatMessage message = ChatMessage.builder()
                .chatRoom(chatRoom)
                .user(user)
                .content(content.trim())
                .messageType("CHAT")
                .build();

        ChatMessageResponse response = ChatMessageResponse.from(chatMessageRepository.save(message));
        messagingTemplate.convertAndSend("/topic/chat/" + chatRoomId, response);

        return response;
    }

    /**
     * 시스템 메시지 전송 (득점, 투수 교체 등 - 타임라인 스케줄러에서 호출)
     */
    @Transactional
    public void sendSystemMessage(Long chatRoomId, String content) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "채팅방을 찾을 수 없습니다"));

        ChatMessage message = ChatMessage.builder()
                .chatRoom(chatRoom)
                .user(null)
                .content(content)
                .messageType("SYSTEM")
                .build();

        ChatMessageResponse response = ChatMessageResponse.from(chatMessageRepository.save(message));
        messagingTemplate.convertAndSend("/topic/chat/" + chatRoomId, response);
    }

    /**
     * 입장 시 이전 메시지 조회 (최근 50개, 오래된 순 정렬)
     * GET /api/chat/rooms/{chatRoomId}/messages
     */
    public List<ChatMessageResponse> getRecentMessages(Long chatRoomId) {
        chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "채팅방을 찾을 수 없습니다"));

        List<ChatMessageResponse> messages = chatMessageRepository
                .findByChatRoomIdOrderByIdDesc(chatRoomId, PageRequest.of(0, 50))
                .stream()
                .map(ChatMessageResponse::from)
                .collect(Collectors.toList());

        Collections.reverse(messages);
        return messages;
    }

    /**
     * 커서 기반 이전 메시지 조회 (스크롤 업 시 추가 로드)
     * GET /api/chat/rooms/{chatRoomId}/messages?cursorId=123
     *
     * cursorId: 현재 화면에서 가장 오래된 메시지의 id
     * 해당 id보다 오래된 메시지 20개를 오래된 순으로 반환
     */
    public List<ChatMessageResponse> getMessagesBefore(Long chatRoomId, Long cursorId) {
        chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "채팅방을 찾을 수 없습니다"));

        List<ChatMessageResponse> messages = chatMessageRepository
                .findByChatRoomIdAndIdLessThanOrderByIdDesc(chatRoomId, cursorId, PageRequest.of(0, 20))
                .stream()
                .map(ChatMessageResponse::from)
                .collect(Collectors.toList());

        Collections.reverse(messages);
        return messages;
    }
}