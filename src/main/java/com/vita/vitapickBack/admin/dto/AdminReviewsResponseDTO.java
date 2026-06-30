package com.vita.vitapickBack.admin.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
// 관리자 리뷰 목록 응답 DTO
public class AdminReviewsResponseDTO {

    private List<AdminReviewDTO> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    // 관리자 리뷰 목록 항목 DTO
    public static class AdminReviewDTO {
        private Long reviewId;
        private Long productId;
        private String productName;
        private String writerId;
        private String writerName;
        private Integer rating;
        private String content;
        private String replyTxt;
        private String useYn;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    // 관리자 리뷰 답글 저장 요청 DTO
    public static class AdminReviewReplyRequestDTO {
        // 답글 내용
        private String replyTxt;
    }
}
