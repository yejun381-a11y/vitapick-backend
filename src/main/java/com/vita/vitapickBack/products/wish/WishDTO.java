package com.vita.vitapickBack.products.wish;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// 찜 DTO
// 프론트와 백엔드 사이에서 찜 데이터를 주고받을 때 사용
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WishDTO {

    // 찜 ID
    private Long wishId;

    // 회원 번호
    // 실제 찜 등록 시에는 토큰에서 userNum을 가져올 예정이라
    // 프론트에서 꼭 보낼 필요는 없음
    private Long userNum;

    // 상품 ID
    private Long prdId;

    // 생성일시
    private LocalDateTime crtAt;

    // 찜 여부
    // 상세 페이지에서 이 상품이 찜 되어 있는지 내려줄 때 사용 가능
    private boolean wished;
}