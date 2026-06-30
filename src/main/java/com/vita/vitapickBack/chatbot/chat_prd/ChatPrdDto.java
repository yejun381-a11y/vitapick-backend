package com.vita.vitapickBack.chatbot.chat_prd;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// 추천상품 저장 요청 DTO (ChatPrdServiceImpl.saveChatPrd 에서 받아서 씀)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatPrdDto {
	private Long msgId;
	private Long prdId;
	private Integer sortNum;
	private String chatRecReason;
}
