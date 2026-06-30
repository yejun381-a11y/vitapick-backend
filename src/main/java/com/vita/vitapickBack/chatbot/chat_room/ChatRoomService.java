package com.vita.vitapickBack.chatbot.chat_room;

import java.util.List;

import com.vita.vitapickBack.chatbot.chat_msg.ChatMsg;

// 챗봇 서비스 인터페이스
public interface ChatRoomService {
	
    // 유저 메시지 받아서 GPT 호출 후 AI 응답 반환
	ChatMsg chatMsg(Long userNum, ChatRoomDto dto);
	
	// 유저 번호로 챗봇방 목록 조회
	List<ChatRoomMPDto> getMyChatRooms(Long userNum);
	
	// 챗봇방 닫기
	void closeChatRoom(Long userNum, Long chatId);
	
	// 챗봇방 삭제
	void deleteChatRoom(Long userNum, Long chatId);
}
