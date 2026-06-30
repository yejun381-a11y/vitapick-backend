package com.vita.vitapickBack.admin.dto;

import lombok.Data;

@Data
// 관리자 FAQ 수정 요청 DTO
public class AdminFaqUpdateRequestDTO {

    // FAQ 분류 코드
    private String faqCtgCd;
    // FAQ 제목
    private String ttl;
    // FAQ 내용
    private String faqTxt;
    // 사용 여부
    private String useYn;
}
