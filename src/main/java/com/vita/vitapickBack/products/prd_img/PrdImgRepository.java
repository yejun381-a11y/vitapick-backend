package com.vita.vitapickBack.products.prd_img;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


// 이미지 DB 조회 창구
@Repository
public interface PrdImgRepository extends JpaRepository<PrdImg, Long> {
	
    // 상품 ID로 이미지 목록 조회
    List<PrdImg> findByPrdId(Long prdId);
    
    // 상품 type 코드, Prd_id 에 따라 목록조회
    Optional<PrdImg> findByPrdIdAndImgTypeCd(Long prdId, String imgTypeCd);
 
}
