package com.vita.vitapickBack.cscenter.inq;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder

public class InqDTO {
	
	private Long inqId;
	private Long userNum;
	private String inqTpCd;
	private String inqStCd;
	private String ttl;
	private String inqTxt;
	private String ansTxt;
	private LocalDateTime ansAt;
	private Integer viewCnt;
	private LocalDateTime crtAt;
	private LocalDateTime updAt;

}
