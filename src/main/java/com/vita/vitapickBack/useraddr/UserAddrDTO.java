package com.vita.vitapickBack.useraddr;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAddrDTO {

	private Long addrId;
	private Long userNum;
	private String addrNm;
	private String rcvNm;
	private String rcvTel;
	private String zipCd;
	private String addr1;
	private String addr2;
	private String baseYn;
}