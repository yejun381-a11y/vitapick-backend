package com.vita.vitapickBack.chatbot.chat_room;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.stereotype.Service;

import com.vita.vitapickBack.chatbot.chat_msg.ChatMsg;
import com.vita.vitapickBack.chatbot.chat_msg.ChatMsgRepository;
import com.vita.vitapickBack.chatbot.chat_prd.ChatPrdDto;
import com.vita.vitapickBack.chatbot.chat_prd.ChatPrdRepository;
import com.vita.vitapickBack.chatbot.chat_prd.ChatPrdService;
import com.vita.vitapickBack.products.prd.Prd;
import com.vita.vitapickBack.products.prd.PrdRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
public class ChatRoomServiceImpl implements ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMsgRepository chatMsgRepository;
    private final OpenAiChatModel openAiChatModel;
    private final ChatPrdRepository chatPrdRepository;
    private final ChatPrdService chatPrdService;
    private final PrdRepository prdRepository;
    private final ObjectMapper objectMapper;
    
    @Override
    public ChatMsg chatMsg(Long userNum, ChatRoomDto dto) {

        // 0. 값 검증
        if (userNum == null) {
            throw new RuntimeException("로그인이 필요합니다.");
        }

        if (dto == null || dto.getMsgTxt() == null || dto.getMsgTxt().trim().isEmpty()) {
            throw new RuntimeException("메시지 내용이 없습니다.");
        }

        // 1. chatId가 있으면 기존 방 사용, 없으면 ACTIVE 방 찾고 없을 때 새로 생성
        ChatRoom chatRoom;

        if (dto.getChatId() != null) {

            chatRoom = chatRoomRepository.findById(dto.getChatId())
                    .orElseThrow(() -> new RuntimeException("채팅방이 없습니다."));

            if (!chatRoom.getUserNum().equals(userNum)) {
                throw new RuntimeException("본인 채팅방만 사용할 수 있습니다.");
            }

        } else {

            Optional<ChatRoom> found =
                    chatRoomRepository.findTopByUserNumAndChatStCd(userNum, "ACTIVE");

            if (found.isPresent()) {
                chatRoom = found.get();
            } else {
                chatRoom = chatRoomRepository.save(
                        ChatRoom.builder()
                                .userNum(userNum)
                                .chatStCd("ACTIVE")
                                .crtAt(LocalDateTime.now())
                                .updAt(LocalDateTime.now())
                                .build()
                );
            }
        }

        // 2. 사용자 메시지 저장
        ChatMsg userMsg = ChatMsg.builder()
                .chatId(chatRoom.getChatId())
                .senderCd("USER")
                .msgTxt(dto.getMsgTxt())
                .crtAt(LocalDateTime.now())
                .build();
        chatMsgRepository.save(userMsg);

        // 3. DB에서 상품 목록 가져오기
        List<Prd> prdList = prdRepository.findAll();
        String prdInfo = "";

        for (Prd p : prdList) {
            prdInfo += "상품ID:" + p.getPrdId()
                    + " 상품명:" + p.getPrdNm()
                    + " 카테고리:" + p.getCatCd()
                    + " 성분:" + p.getIngr()
                    + " 상품설명:" + p.getDescTxt()
                    + " 복용법:" + p.getDosTxt()
                    + " 주의사항:" + p.getWarnTxt()
                    + "\n";
        }

     // 4. GPT 호출
        String prompt = "당신은 VitaPick 쇼핑몰의 건강기능식품 상품 추천 챗봇입니다.\n"
                + "당신의 역할은 사용자의 증상이나 건강 고민에 맞는 상품을 간단히 안내하는 것입니다.\n"
                + "여러 영양제를 조합해서 처방하듯 추천하지 마세요.\n"
                + "VitaPick에는 별도의 AI 맞춤 영양 설문 기능이 있으므로, 조합 추천은 챗봇에서 하지 않습니다.\n\n"

                + "아래는 우리 쇼핑몰 상품 목록입니다:\n"
                + prdInfo + "\n\n"

                + "사용자 요청: " + dto.getMsgTxt() + "\n\n"

                + "반드시 지켜야 할 규칙:\n"
                + "1. 사용자의 요청이 욕설, 비하, 장난, 숫자만 있는 입력, 의미 없는 문장, 상품 추천과 무관한 내용이면 상품을 추천하지 마세요.\n"
                + "2. 예를 들어 '12345', 'ㅋㅋㅋ', 'asdf', '...', '???', 욕설만 있는 문장은 products를 빈 배열 []로 반환하세요.\n"
                + "3. 추천하지 않는 경우 reason에는 '건강 고민이나 원하는 기능을 조금 더 구체적으로 알려주시면 알맞은 상품을 안내해드릴게요.'처럼 자연스럽게 답하세요.\n"
                + "4. 사용자가 '조합', '같이 먹을 것', '여러 개 추천', '세트', '루틴', '복합 추천'을 요청하면 products는 빈 배열 []로 반환하세요.\n"
                + "5. 조합 요청인 경우 reason에는 '영양제 조합은 개인 상태에 따라 달라질 수 있어 VitaPick AI 맞춤 영양 설문을 이용해 주세요.'라고 안내하세요.\n"
                + "6. 사용자가 피로, 눈 건강, 장 건강, 면역, 피부, 다이어트, 수면, 관절 등 건강 목적을 말한 경우에만 상품을 추천하세요.\n"
                + "7. 추천 상품은 항상 가장 적합한 상품 1개만 고르세요. 어떤 경우에도 2개 이상 추천하지 마세요.\n"
                + "8. 종합영양제, 올인원, 멀티비타민, 종합비타민 상품은 단독 추천하세요.\n"
                + "9. 같은 성분이나 비슷한 목적의 상품을 중복 추천하지 마세요.\n"
                + "10. 상품 목록에 없는 상품명이나 상품ID는 절대 만들지 마세요.\n"
                + "11. 상품의 성분, 상품설명, 복용법, 주의사항을 근거로 추천하세요.\n"
                + "12. 추천 이유는 '도움이 됩니다'처럼 포괄적으로 쓰지 말고, 실제 포함된 성분명이나 상품설명에 있는 핵심 표현을 1개 이상 포함해서 2문장 정도로 작성하세요.\n"
                + "13. 상품 목록에 없는 성분이나 효능은 절대 언급하지 마세요.\n"
                + "14. 의학적 진단이나 치료처럼 말하지 말고, 건강기능식품 선택 참고 수준으로 말하세요.\n"
                + "15. comboReason은 항상 빈 문자열로 두세요.\n"
                + "16. caution은 실제 주의사항이 필요할 때만 작성하세요. 필요 없으면 빈 문자열로 두세요.\n"
                + "17. 반드시 아래 JSON 형식으로만 답하세요. JSON 밖에 다른 문장은 절대 쓰지 마세요.\n\n"

                + "{\n"
                + "  \"reason\": \"추천 이유 또는 추천하지 않는 이유. 추천하는 경우 실제 성분이나 상품설명을 근거로 2문장 정도 작성\",\n"
                + "  \"products\": [\n"
                + "    {\"prd_id\": 숫자, \"sort_num\": 1}\n"
                + "  ],\n"
                + "  \"comboReason\": \"\",\n"
                + "  \"caution\": \"필요한 주의사항, 없으면 빈 문자열\"\n"
                + "}\n";

        String gptResponse = openAiChatModel.call(prompt);

        // 5. 봇 응답 저장
        ChatMsg botMsg = ChatMsg.builder()
                .chatId(chatRoom.getChatId())
                .senderCd("AI")
                .msgTxt(gptResponse)
                .aiModel("gpt-4o-mini")
                .crtAt(LocalDateTime.now())
                .build();
        botMsg = chatMsgRepository.save(botMsg);

        // 6. GPT 응답 JSON 파싱
        JsonNode root;

        try {
            root = objectMapper.readTree(gptResponse);
        } catch (Exception e) {
            throw new RuntimeException("GPT 응답 JSON 파싱 실패");
        }

        // 7. products 배열 꺼내기
        JsonNode products = root.path("products");

        // 8. 추천상품 저장
        if (products.isArray()) {
            for (JsonNode item : products) {

                Long prdId = item.path("prd_id").asLong();
                Integer sortNum = item.path("sort_num").asInt();

                ChatPrdDto chatPrdDto = ChatPrdDto.builder()
                        .msgId(botMsg.getMsgId())
                        .prdId(prdId)
                        .sortNum(sortNum)
                        .chatRecReason(gptResponse)
                        .build();
  
                chatPrdService.saveChatPrd(chatPrdDto);
            }
        }

        return botMsg;
    }
    
    @Override
    public List<ChatRoomMPDto> getMyChatRooms(Long userNum) {
    	
    	 // 1. 내 채팅방 목록 가져오기
        List<ChatRoom> roomList = chatRoomRepository.findByUserNumOrderByUpdAtDesc(userNum);

        // 2. 프론트로 보낼 DTO 리스트 만들기
        List<ChatRoomMPDto> dtoList = new java.util.ArrayList<>();

        // 3. 채팅방 하나씩 꺼내서 DTO로 바꾸기
        for (ChatRoom room : roomList) {

            // 4. 이 채팅방의 첫 번째 USER 메시지 가져오기
            Optional<ChatMsg> firstMsg =
                    chatMsgRepository.findTopByChatIdAndSenderCdOrderByCrtAtAsc(
                            room.getChatId(),
                            "USER"
                    );

            // 5. 제목 정하기
            String title = "제목 없는 상담";

            if (firstMsg.isPresent()) {
                title = firstMsg.get().getMsgTxt();
            }

            // 6. DTO 만들기
            ChatRoomMPDto dto = ChatRoomMPDto.builder()
                    .chatId(room.getChatId())
                    .title(title)
                    .crtAt(room.getCrtAt())
                    .updAt(room.getUpdAt())
                    .build();

            // 7. 리스트에 담기
            dtoList.add(dto);
        }

        // 8. 완성된 목록 반환
        return dtoList;
    }
    
    // 챗봇방 닫기
    @Override
    public void closeChatRoom(Long userNum, Long chatId) {

        ChatRoom chatRoom = chatRoomRepository.findById(chatId)
                .orElseThrow(() -> new RuntimeException("채팅방이 없습니다."));

        if (!chatRoom.getUserNum().equals(userNum)) {
            throw new RuntimeException("본인 채팅방만 닫을 수 있습니다.");
        }

        chatRoom.setChatStCd("CLOSED");
        chatRoom.setUpdAt(LocalDateTime.now());

        chatRoomRepository.save(chatRoom);
    }
    
    // 마이페이지 - 챗봇 상담방 삭제
    @Transactional // 트랜잭션 처리로 메시지와 추천상품이 함께 삭제되도록 보장
    @Override
    public void deleteChatRoom(Long userNum, Long chatId) {

        // 1. 채팅방 조회
        ChatRoom chatRoom = chatRoomRepository.findById(chatId)
                .orElseThrow(() -> new RuntimeException("채팅방이 없습니다."));

        // 2. 본인 채팅방인지 확인
        if (!chatRoom.getUserNum().equals(userNum)) {
            throw new RuntimeException("본인 채팅방만 삭제할 수 있습니다.");
        }

        // 3. 해당 채팅방의 메시지 목록 조회
        List<ChatMsg> msgList = chatMsgRepository.findByChatId(chatId);

        // 4. 각 메시지에 연결된 추천상품 삭제
        for (ChatMsg msg : msgList) {
            chatPrdRepository.deleteByMsgId(msg.getMsgId());
        }

        // 5. 메시지 삭제
        chatMsgRepository.deleteByChatId(chatId);

        // 6. 채팅방 삭제
        chatRoomRepository.delete(chatRoom);
    }
}
