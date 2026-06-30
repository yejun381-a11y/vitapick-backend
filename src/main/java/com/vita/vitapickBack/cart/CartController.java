package com.vita.vitapickBack.cart;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cart")
public class CartController {

	private final CartService cartService;

	// 회원 장바구니 목록 조회
	@GetMapping
	public ResponseEntity<?> findByUserNum(@AuthenticationPrincipal Long userNum) {
		try {
			List<CartDTO> result = cartService.findCartListWithProduct(userNum);
			return ResponseEntity.status(HttpStatus.OK).body(result);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("장바구니 목록 조회에 실패했습니다.");
		}
	}

	// 동일 상품 체크
	@GetMapping("/check")
	public ResponseEntity<?> checkCart(
			@AuthenticationPrincipal Long userNum,
			@RequestParam("prdId") Long prdId,
			@RequestParam(value = "cusId", required = false) Long cusId) {
		try {
			Cart result;

			if (cusId == null) {
				result = cartService.findByUserNumAndCusIdIsNullAndPrdId(userNum, prdId);
			} else {
				result = cartService.findByUserNumAndCusIdAndPrdId(userNum, cusId, prdId);
			}

			return ResponseEntity.status(HttpStatus.OK).body(result);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("동일 상품 확인에 실패했습니다.");
		}
	}

	// 장바구니 담기
	@PostMapping
	public ResponseEntity<?> addCart(
			@AuthenticationPrincipal Long userNum,
			@RequestBody CartDTO dto) {
		try {
			Cart result = cartService.addCart(userNum, dto);
			return ResponseEntity.status(HttpStatus.OK).body(result);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(e.getMessage());
		}
	}

	// 장바구니 수량 변경
	@PatchMapping("/{cartId}/qty")
	public ResponseEntity<?> updateQty(
			@PathVariable("cartId") Long cartId,
			@RequestBody CartDTO dto) {
		try {
			Cart result = cartService.updateQty(cartId, dto.getItQty());
			return ResponseEntity.status(HttpStatus.OK).body(result);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(e.getMessage());
		}
	}

	// 전체 선택 / 전체 해제
	@PatchMapping("/selected/all")
	public ResponseEntity<?> updateAllSelectedYn(
			@AuthenticationPrincipal Long userNum,
			@RequestBody CartDTO dto) {
		try {
			cartService.updateAllSelectedYn(userNum, dto.getSelectedYn());
			return ResponseEntity.status(HttpStatus.OK).body("상태 변경되었습니다.");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("상태 변경에 실패했습니다.");
		}
	}

	// 장바구니 선택 상태 변경
	@PatchMapping("/{cartId}/selected")
	public ResponseEntity<?> updateSelectedYn(
			@PathVariable("cartId") Long cartId,
			@RequestBody CartDTO dto) {
		try {
			Cart result = cartService.updateSelectedYn(cartId, dto.getSelectedYn());
			return ResponseEntity.status(HttpStatus.OK).body(result);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("상품 선택 상태 변경에 실패했습니다.");
		}
	}

	// 장바구니 개별 삭제
	@DeleteMapping("/{cartId}")
	public ResponseEntity<?> deleteCart(
			@AuthenticationPrincipal Long userNum,
			@PathVariable("cartId") Long cartId) {
		try {
			Cart result = cartService.deleteCart(userNum, cartId);
			return ResponseEntity.status(HttpStatus.OK).body(result);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(e.getMessage());
		}
	}

	// 선택된 장바구니 상품 조회
	@GetMapping("/selected")
	public ResponseEntity<?> findByUserNumAndSelectedYn(@AuthenticationPrincipal Long userNum) {
		try {
			List<Cart> result = cartService.findByUserNumAndSelectedYn(userNum, 'Y');
			return ResponseEntity.status(HttpStatus.OK).body(result);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("선택 상품 조회에 실패했습니다.");
		}
	}

	// 선택 상품 삭제
	@DeleteMapping("/selected")
	public ResponseEntity<?> deleteByUserNumAndSelectedYn(@AuthenticationPrincipal Long userNum) {
		try {
			cartService.deleteByUserNumAndSelectedYn(userNum, 'Y');
			return ResponseEntity.status(HttpStatus.OK).build();
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("선택 상품 삭제에 실패했습니다.");
		}
	}


	// 개별 수량 최대 10개 체크
	@GetMapping("/check/qty/{itQty}")
	public ResponseEntity<?> checkQty(@PathVariable("itQty") Integer itQty) {
		try {
			cartService.checkQty(itQty);
			return ResponseEntity.status(HttpStatus.OK).body("수량 체크 성공");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(e.getMessage());
		}
	}

	// 장바구니 전체 수량 99개 체크
	@GetMapping("/check/total/{itQty}")
	public ResponseEntity<?> totalCheckQty(
			@AuthenticationPrincipal Long userNum,
			@PathVariable("itQty") Integer itQty) {
		try {
			cartService.totalCheckQty(userNum, itQty);
			return ResponseEntity.status(HttpStatus.OK).body("전체 수량 체크 성공");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(e.getMessage());
		}
	}
}