package com.vita.vitapickBack.products.prd_img;

import java.util.List;


// 이미지 서비스 인터페이스
public interface PrdImgService {

    // 상품 ID로 이미지 목록 조회
    List<PrdImg> findByPrdId(Long prdId);
}