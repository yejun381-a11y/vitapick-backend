package com.vita.vitapickBack.products.wish;

import java.util.List;

public interface WishService {

    // 내 찜 목록 조회
    List<Wish> findByUserNum(Long userNum);

    // 찜 여부 확인
    boolean isWished(Long userNum, Long prdId);

    // 찜 추가
    Wish addWish(Long userNum, WishDTO dto);

    // 찜 취소
    void deleteWish(Long userNum, Long prdId);

    // 찜 토글
    boolean toggleWish(Long userNum, Long prdId);
}