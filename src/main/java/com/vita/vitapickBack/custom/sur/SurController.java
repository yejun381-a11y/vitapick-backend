package com.vita.vitapickBack.custom.sur;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
@RequestMapping("/api/v1/sur")
@RequiredArgsConstructor
public class SurController {

	private final SurService surService;
	
	//저장
	@PostMapping(value = "/save",
            consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> saveSur(@RequestBody SurDTO surDTO) {
        try {
            SurDTO result = surService.saveSur(surDTO);
            log.info("** 설문 저장 성공 => " + surDTO.getUserNum());
            return ResponseEntity.status(HttpStatus.OK).body(result);
        } catch (Exception e) {
            log.error("** 설문 저장 실패 => " + e.toString());
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body("설문 저장 실패 => " + e.getMessage());
        }
	}
	
	//내설문리스트조회
	@GetMapping(value= "/list/{userNum}")
	public ResponseEntity<?> getSurList(@PathVariable Long userNum){
		try {
			List<SurDTO> result = surService.getSurList(userNum);
			log.info("** 설문목록조회 성공 userNum="+userNum);
			return ResponseEntity.status(HttpStatus.OK).body(result);
		} catch(Exception e) {
			log.error("** 설문목록조회 실패 => " + e.toString());
			return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("목록조회 실패="+e.getMessage());
		}
	}
	
	//설문디테일보기
	@GetMapping(value= "/detail/{surId}")
	public ResponseEntity<?> getSurDetail(@PathVariable Long surId){
		try {
			SurDTO result = surService.getSurDetail(surId);
			log.info("** 설문 상세 조회 성공 => surId: " + surId);
			return ResponseEntity.status(HttpStatus.OK).body(result);
		} catch(Exception e){
			log.error("** 설문 상세 조회 실패 => " + e.toString());
			return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("상세 조회 실패 => " + e.getMessage());
		}
	}

}
