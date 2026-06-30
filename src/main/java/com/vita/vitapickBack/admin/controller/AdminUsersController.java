package com.vita.vitapickBack.admin.controller;

// 관리자 회원 엑셀 다운로드
import java.io.IOException;
import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vita.vitapickBack.admin.dto.AdminUserDetailDTO;
import com.vita.vitapickBack.admin.dto.AdminUsersResponseDTO;
import com.vita.vitapickBack.admin.service.AdminUsersService;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/users")
public class AdminUsersController {

	private final AdminUsersService adminUsersService;

	// 관리자 회원 목록 조회 API
	@GetMapping
	public ResponseEntity<AdminUsersResponseDTO> getUsers(
			@RequestParam(name = "page", defaultValue = "0") int page,
			@RequestParam(name = "size", defaultValue = "10") int size,
			@RequestParam(name = "keyword", required = false) String keyword,
			@RequestParam(name = "statusCd", required = false) String statusCd,
			@RequestParam(name = "roleCd", required = false) String roleCd,
			@RequestParam(name = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
			@RequestParam(name = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

		return ResponseEntity.ok(adminUsersService.getUsers(page, size, keyword, statusCd, startDate, endDate));
	}

	// 관리자 회원 엑셀 다운로드 API
	@GetMapping("/{userNum}")
	public ResponseEntity<AdminUserDetailDTO> getUserDetail(@PathVariable("userNum") Long userNum) {
		return ResponseEntity.ok(adminUsersService.getUserDetail(userNum));
	}

	@GetMapping("/excel")
	public void downloadUsersExcel(
			@RequestParam(name = "keyword", required = false) String keyword,
			@RequestParam(name = "statusCd", required = false) String statusCd,
			@RequestParam(name = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
			@RequestParam(name = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
			HttpServletResponse response) throws IOException {

		adminUsersService.downloadUsersExcel(keyword, statusCd, startDate, endDate, response);
	}
}
