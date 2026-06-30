package com.vita.vitapickBack.cscenter.faq;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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
@RequestMapping("/api/cscenter")
public class FaqController {

	private final FaqService faqService;

	// 관리자 권한 확인
	private boolean isAdmin(Authentication authentication) {

		if (authentication == null) {
			return false;
		}

		return authentication.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
	}

	// FAQ 목록 조회
	// = 관리자: use_yn Y/N 전체 조회
	// = 일반회원 / 비로그인: use_yn = 'Y'만 조회
	@GetMapping("/faqs")
	public ResponseEntity<?> findFaqAll(@RequestParam(value = "faqCtgCd", required = false) String faqCtgCd,
			Authentication authentication) {

		try {
			List<Faq> result;

			if (isAdmin(authentication)) {

				if (faqCtgCd != null) {
					result = faqService.findByFaqCtgCd(faqCtgCd);
				} else {
					result = faqService.findFaqAll();
				}

			} else {

				if (faqCtgCd != null) {
					result = faqService.findByFaqCtgCdAndUseYn(faqCtgCd, "Y");
				} else {
					result = faqService.findByUseYn("Y");
				}
			}

			return ResponseEntity.status(HttpStatus.OK).body(result);

		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("FAQ 목록 조회에 실패했습니다.");
		}
	}

	// FAQ 상세 조회
	// = 관리자: use_yn Y/N 모두 상세 조회 가능
	// = 일반회원 / 비로그인: use_yn = 'Y'만 상세 조회 가능
	@GetMapping("/faqs/{faqId}")
	public ResponseEntity<?> selectOne(@PathVariable("faqId") Long faqId, Authentication authentication) {

		try {
			Faq result = faqService.findOne(faqId);

			if (!isAdmin(authentication) && "N".equals(result.getUseYn())) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body("비공개 FAQ입니다.");
			}

			Faq updatedResult = faqService.selectOne(faqId);

			return ResponseEntity.status(HttpStatus.OK).body(updatedResult);

		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("FAQ 상세 정보를 불러오지 못했습니다.");
		}
	}

	// FAQ 등록
	// = 관리자만 가능
	@PostMapping("/faqs")
	public ResponseEntity<?> saveFaq(@RequestBody FaqDto dto, Authentication authentication) {

		try {
			if (!isAdmin(authentication)) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body("관리자만 이용할 수 있습니다.");
			}

			Faq result = faqService.createFaq(dto);

			return ResponseEntity.status(HttpStatus.CREATED).body(result);

		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("FAQ 등록에 실패했습니다.");
		}
	}

	// FAQ 수정
	// = 관리자만 가능
	@PatchMapping("/faqs/{faqId}")
	public ResponseEntity<?> updateFaq(@PathVariable("faqId") Long faqId, @RequestBody FaqDto dto,
			Authentication authentication) {

		try {
			if (!isAdmin(authentication)) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body("관리자만 이용할 수 있습니다.");
			}

			Faq result = faqService.updateFaq(faqId, dto);

			return ResponseEntity.status(HttpStatus.OK).body(result);

		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("FAQ 수정에 실패했습니다.");
		}
	}

	// FAQ 삭제
	// = 관리자만 가능
	@DeleteMapping("/faqs/{faqId}")
	public ResponseEntity<?> deleteFaq(@PathVariable("faqId") Long faqId, Authentication authentication) {

		try {
			if (!isAdmin(authentication)) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body("관리자만 이용할 수 있습니다.");
			}

			faqService.deleteFaq(faqId);

			return ResponseEntity.status(HttpStatus.OK).body("삭제완료");

		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("FAQ 삭제에 실패했습니다.");
		}
	}
}