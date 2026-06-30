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
// 관리자 주문 목록 응답 DTO
public class AdminOrdersResponseDTO {

    private List<AdminOrderDTO> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    // 관리자 주문 목록 항목 DTO
    public static class AdminOrderDTO {
        private Long orderId;
        private String orderNo;
        private String productName;
        private String buyerId;
        private String buyerName;
        private Integer totalPrice;
        private String orderStatus;
        private String payMthdCd;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }
}
