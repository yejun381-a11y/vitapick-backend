package com.vita.vitapickBack.chatbot.chat_prd;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;

// 추천상품 API 창구
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/chatbot")
public class ChatPrdController {

    private final ChatPrdService chatPrdService;

    // GET /api/v1/chatbot/messages/{msgId}/products - msgId로 추천상품+이미지 조회
    @GetMapping("/messages/{msgId}/products")
    public ResponseEntity<List<ChatPrdResDto>> getRecommendedPrds(
            @PathVariable("msgId") Long msgId) {

        return ResponseEntity.ok(chatPrdService.getRecommendedPrds(msgId));
    }
}