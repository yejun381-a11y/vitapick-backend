package com.vita.vitapickBack.chatbot.chat_prd;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// 추천상품 DB 조회 창구
@Repository
public interface ChatPrdRepository extends JpaRepository<ChatPrd, Long> {
    
	// msgId로 추천상품 목록 조회
    List<ChatPrd> findByMsgId(Long msgId);
   
    // msgId로 추천상품 삭제
    void deleteByMsgId(Long msgId);
}
