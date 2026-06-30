package com.vita.vitapickBack.cscenter.faq;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FaqDto {

	private Long faqId;
	private String faqCtgCd;
	private String ttl;
	private String faqTxt;
	private Integer viewCnt;
	private String useYn;

}