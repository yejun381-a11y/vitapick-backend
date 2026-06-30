package com.vita.vitapickBack.products.rvw;

import java.util.List;

public interface RvwService {

    // 리뷰 작성
    Rvw createRvw(Long userNum, RvwDTO dto);

    // 리뷰 작성 가능 여부 확인
    RvwCanWriteDTO canWriteReview(Long userNum, Long prdId);

    // 상품 ID로 리뷰 조회
    List<RvwDTO> findByPrdId(Long prdId);

    // 회원 번호로 리뷰 조회
    List<Rvw> findByUserNum(Long userNum);

    // 리뷰 단건 조회
    Rvw findByRvwId(Long rvwId);

    // 리뷰 삭제
    void deleteRvw(Long userNum, Long rvwId);

    // 리뷰 수정
    Rvw updateRvw(Long userNum, Long rvwId, RvwDTO dto);

    // 관리자 리뷰 답글 등록
    Rvw createRvwReply(Long rvwId, String replyTxt);

    // 관리자 리뷰 답글 수정
    Rvw updateRvwReply(Long rvwId, String replyTxt);

    // 관리자 리뷰 답글 삭제
    Rvw deleteRvwReply(Long rvwId);
}