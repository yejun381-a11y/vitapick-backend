package com.vita.vitapickBack.admin.service;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vita.vitapickBack.admin.dto.AdminUsersResponseDTO;
import com.vita.vitapickBack.admin.dto.AdminUsersResponseDTO.AdminUserDTO;
import com.vita.vitapickBack.admin.dto.AdminUserDetailDTO;
import com.vita.vitapickBack.admin.dto.AdminUserDetailDTO.AdminUserPurchaseSummaryDTO;
import com.vita.vitapickBack.admin.dto.AdminUserDetailDTO.AdminUserRecentOrderDTO;
import com.vita.vitapickBack.admin.repository.AdminOrdRepository;
import com.vita.vitapickBack.admin.repository.AdminUsersRepository;
import com.vita.vitapickBack.order.Ord;
import com.vita.vitapickBack.users.Users;

import lombok.RequiredArgsConstructor;

// 엑셀 다운로드 처리
import java.io.IOException;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import jakarta.servlet.http.HttpServletResponse;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminUsersService {

	private static final String PAID = "PAID";

	private final AdminUsersRepository adminUsersRepository;
	private final AdminOrdRepository adminOrdRepository;

	// 관리자 회원 목록 조회 조건을 처리한다.
	public AdminUsersResponseDTO getUsers(int page, int size, String keyword, String statusCd, LocalDate startDate,
			LocalDate endDate) {

		int safePage = Math.max(page, 0);
		int safeSize = size <= 0 ? 10 : Math.min(size, 100);
		Pageable pageable = PageRequest.of(safePage, safeSize, Sort.by(Sort.Direction.DESC, "crtAt"));
		LocalDateTime startAt = startDate == null ? null : startDate.atStartOfDay();
		LocalDateTime endAt = endDate == null ? null : endDate.plusDays(1).atStartOfDay();

		Page<Users> usersPage = adminUsersRepository.findAdminUsers(keyword, statusCd, startAt, endAt, pageable);

		return AdminUsersResponseDTO.builder()
				.content(usersPage.getContent().stream().map(this::toAdminUserDTO).toList()).page(usersPage.getNumber())
				.size(usersPage.getSize()).totalElements(usersPage.getTotalElements())
				.totalPages(usersPage.getTotalPages()).build();
	}

	// 관리자 회원 응답 DTO로 변환한다.
	private AdminUserDTO toAdminUserDTO(Users users) {
		return AdminUserDTO.builder().userNum(users.getUserNum()).loginId(users.getLoginId()).userNm(users.getUserNm())
				.tel(users.getTel()).statusCd(users.getStatusCd()).roleCd(users.getRoleCd()).crtAt(users.getCrtAt())
				.updAt(users.getUpdAt()).wdDt(users.getWdDt()).build();
	}

	// 관리자 회원 목록 엑셀 다운로드를 처리한다.
	public AdminUserDetailDTO getUserDetail(Long userNum) {
		Users users = adminUsersRepository.findById(userNum)
				.orElseThrow(() -> new IllegalArgumentException("User not found"));
		Long paidOrderCount = adminOrdRepository.countByUserNumAndOrdStCd(userNum, PAID);
		Long totalPaidAmount = adminOrdRepository.sumTotalAmtByUserNumAndOrdStCd(userNum, PAID);
		Pageable recentOrdersPageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "crtAt"));
		List<AdminUserRecentOrderDTO> recentOrders = adminOrdRepository
				.findRecentOrdersByUserNumAndOrdStCd(userNum, PAID, recentOrdersPageable).stream()
				.map(this::toAdminUserRecentOrderDTO)
				.toList();

		return AdminUserDetailDTO.builder()
				.userNum(users.getUserNum())
				.loginId(users.getLoginId())
				.userNm(users.getUserNm())
				.genderCd(users.getGenderCd())
				.email(users.getEmail())
				.birthYmd(users.getBirthYmd())
				.crtAt(users.getCrtAt())
				.roleCd(users.getRoleCd())
				.statusCd(users.getStatusCd())
				.tel(users.getTel())
				.purchaseSummary(AdminUserPurchaseSummaryDTO.builder()
						.paidOrderCount(paidOrderCount == null ? 0L : paidOrderCount)
						.totalPaidAmount(totalPaidAmount == null ? 0L : totalPaidAmount)
						.build())
				.recentOrders(recentOrders)
				.build();
	}

	private AdminUserRecentOrderDTO toAdminUserRecentOrderDTO(Ord ord) {
		return AdminUserRecentOrderDTO.builder()
				.orderId(ord.getOrdId())
				.orderNo(ord.getOrdNo())
				.orderDate(ord.getCrtAt())
				.payMthdCd(adminOrdRepository.findPayMthdCdByOrdId(ord.getOrdId()))
				.orderStatus(ord.getOrdStCd())
				.totalAmount(ord.getTotalAmt())
				.build();
	}

	public void downloadUsersExcel(String keyword, String statusCd, LocalDate startDate, LocalDate endDate,
			HttpServletResponse response) throws IOException {

		LocalDateTime startAt = startDate == null ? null : startDate.atStartOfDay();
		LocalDateTime endAt = endDate == null ? null : endDate.plusDays(1).atStartOfDay();

		List<Users> users = adminUsersRepository.findAdminUsersForExcel(keyword, statusCd, startAt, endAt);

		Workbook workbook = new XSSFWorkbook();
		Sheet sheet = workbook.createSheet("회원목록");

		// Header
		Row header = sheet.createRow(0);

		header.createCell(0).setCellValue("회원번호");
		header.createCell(1).setCellValue("아이디");
		header.createCell(2).setCellValue("이름");
		header.createCell(3).setCellValue("연락처");
		header.createCell(4).setCellValue("상태");
		header.createCell(5).setCellValue("권한");
		header.createCell(6).setCellValue("가입일");
		header.createCell(7).setCellValue("수정일");
		header.createCell(8).setCellValue("탈퇴일");

		int rowNum = 1;

		for (Users user : users) {

			Row row = sheet.createRow(rowNum++);

			row.createCell(0).setCellValue(user.getUserNum());
			row.createCell(1).setCellValue(user.getLoginId());
			row.createCell(2).setCellValue(user.getUserNm());
			row.createCell(3).setCellValue(user.getTel());
			row.createCell(4).setCellValue(user.getStatusCd());
			row.createCell(5).setCellValue(user.getRoleCd());

			row.createCell(6).setCellValue(user.getCrtAt() == null ? "" : user.getCrtAt().toString());

			row.createCell(7).setCellValue(user.getUpdAt() == null ? "" : user.getUpdAt().toString());

			row.createCell(8).setCellValue(user.getWdDt() == null ? "" : user.getWdDt().toString());
		}

		response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

		response.setHeader("Content-Disposition", "attachment; filename=users.xlsx");

		workbook.write(response.getOutputStream());
		workbook.close();
	}

}
