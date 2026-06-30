package com.vita.vitapickBack.cart;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

	private final CartRepository cartRepository;

	// 회원 장바구니 목록 조회
	@Override
	public List<Cart> findByUserNum(Long userNum) {
		return cartRepository.findByUserNum(userNum);
	}

	// 장바구니 화면 상품 노출
	@Override
	public List<CartDTO> findCartListWithProduct(Long userNum) {
		return cartRepository.findCartListWithProduct(userNum);
	}

	// 동일 상품 체크
	@Override
	public Cart findByUserNumAndCusIdAndPrdId(Long userNum, Long cusId, Long prdId) {
		return cartRepository.findByUserNumAndCusIdAndPrdId(userNum, cusId, prdId);
	}

	// 일반 상품 동일 상품 체크
	@Override
	public Cart findByUserNumAndCusIdIsNullAndPrdId(Long userNum, Long prdId) {
		return cartRepository.findByUserNumAndCusIdIsNullAndPrdId(userNum, prdId);
	}

	// 장바구니 담기
	@Override
	public Cart addCart(Long userNum, CartDTO dto) {

		Cart existCart;

		if (dto.getCusId() == null) {
			existCart = cartRepository.findByUserNumAndCusIdIsNullAndPrdId(userNum, dto.getPrdId());
		} else {
			existCart = cartRepository.findByUserNumAndCusIdAndPrdId(userNum, dto.getCusId(), dto.getPrdId());
		}

		if (existCart != null) {
			Integer newQty = existCart.getItQty() + dto.getItQty();

			checkQty(newQty);
			totalCheckQty(userNum, dto.getItQty());

			existCart.setItQty(newQty);

			return cartRepository.save(existCart);
		}

		checkQty(dto.getItQty());
		totalCheckQty(userNum, dto.getItQty());

		Cart cart = Cart.builder()
				.userNum(userNum)
				.prdId(dto.getPrdId())
				.cusId(dto.getCusId())
				.itQty(dto.getItQty())
				.selectedYn('Y')
				.build();

		return cartRepository.save(cart);
	}

	// 장바구니 수량 증가/감소/변경
	@Override
	public Cart updateQty(Long cartId, Integer itQty) {

		Cart cart = cartRepository.findById(cartId).orElse(null);

		if (cart == null) {
			throw new RuntimeException("장바구니에 담긴 상품이 없습니다.");
		}

		checkQty(itQty);

		List<Cart> cartList = cartRepository.findByUserNum(cart.getUserNum());

		int totalQty = 0;

		for (Cart item : cartList) {
			if (!item.getCartId().equals(cartId)) {
				totalQty += item.getItQty();
			}
		}

		totalQty += itQty;

		if (totalQty > 99) {
			throw new RuntimeException("장바구니에는 상품을 최대 99개까지 담을 수 있습니다.");
		}

		cart.setItQty(itQty);

		return cartRepository.save(cart);
	}

	// 전체 선택 / 전체 해제
	@Override
	public void updateAllSelectedYn(Long userNum, Character selectedYn) {
		cartRepository.updateAllSelectedYn(userNum, selectedYn);
	}

	// 장바구니 선택 상태 변경
	@Override
	public Cart updateSelectedYn(Long cartId, Character selectedYn) {

		Cart cart = cartRepository.findById(cartId).orElse(null);

		if (cart == null) {
			throw new RuntimeException("선택한 상품을 찾을 수 없습니다.");
		}

		cart.setSelectedYn(selectedYn);

		return cartRepository.save(cart);
	}

	// 장바구니 개별 삭제
	@Override
	public Cart deleteCart(Long userNum, Long cartId) {

		Cart cart = cartRepository.findById(cartId).orElse(null);

		if (cart == null) {
			throw new RuntimeException("장바구니에 담긴 상품이 없습니다.");
		}

		if (!cart.getUserNum().equals(userNum)) {
			throw new RuntimeException("본인의 장바구니 상품만 삭제할 수 있습니다.");
		}

		cartRepository.delete(cart);

		return cart;
	}

	// 선택된 장바구니 상품 조회
	@Override
	public List<Cart> findByUserNumAndSelectedYn(Long userNum, Character selectedYn) {
		return cartRepository.findByUserNumAndSelectedYn(userNum, selectedYn);
	}

	// 선택 상품 삭제
	@Override
	public void deleteByUserNumAndSelectedYn(Long userNum, Character selectedYn) {
		cartRepository.deleteByUserNumAndSelectedYn(userNum, selectedYn);
	}


	// 개별 수량 최대 10개 체크
	@Override
	public void checkQty(Integer itQty) {

		if (itQty == null || itQty < 1) {
			throw new RuntimeException("상품 수량은 1개 이상이어야 합니다.");
		}

		if (itQty > 10) {
			throw new RuntimeException("상품은 최대 10개까지 담을 수 있습니다.");
		}
	}

	// 장바구니 전체 수량 99개 체크
	@Override
	public void totalCheckQty(Long userNum, Integer itQty) {

		List<Cart> cartList = cartRepository.findByUserNum(userNum);

		int totalQty = 0;

		for (Cart cart : cartList) {
			totalQty += cart.getItQty();
		}

		totalQty += itQty;

		if (totalQty > 99) {
			throw new RuntimeException("장바구니에는 상품을 최대 99개까지 담을 수 있습니다.");
		}
	}
}