package com.vita.vitapickBack.admin.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
// 관리자 대시보드 요약 응답 DTO
public class DashboardSummaryDTO {

    private Long todaySalesAmt;
    private Long monthSalesAmt;
    private Long todayPaidOrderCount;
    private PopularCategoryDTO popularCategory;
    private List<ProductSalesTopDTO> productSalesTop5;
    private InquiryStatsDTO inquiryStats;
    private MemberStatsDTO memberStats;
    private List<MonthlyCountDTO> monthlyNewUsers;
    private List<MonthlyCountDTO> monthlyPaidOrders;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    // 관리자 대시보드 인기 카테고리 응답 DTO
    public static class PopularCategoryDTO {
        private Integer catCd;
        private String catNm;
        private Long salesAmt;
        private Long orderQty;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    // 관리자 대시보드 상품 매출 TOP 응답 DTO
    public static class ProductSalesTopDTO {
        private Long prdId;
        private String prdNm;
        private Integer catCd;
        private String catNm;
        private Long paidQty;
        private Long salesAmt;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    // 관리자 대시보드 1:1 문의 통계 응답 DTO
    public static class InquiryStatsDTO {
        private Long waitingCount;
        private Long answeredCount;
        private Long todayNewCount;
        private Double answerRate;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    // 관리자 대시보드 회원 통계 응답 DTO
    public static class MemberStatsDTO {
        private Long totalCount;
        private Long activeCount;
        private Long withdrawnCount;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    // 관리자 대시보드 월별 건수 응답 DTO
    public static class MonthlyCountDTO {
        private String month;
        private Long count;
    }
}
