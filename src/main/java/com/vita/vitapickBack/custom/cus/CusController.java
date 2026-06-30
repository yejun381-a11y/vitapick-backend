package com.vita.vitapickBack.custom.cus;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
@RequestMapping("/api/v1/cus")
@RequiredArgsConstructor
public class CusController {

	private final CusService cusService;

	// AI 추천 실행
	// POST /v1/cus/recommend?surId=1&userNum=1
	@PostMapping(value="/recommend", consumes=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> recommend(
			@RequestBody CusDTO cusDTO,
			@AuthenticationPrincipal Long userNum) {
		try {
			CusDTO result = cusService.recommend(cusDTO.getSurId(), userNum);
			log.info("** AI 추천 완료 surId={} userNum={}", cusDTO.getSurId(), userNum);
			return ResponseEntity.status(HttpStatus.OK).body(result);
		} catch (Exception e) {
			log.error("** AI 추천 실패 => {}", e.toString());
			return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
					.body("AI 추천 실패 => " + e.getMessage());
		}
	}

	// 추천 결과 조회(cusresult)
	// GET /v1/cus/detail/{cusId}
	@GetMapping("/detail/{cusId}")
	public ResponseEntity<?> getCusDetail(@PathVariable("cusId") Long cusId, @AuthenticationPrincipal Long userNum) {
		try {
			CusDTO result = cusService.getCusDetail(cusId, userNum);
			log.info("** 추천 상세 조회 성공 cusId={}", cusId);
			return ResponseEntity.status(HttpStatus.OK).body(result);
		} catch (Exception e) {
			log.error("** 추천 상세 조회 실패 => {}", e.toString());
			return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
					.body("추천 상세 조회 실패 => " + e.getMessage());
		}
	}

	// 내 추천 목록 조회(mypage)
	// GET /v1/cus/list/{userNum}
	@GetMapping("/list/")
	public ResponseEntity<?> getCusList(@AuthenticationPrincipal Long userNum) {
		try {
			List<CusDTO> result = cusService.getCusList(userNum);
			log.info("** 추천 목록 조회 성공 userNum={}", userNum);
			return ResponseEntity.status(HttpStatus.OK).body(result);
		} catch (Exception e) {
			log.error("** 추천 목록 조회 실패 => {}", e.toString());
			return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
					.body("추천 목록 조회 실패 => " + e.getMessage());
		}
	}
	
	// 추천 삭제
	// delete v1/cus/delete/{cusId}
	@DeleteMapping("delete/{cusId}")
	public ResponseEntity<?> deleteCus(@PathVariable("cusId") Long cusId, @AuthenticationPrincipal Long userNum){
		try {
			cusService.deleteCus(cusId, userNum);
			log.info("추천삭제성공 cusId=", cusId);
			return ResponseEntity.status(HttpStatus.OK).body("삭제완료");
		}catch(Exception e) {
			log.error("추천삭제실패", e.toString());
			return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("삭제실패"+e.toString());
		}
	}
	
}