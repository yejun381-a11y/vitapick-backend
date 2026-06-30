package com.vita.vitapickBack.custom.cus;

import java.time.LocalDateTime;
import java.util.List;

import com.vita.vitapickBack.custom.cusIt.CusItDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CusDTO {

	private Long cusId;
	private Long surId;
	private String surTitle;
	private Long userNum;
	private String aiModel;
	private String cusSum;        // 커스텀 요약
	private String cusReason;     // 추천 이유
	private String cusDos;        // 복용 가이드
	private String cusCaution;    // 주의 사항
	private LocalDateTime crtAt;
	private List<CusItDTO> items; // 추천 상품 목록
	private String message;       // 처리 결과 메시지
}