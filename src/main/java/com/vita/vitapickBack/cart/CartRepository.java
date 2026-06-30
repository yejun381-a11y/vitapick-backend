package com.vita.vitapickBack.cart;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface CartRepository extends JpaRepository<Cart, Long> {

	// 회원 장바구니 목록 조회
	List<Cart> findByUserNum(Long userNum);

	// 장바구니 화면 상품 노출
	@Query("""
	        SELECT new com.vita.vitapickBack.cart.CartDTO(
	            c.cartId,
	            c.userNum,
	            c.prdId,
	            c.cusId,
	            c.itQty,
	            c.selectedYn,
	            c.crtAt,
	            c.updAt,
	            p.prdNm,
	            p.price,
	            p.brand,
	            pi.imgUrl,
	            cu.cusReason,
	            cu.surTitle
	        )
	        FROM Cart c
	        JOIN Prd p ON c.prdId = p.prdId
	        JOIN PrdImg pi ON p.prdId = pi.prdId
	        LEFT JOIN Cus cu ON c.cusId = cu.cusId
	        WHERE pi.imgTypeCd = 'THUMB'
	        AND c.userNum = :userNum
	         ORDER BY
            CASE WHEN c.cusId IS NOT NULL THEN 0 ELSE 1 END,
            c.cartId DESC
	        """)
	List<CartDTO> findCartListWithProduct(@Param("userNum") Long userNum);

	// 동일 상품 체크
	// 같은 커스텀(cus_id) 안에서 같은 상품이면 수량 증가
	Cart findByUserNumAndCusIdAndPrdId(Long userNum, Long cusId, Long prdId);

	// 일반 상품 동일 상품 체크
	// cus_id가 없는 일반 상품이면 user_num + prd_id로 체크
	Cart findByUserNumAndCusIdIsNullAndPrdId(Long userNum, Long prdId);

	// 장바구니 수량 증가/감소/변경
	// 기본 제공 메서드 사용
	// findById(cartId)
	// save(cart)

	// 전체 선택 / 전체 해제
	@Modifying
	@Transactional
	@Query("""
			UPDATE Cart c
			SET c.selectedYn = :selectedYn
			WHERE c.userNum = :userNum
			""")
	void updateAllSelectedYn(@Param("userNum") Long userNum, @Param("selectedYn") Character selectedYn);

	// 개별 삭제
	// 기본 제공 메서드 사용
	// deleteById(cartId)

	// 선택된 장바구니 상품 조회
	// 선택 상품 삭제 및 결제하기 시 사용
	List<Cart> findByUserNumAndSelectedYn(Long userNum, Character selectedYn);

	// 선택 상품 삭제
	@Modifying
	@Transactional
	void deleteByUserNumAndSelectedYn(Long userNum, Character selectedYn);
	

	// 개별 수량 최대 10개 체크
	// Service에서 itQty 값 체크

	// 장바구니 전체 수량 99개 체크
	// Service에서 장바구니 전체 수량 합계 계산
	

}