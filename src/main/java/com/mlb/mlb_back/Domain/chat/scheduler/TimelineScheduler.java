package com.mlb.mlb_back.Domain.chat.scheduler;

import com.mlb.mlb_back.Domain.chat.entity.ChatRoom;
import com.mlb.mlb_back.Domain.chat.repository.ChatRoomRepository;
import com.mlb.mlb_back.Domain.chat.service.ChatService;
import com.mlb.mlb_back.Domain.chat.service.TimelineService;
import com.mlb.mlb_back.Domain.game.entity.Game;
import com.mlb.mlb_back.Domain.game.repository.GameRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class TimelineScheduler {

    // "오늘"은 한국시간(KST) 기준으로 판단합니다.
    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    private final GameRepository gameRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatService chatService;
    private final TimelineService timelineService;

    /**
     * 30초마다 오늘 Live 경기 감지 → isActive(Live 배지) 관리
     *
     * 채팅방 생성은 여기서 하지 않음.
     * 채팅방은 프론트에서 경기 페이지 진입 시 getOrCreateChatRoom()으로 자동 생성.
     * 이 스케줄러는 오직 isActive(Live 배지) 플래그만 관리.
     */
    @Scheduled(fixedDelay = 30_000)
    @Transactional
    public void syncLiveBadge() {
        LocalDate today = LocalDate.now(KST);
        Instant startOfDay = today.atStartOfDay(KST).toInstant();
        Instant endOfDay = today.plusDays(1).atStartOfDay(KST).toInstant().minusNanos(1);

        // 오늘 Live 경기 목록
        List<Game> liveGames = gameRepository
                .findByStatusAndGameDateBetween("Live", startOfDay, endOfDay);

        // Live 경기 → 채팅방이 있으면 활성화, 없으면 생성 후 활성화
        for (Game game : liveGames) {
            chatRoomRepository.findByGame_Id(game.getId()).ifPresentOrElse(
                    room -> {
                        if (!room.getIsActive()) {
                            room.activate();
                            chatRoomRepository.save(room);
                            log.info("[Live 배지 ON] {}", room.getRoomName());
                        }
                    },
                    () -> {
                        // 아직 아무도 경기 페이지에 안 들어갔을 경우 → 서버가 직접 생성
                        String roomName = game.getAwayTeam().getAbbreviation()
                                + " vs " + game.getHomeTeam().getAbbreviation()
                                + " (" + game.getGameDate().atZone(KST).toLocalDate() + ")";

                        ChatRoom newRoom = ChatRoom.builder()
                                .game(game)
                                .roomName(roomName)
                                .build();
                        newRoom.activate();
                        ChatRoom saved = chatRoomRepository.save(newRoom);
                        chatService.sendSystemMessage(saved.getId(), "⚾ 경기가 시작되었습니다!");
                        log.info("[Live 채팅방 생성] {}", roomName);
                    }
            );
        }

        // 오늘 Live가 아닌 경기 채팅방 → isActive false 처리 (종료된 경기 Live 배지 제거)
        List<ChatRoom> activatedRooms = chatRoomRepository.findByIsActiveTrue();
        for (ChatRoom room : activatedRooms) {
            boolean stillLive = liveGames.stream()
                    .anyMatch(g -> g.getId().equals(room.getGame().getId()));
            if (!stillLive) {
                room.deactivate();
                chatRoomRepository.save(room);
                log.info("[Live 배지 OFF] {}", room.getRoomName());
            }
        }
    }

    /**
     * 20초마다 Live 경기(isActive=true) 타임라인 갱신 → WebSocket 브로드캐스트
     * 과거/예정 경기는 타임라인 갱신 대상 아님
     */
    @Scheduled(fixedDelay = 20_000)
    public void refreshTimelines() {
        timelineService.refreshTimelines();
    }
}