package com.vita.vitapickBack.chatbot.chat_room;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoomMPDto {

    private Long chatId;

    // 목록에서 제목처럼 보여줄 문장
    private String title;

    // 채팅방 생성일시
    private LocalDateTime crtAt;

    // 마지막 수정일시
    private LocalDateTime updAt;
}