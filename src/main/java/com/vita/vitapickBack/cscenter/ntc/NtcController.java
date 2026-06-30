package com.vita.vitapickBack.cscenter.ntc;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
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

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cscenter")
@Log4j2
@CrossOrigin(origins = { "http://localhost:5173", "http://localhost:5174" })
public class NtcController {

	private final NtcService ntcService;

	// 관리자 권한 확인
	private boolean isAdmin(Authentication authentication) {

		if (authentication == null) {
			return false;
		}

		return authentication.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
	}

	// 공지사항 목록 조회
	// = 관리자: use_yn Y/N 전체 조회
	// = 일반회원 / 비로그인: use_yn = 'Y'만 조회
	// 공지사항 목록 조회
	// = 관리자: use_yn Y/N 전체 조회
	// = 일반회원 / 비로그인: use_yn = 'Y'만 조회
	@GetMapping("/notices")
	public ResponseEntity<?> ntcList(Authentication authentication) {

		try {
			List<Ntc> result;

			if (isAdmin(authentication)) {
				result = ntcService.allNtcList();
			} else {
				result = ntcService.useYNtcList('Y');
			}

			return ResponseEntity.status(HttpStatus.OK).body(result);

		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("목록조회실패");
		}
	}

	// 공지사항 상세 조회
	// = 관리자: use_yn Y/N 모두 상세 조회 가능
	// = 일반회원 / 비로그인: use_yn = 'Y'만 상세 조회 가능
	@GetMapping("/notices/{ntcId}")
	public ResponseEntity<?> ntcDetail(@PathVariable("ntcId") Long ntcId, Authentication authentication) {

		try {

			Ntc entity = ntcService.findOne(ntcId);

			if (!isAdmin(authentication) && entity.getUseYn() == 'N') {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body("비공개 공지사항입니다.");
			}

			Ntc updatedEntity = ntcService.selectOne(ntcId);

			return ResponseEntity.status(HttpStatus.OK).body(updatedEntity);

		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("해당하는 번호는 존재하지 않습니다.");
		}
	}

	// 공지사항 등록
	// = 관리자만 가능
	@PostMapping("/notices")
	public ResponseEntity<?> insert(@RequestBody Ntc entity, Authentication authentication) {

		try {
			if (!isAdmin(authentication)) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body("관리자만 이용할 수 있습니다.");
			}

			Ntc result = ntcService.createNtc(entity);

			return ResponseEntity.status(HttpStatus.OK).body(result);

		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("입력오류입니다.");
		}
	}

	// 공지사항 수정
	// = 관리자만 가능
	@PatchMapping("/notices/{ntcId}")
	public ResponseEntity<?> update(@PathVariable("ntcId") Long ntcId, @RequestBody Ntc entity,
			Authentication authentication) {

		try {
			if (!isAdmin(authentication)) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body("관리자만 이용할 수 있습니다.");
			}

			Ntc result = ntcService.updateNtc(ntcId, entity);

			return ResponseEntity.status(HttpStatus.OK).body(result);

		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("수정실패");
		}
	}

	// 공지사항 삭제
	// = 관리자만 가능
	@DeleteMapping("/notices/{ntcId}")
	public ResponseEntity<?> deleteNotice(@PathVariable("ntcId") Long ntcId, Authentication authentication) {

		try {
			if (!isAdmin(authentication)) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body("관리자만 이용할 수 있습니다.");
			}

			ntcService.deleteNtc(ntcId);

			return ResponseEntity.ok("공지사항 삭제 성공");

		} catch (Exception e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
}