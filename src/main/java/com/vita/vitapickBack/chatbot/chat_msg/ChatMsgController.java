package com.vita.vitapickBack.chatbot.chat_msg;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/chatbot")
public class ChatMsgController {

    private final ChatMsgService chatMsgService;

    // 대화 이력 조회
    @GetMapping("/messages/{chatId}")
    public ResponseEntity<List<ChatMsg>> findByChatId(@PathVariable("chatId") Long chatId) {
        return ResponseEntity.ok(chatMsgService.findByChatId(chatId));
    }
}