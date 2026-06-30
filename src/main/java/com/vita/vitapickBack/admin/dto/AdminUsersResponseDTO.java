package com.vita.vitapickBack.admin.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.IOException;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
// 관리자 회원 목록 응답 DTO
public class AdminUsersResponseDTO {

	private List<AdminUserDTO> content;
	private int page;
	private int size;
	private long totalElements;
	private int totalPages;

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	// 관리자 회원 목록 항목 DTO
	public static class AdminUserDTO {
		private Long userNum;
		private String loginId;
		private String userNm;
		private String tel;
		private String statusCd;
		private String roleCd;
		private LocalDateTime crtAt;
		private LocalDateTime updAt;
		private LocalDateTime wdDt;
	}
}
