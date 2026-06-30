package com.vita.vitapickBack.chatbot.chat_prd;

import java.util.List;

// 추천상품 서비스 인터페이스
public interface ChatPrdService {
	
    // 추천상품 저장
	ChatPrd saveChatPrd(ChatPrdDto dto);
    // msgId로 추천상품 + THUMB 이미지 조회해서 반환
	List<ChatPrdResDto> getRecommendedPrds(Long msgId);
}
