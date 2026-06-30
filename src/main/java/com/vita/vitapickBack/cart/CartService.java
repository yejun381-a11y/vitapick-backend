package com.vita.vitapickBack.cart;

import java.util.List;

public interface CartService {

	// 회원 장바구니 목록 조회
	List<Cart> findByUserNum(Long userNum);
	
	// 장바구니 화면 상품 노출
	List<CartDTO> findCartListWithProduct(Long userNum);

	// 동일 상품 체크
	// 같은 커스텀(cus_id) 안에서 같은 상품이면 수량 증가
	Cart findByUserNumAndCusIdAndPrdId(Long userNum, Long cusId, Long prdId);
	
	// 일반 상품 동일 상품 체크
	// cus_id가 없는 일반 상품이면 user_num + prd_id로 체크
	Cart findByUserNumAndCusIdIsNullAndPrdId(Long userNum, Long prdId);

	// 장바구니 담기
	Cart addCart(Long userNum, CartDTO dto);

	// 장바구니 수량 증가/감소/변경
	Cart updateQty(Long cartId, Integer itQty);
	
	// 전체 선택 / 전체 해제
	void updateAllSelectedYn(Long userNum, Character selectedYn);
	
	// 장바구니 선택 상태 변경
	// 체크박스 선택/해제 시 사용
	Cart updateSelectedYn(Long cartId, Character selectedYn);

	// 장바구니 개별 삭제
	Cart deleteCart(Long userNum, Long cartId);

	// 선택된 장바구니 상품 조회
	List<Cart> findByUserNumAndSelectedYn(Long userNum, Character selectedYn);

	// 선택 상품 삭제
	void deleteByUserNumAndSelectedYn(Long userNum, Character selectedYn);

	// 개별 수량 최대 10개 체크
	void checkQty(Integer itQty);

	// 장바구니 전체 수량 99개 체크
	void totalCheckQty(Long userNum, Integer itQty);

}
