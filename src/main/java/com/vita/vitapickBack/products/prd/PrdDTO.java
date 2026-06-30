package com.vita.vitapickBack.products.prd;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrdDTO {

	// 상품 정보
    private Long prdId;
    private String prdNm;
    private Integer price;
    private String brand;
    private String descTxt;

    // 성분 정보
    private String ingr;

    // 썸네일 이미지 URL
    private String thumbImgUrl;
    
    // 상세 이미지 URL
    private String detailImgUrl;
}