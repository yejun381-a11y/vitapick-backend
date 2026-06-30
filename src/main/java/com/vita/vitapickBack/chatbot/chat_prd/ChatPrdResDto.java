package com.vita.vitapickBack.chatbot.chat_prd;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// 추천상품 응답 DTO - 상품정보 + 썸네일 이미지 URL을 백엔드에서 필터링해서 프론트에 전달
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatPrdResDto {
    private Long prdId;
    private String prdNm;
    private Integer price;
    private String brand;
    private String thumbImgUrl;
    private String chatRecReason;
    private Integer sortNum;
}
