package com.vita.vitapickBack.cscenter.inq;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.Authentication;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cscenter")
public class InqController {

	private final InqService inqService;

	// 관리자 권한 확인
	private boolean isAdmin(Authentication authentication) {

		if (authentication == null) {
			return false;
		}

		return authentication.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
	}

	// 로그인 회원번호 조회
	private Long getLoginUserNum(Authentication authentication) {

		if (authentication == null) {
			return null;
		}

		return Long.valueOf(authentication.getPrincipal().toString());
	}

	// 전체 1:1 문의 목록 조회
	@GetMapping("/inquiries")
	public ResponseEntity<?> getAllInq() {

		try {
			List<Inq> result = inqService.getAllInq();

			return ResponseEntity.status(HttpStatus.OK).body(result);

		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("1:1 문의 목록 조회에 실패했습니다.");
		}
	}

	// 회원 본인 문의 목록 조회 (마이페이지)
	@GetMapping("/mypage/inquiries")
	public ResponseEntity<?> getMyInq(Authentication authentication) {

		try {

			if (authentication == null) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 후 이용해주세요.");
			}

			Long userNum = getLoginUserNum(authentication);

			List<Inq> result = inqService.getMyInq(userNum);

			return ResponseEntity.status(HttpStatus.OK).body(result);

		} catch (Exception e) {

			return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("내 문의 목록 조회에 실패했습니다.");
		}
	}

	// 문의 상세 조회(관리자+회원)
	@GetMapping("/inquiries/{inqId}")
	public ResponseEntity<?> selectOne(@PathVariable("inqId") Long inqId, Authentication authentication) {

		try {

			if (authentication == null) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 후 이용해주세요.");
			}

			Inq result;

			if (isAdmin(authentication)) {
				result = inqService.selectOne(inqId);
			} else {
				Long userNum = getLoginUserNum(authentication);
				result = inqService.selectMyOne(inqId, userNum);
			}

			return ResponseEntity.status(HttpStatus.OK).body(result);

		} catch (Exception e) {

			return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("문의 상세 정보를 불러오지 못했습니다.");
		}
	}

	// 문의 등록
	@PostMapping("/inquiries")
	public ResponseEntity<?> createInq(@RequestBody Inq inq, Authentication authentication) {

		try {
			if (authentication == null) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 후 이용해주세요.");
			}

			Long userNum = getLoginUserNum(authentication);

			inqService.createInq(inq, userNum);

			return ResponseEntity.status(HttpStatus.CREATED).build();

		} catch (Exception e) {
			e.printStackTrace();

			return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("1:1 문의 등록에 실패했습니다.");
		}
	}

	// 회원 본인 문의 수정
	@PatchMapping("/inquiries/{inqId}")
	public ResponseEntity<?> updateInq(@PathVariable("inqId") Long inqId, @RequestBody Inq inq,
			Authentication authentication) {

		try {
			if (authentication == null) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 후 이용해주세요.");
			}

			Long userNum = getLoginUserNum(authentication);

			inqService.updateInq(inqId, userNum, inq);

			return ResponseEntity.status(HttpStatus.OK).build();

		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("1:1 문의 수정에 실패했습니다.");
		}
	}

	// 회원 본인 문의 삭제
	@DeleteMapping("/inquiries/{inqId}")
	public ResponseEntity<?> deleteInq(@PathVariable("inqId") Long inqId, Authentication authentication) {

		try {
			if (authentication == null) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 후 이용해주세요.");
			}

			Long userNum = getLoginUserNum(authentication);

			inqService.deleteInq(inqId, userNum);

			return ResponseEntity.status(HttpStatus.OK).build();

		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("1:1 문의 삭제에 실패했습니다.");
		}
	}

	// 관리자 답변 등록
	@PatchMapping("/inquiries/{inqId}/answer")
	public ResponseEntity<?> answerInq(@PathVariable("inqId") Long inqId, @RequestBody Inq inq,
			Authentication authentication) {

		try {
			if (authentication == null) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 후 이용해주세요.");
			}

			if (!isAdmin(authentication)) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body("관리자만 이용할 수 있습니다.");
			}

			inqService.answerInq(inqId, inq.getAnsTxt());

			return ResponseEntity.status(HttpStatus.OK).build();

		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("댓글 등록에 실패했습니다.");
		}
	}

}