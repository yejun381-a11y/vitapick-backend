package com.vita.vitapickBack.chatbot.chat_msg;

import java.util.List;

// 메시지 서비스 인터페이스
public interface ChatMsgService {

    // 채팅방 ID로 메시지 목록 조회
    List<ChatMsg> findByChatId(Long chatId);
}