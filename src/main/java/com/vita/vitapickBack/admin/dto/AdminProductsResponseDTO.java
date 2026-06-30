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
// 관리자 상품 목록 응답 DTO
public class AdminProductsResponseDTO {

    private List<AdminProductDTO> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    // 관리자 상품 목록 항목 DTO
    public static class AdminProductDTO {
        private Long prdId;
        private String prdNm;
        private String brand;
        private Integer catCd;
        private String categoryName;
        private Integer price;
        private String useYn;
        private String thumbImgUrl;
        private LocalDateTime crtAt;
        private LocalDateTime updAt;
        private LocalDateTime wdAt;
    }
}
