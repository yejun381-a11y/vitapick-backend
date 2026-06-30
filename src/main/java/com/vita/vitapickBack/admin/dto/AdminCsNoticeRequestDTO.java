package com.vita.vitapickBack.admin.dto;

import lombok.Data;

@Data
// 관리자 공지사항 등록/수정 요청 DTO
public class AdminCsNoticeRequestDTO {

    // 공지사항 제목
    private String ttl;
    // 공지사항 내용
    private String ntcTxt;
    // 사용 여부
    private Character useYn;
}
