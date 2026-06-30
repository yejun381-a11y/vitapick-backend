package com.vita.vitapickBack.admin.dto;

import java.time.LocalDate;
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
public class AdminUserDetailDTO {

    private Long userNum;
    private String loginId;
    private String userNm;
    private String genderCd;
    private String email;
    private LocalDate birthYmd;
    private LocalDateTime crtAt;
    private String roleCd;
    private String statusCd;
    private String tel;
    private AdminUserPurchaseSummaryDTO purchaseSummary;
    private List<AdminUserRecentOrderDTO> recentOrders;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AdminUserPurchaseSummaryDTO {
        private Long paidOrderCount;
        private Long totalPaidAmount;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AdminUserRecentOrderDTO {
        private Long orderId;
        private String orderNo;
        private LocalDateTime orderDate;
        private String payMthdCd;
        private String orderStatus;
        private Integer totalAmount;
    }
}
