package com.vita.vitapickBack.admin.dto;

import lombok.Data;

@Data
// 관리자 1:1 문의 답변 저장 요청 DTO
public class AdminCsInquiryAnswerRequestDTO {

    // 답변 내용
    private String ansTxt;
}
