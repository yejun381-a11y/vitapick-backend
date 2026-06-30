package com.vita.vitapickBack.order;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class PayDTO {

    private Long payId;
    private Long ordId;
    private String payNo;
    private String payMthdCd;
    private String payStCd;
    private Integer payAmt;
    private LocalDateTime paidAt;
    private LocalDateTime crtAt;
    private LocalDateTime updAt;

}