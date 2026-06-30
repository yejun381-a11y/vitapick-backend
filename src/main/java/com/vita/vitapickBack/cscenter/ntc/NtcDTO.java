package com.vita.vitapickBack.cscenter.ntc;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder

public class NtcDTO {
	private Long ntc_id;
	private String ttl;
	private String ntc_txt;
	private Integer view_cnt;
	private Character use_yn;
	private LocalDateTime crt_at;
	private LocalDateTime upd_at;
}
