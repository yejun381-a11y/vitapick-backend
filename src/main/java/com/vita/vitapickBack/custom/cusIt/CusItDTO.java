package com.vita.vitapickBack.custom.cusIt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CusItDTO {

	private Long cusItId;
	private Long prdId;
	private Integer sortNum;    // 추천 노출 순서 1~5
	private String surTitle;
	
	// prd 테이블에서 조회한 상품 상세
	private String prdName;
	private String prdImg;
	private Integer prdPrice;
}