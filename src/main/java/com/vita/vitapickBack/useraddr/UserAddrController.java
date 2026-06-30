package com.vita.vitapickBack.useraddr;

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
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/address")
public class UserAddrController {

	private final UserAddrService userAddrService;

	// 로그인 확인
	private ResponseEntity<?> checkLogin(Long userNum) {

		if (userNum == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 후 이용해주세요.");
		}

		return null;
	}

	// 회원 배송지 목록 조회
	// 마이페이지 배송지 관리 / 주문서 배송지 선택 둘 다 사용
	@GetMapping
	public ResponseEntity<?> findByUserNum(@AuthenticationPrincipal Long userNum) {

		ResponseEntity<?> loginCheck = checkLogin(userNum);

		if (loginCheck != null) {
			return loginCheck;
		}

		try {
			log.info("배송지 조회 userNum => " + userNum);

			List<UserAddr> result = userAddrService.findByUserNum(userNum);

			return ResponseEntity.status(HttpStatus.OK).body(result);

		} catch (Exception e) {
			log.error("배송지 목록 조회 실패 => " + e.toString());

			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	// 기본 배송지 조회
	// 주문서 진입 시 기본 배송지 자동 표시
	@GetMapping("/base")
	public ResponseEntity<?> getBaseAddr(@AuthenticationPrincipal Long userNum) {

		ResponseEntity<?> loginCheck = checkLogin(userNum);

		if (loginCheck != null) {
			return loginCheck;
		}

		try {
			log.info("기본 배송지 조회 userNum => " + userNum);

			UserAddr result = userAddrService.getBaseAddr(userNum);

			return ResponseEntity.status(HttpStatus.OK).body(result);

		} catch (Exception e) {
			log.error("기본 배송지 조회 실패 => " + e.toString());

			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	// 배송지 등록
	// 마이페이지 배송지 추가 / 주문서에서 배송지 없을 때 추가 둘 다 사용
	@PostMapping
	public ResponseEntity<?> createAddr(@AuthenticationPrincipal Long userNum, @RequestBody UserAddrDTO dto) {

		ResponseEntity<?> loginCheck = checkLogin(userNum);

		if (loginCheck != null) {
			return loginCheck;
		}

		try {
			UserAddr result = userAddrService.createAddr(userNum, dto);

			return ResponseEntity.status(HttpStatus.OK).body(result);

		} catch (Exception e) {
			log.error("배송지 등록 실패 => " + e.toString());

			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	// 배송지 수정
	// 마이페이지 배송지 수정 / 주문서에서 배송지 변경 시 사용 가능
	@PatchMapping("/{addrId}")
	public ResponseEntity<?> updateAddr(@AuthenticationPrincipal Long userNum, @PathVariable("addrId") Long addrId,
			@RequestBody UserAddrDTO dto) {

		ResponseEntity<?> loginCheck = checkLogin(userNum);

		if (loginCheck != null) {
			return loginCheck;
		}

		try {
			UserAddr result = userAddrService.updateAddr(userNum, addrId, dto);

			return ResponseEntity.status(HttpStatus.OK).body(result);

		} catch (Exception e) {
			log.error("배송지 수정 실패 => " + e.toString());

			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	// 배송지 삭제
	// 마이페이지 배송지 관리에서 사용
	@DeleteMapping("/{addrId}")
	public ResponseEntity<?> deleteAddr(@AuthenticationPrincipal Long userNum, @PathVariable("addrId") Long addrId) {

		ResponseEntity<?> loginCheck = checkLogin(userNum);

		if (loginCheck != null) {
			return loginCheck;
		}

		try {
			userAddrService.deleteAddr(userNum, addrId);

			return ResponseEntity.status(HttpStatus.OK).body("배송지가 삭제되었습니다.");

		} catch (Exception e) {
			log.error("배송지 삭제 실패 => " + e.toString());

			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	// 기본 배송지 변경
	// 마이페이지 기본배송지 설정 / 주문서에서 선택 배송지를 기본배송지로 변경할 때 사용
	@PatchMapping("/{addrId}/base")
	public ResponseEntity<?> updateBaseAddr(@AuthenticationPrincipal Long userNum,
			@PathVariable("addrId") Long addrId) {

		ResponseEntity<?> loginCheck = checkLogin(userNum);

		if (loginCheck != null) {
			return loginCheck;
		}

		try {
			userAddrService.updateBaseAddr(userNum, addrId);

			return ResponseEntity.status(HttpStatus.OK).body("기본 배송지가 변경되었습니다.");

		} catch (Exception e) {
			log.error("기본 배송지 변경 실패 => " + e.toString());

			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}
}