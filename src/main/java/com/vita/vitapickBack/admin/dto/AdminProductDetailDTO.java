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
public class AdminProductDetailDTO {

    private Long prdId;
    private String prdNm;
    private String brand;
    private Integer catCd;
    private Integer price;
    private String descTxt;
    private String dosTxt;
    private String warnTxt;
    private String useYn;
    private LocalDateTime crtAt;
    private LocalDateTime updAt;
    private String ingr;
}
