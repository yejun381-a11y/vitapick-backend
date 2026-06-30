package com.vita.vitapickBack.chatbot.chat_prd;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import com.vita.vitapickBack.products.prd.Prd;
import com.vita.vitapickBack.products.prd.PrdRepository;
import com.vita.vitapickBack.products.prd_img.PrdImg;
import com.vita.vitapickBack.products.prd_img.PrdImgRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatPrdServiceImpl implements ChatPrdService {

    private final ChatPrdRepository chatPrdRepository;
    private final PrdRepository prdRepository;
    private final PrdImgRepository prdImgRepository;
    
    // 챗봇이 추천한 상품 저장
    @Override
    public ChatPrd saveChatPrd(ChatPrdDto dto) {
        ChatPrd chatPrd = ChatPrd.builder()
                .msgId(dto.getMsgId())
                .prdId(dto.getPrdId())
                .sortNum(dto.getSortNum())
                .chatRecReason(dto.getChatRecReason())
                .build();
        return chatPrdRepository.save(chatPrd);
    }
    
    // 챗봇이 추천한 상품 목록 반환
    @Override
    public List<ChatPrdResDto> getRecommendedPrds(Long msgId) {
    	
    	// msgId로 챗봇이 추천한 상품 목록 조회
        List<ChatPrd> chatPrds = chatPrdRepository.findByMsgId(msgId);
        List<ChatPrdResDto> result = new ArrayList<>();
        
        for (ChatPrd chatPrd : chatPrds) {
            Optional<Prd> prdResult = prdRepository.findById(chatPrd.getPrdId());
            if (prdResult.isPresent()) { // 상품이 존재하는 경우에만 결과에 추가
                Prd prd = prdResult.get();
                
                String thumbUrl = null;
                List<PrdImg> imgList = prdImgRepository.findByPrdId(prd.getPrdId());
                for (PrdImg img : imgList) {
                    if ("THUMB".equals(img.getImgTypeCd())) {
                        thumbUrl = img.getImgUrl();
                        break; // 하나 찾으면 바로 탈출
                    }
                }
                
                // Builder 패턴: new ChatPrdResDto(...) 대신에 가독성 좋게 객체 생성
                ChatPrdResDto dto = ChatPrdResDto.builder()
                        .prdId(prd.getPrdId())
                        .prdNm(prd.getPrdNm())
                        .price(prd.getPrice())
                        .brand(prd.getBrand())
                        .thumbImgUrl(thumbUrl) // 상품 썸네일 이미지 URL
                        .chatRecReason(chatPrd.getChatRecReason())
                        .sortNum(chatPrd.getSortNum())
                        .build();
                result.add(dto);
            }
        }
        return result;
    }
}
