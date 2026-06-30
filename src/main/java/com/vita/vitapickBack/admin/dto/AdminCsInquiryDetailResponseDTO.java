package com.vita.vitapickBack.admin.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
// 관리자 1:1 문의 상세 응답 DTO
public class AdminCsInquiryDetailResponseDTO {

    private Long inquiryId;
    private Long userNum;
    private String writerId;
    private String writerName;
    private String title;
    private String category;
    private String status;
    private String inquiryText;
    private String ansTxt;
    private LocalDateTime answeredAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
