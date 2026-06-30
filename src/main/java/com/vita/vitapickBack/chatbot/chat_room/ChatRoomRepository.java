package com.vita.vitapickBack.chatbot.chat_room;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
	
	// 유저 번호와 챗봇 상태 코드로 챗봇방 조회
	Optional<ChatRoom> findTopByUserNumAndChatStCd(Long userNum, String chatStCd);
	
	// 유저 번호로 챗봇방 목록 조회 (최신순)
	List<ChatRoom> findByUserNumOrderByUpdAtDesc(Long userNum);
}
