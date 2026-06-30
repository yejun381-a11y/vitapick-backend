package com.vita.vitapickBack.chatbot.chat_msg;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// 메시지 DB 조회 창구
@Repository
public interface ChatMsgRepository extends JpaRepository<ChatMsg, Long> {
	
    // 채팅방 ID로 전체 메시지 목록 조회 (상세 페이지 용)
    List<ChatMsg> findByChatId(Long chatId);
    
    // 채팅방 ID와 발신자 코드로 첫 메시지 조회 (목록 페이지 제목)
    Optional<ChatMsg> findTopByChatIdAndSenderCdOrderByCrtAtAsc(Long chatId, String senderCd);
    
    // 채팅방 ID로 메시지 삭제
    void deleteByChatId(Long chatId);
}