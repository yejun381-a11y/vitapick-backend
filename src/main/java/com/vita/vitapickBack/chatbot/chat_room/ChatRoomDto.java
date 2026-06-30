package com.vita.vitapickBack.chatbot.chat_room;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// 챗봇 메시지 요청 DTO - 프론트에서 보내는 데이터 모양
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomDto {
	
	private String msgTxt;
	private Long chatId;
}
