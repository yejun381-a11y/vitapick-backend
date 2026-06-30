package com.vita.vitapickBack.products.rvw;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RvwDTO {

    // 리뷰 ID
    private Long rvwId;

    // 주문 상품 ID
    private Long ordItId;

    // 상품 ID
    private Long prdId;

    // 회원 번호
    private Long userNum;

    // 작성자 로그인 ID
    private String loginId;

    // 평점
    private Integer rating;

    // 리뷰 내용
    private String cmt;

    // 관리자 답글 내용
    private String replyTxt;

    // 관리자 답글 작성일
    private LocalDateTime replyAt;

    // 작성일
    private LocalDateTime crtAt;

    // 수정일
    private LocalDateTime updAt;
}