package com.vita.vitapickBack.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class OrdItDTO {

	private Long ordItId;
	private Long ordId;
	private Long prdId;
	// 커스텀 상품 묶음 번호
	private Long cusId;
	private String prdNm;
	private Integer itQty;
	private Integer price;
	private Integer itAmt;
	
	// 주문 상품 이미지
	private String thumbImgUrl;
	
	// 상품별 판매량 집계
	private Long totalQty;
	
}