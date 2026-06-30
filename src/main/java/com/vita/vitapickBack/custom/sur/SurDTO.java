package com.vita.vitapickBack.custom.sur;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SurDTO {

    private Long surId;      // 응답 시 — 저장된 설문 ID
    private Long userNum;    // 요청 시 — 회원번호
    private String surTitle; // 입력이 들어오면 입력된 값, 아니면 생성날짜
    private String ansJson;  // 요청 시 — 설문 응답 JSON 문자열
    private String message;  // 응답 시 — 처리 결과 메시지
}
