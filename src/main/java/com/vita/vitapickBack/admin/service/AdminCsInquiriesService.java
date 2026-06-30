package com.vita.vitapickBack.admin.service;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vita.vitapickBack.admin.dto.AdminCsInquiriesResponseDTO;
import com.vita.vitapickBack.admin.dto.AdminCsInquiriesResponseDTO.AdminCsInquiryDTO;
import com.vita.vitapickBack.admin.repository.AdminInqRepository;
import com.vita.vitapickBack.cscenter.inq.Inq;
import com.vita.vitapickBack.users.Users;
import com.vita.vitapickBack.users.UsersRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminCsInquiriesService {

    private final AdminInqRepository adminInqRepository;
    private final UsersRepository usersRepository;

    // 관리자 1:1 문의 목록 조회 조건을 처리한다.
    public AdminCsInquiriesResponseDTO getInquiries(
            int page,
            int size,
            String keyword,
            String status,
            String type,
            LocalDate startDate,
            LocalDate endDate) {

        int safePage = Math.max(page, 0);
        int safeSize = size <= 0 ? 10 : Math.min(size, 100);
        Pageable pageable = PageRequest.of(safePage, safeSize);

        LocalDateTime startAt = startDate == null ? null : startDate.atStartOfDay();
        LocalDateTime endAt = endDate == null ? null : endDate.plusDays(1).atStartOfDay();

        Page<Inq> inquiriesPage = adminInqRepository.findAdminInquiries(keyword, status, type, startAt, endAt, pageable);

        return AdminCsInquiriesResponseDTO.builder()
                .content(inquiriesPage.getContent().stream()
                        .map(this::toAdminCsInquiryDTO)
                        .toList())
                .page(inquiriesPage.getNumber())
                .size(inquiriesPage.getSize())
                .totalElements(inquiriesPage.getTotalElements())
                .totalPages(inquiriesPage.getTotalPages())
                .build();
    }

    // 관리자 1:1 문의 목록 응답 DTO로 변환한다.
    private AdminCsInquiryDTO toAdminCsInquiryDTO(Inq inq) {
        Users writer = usersRepository.findById(inq.getUserNum()).orElse(null);

        return AdminCsInquiryDTO.builder()
                .inquiryId(inq.getInqId())
                .title(inq.getTtl())
                .writerId(writer == null ? null : writer.getLoginId())
                .writerName(writer == null ? null : writer.getUserNm())
                .category(inq.getInqTpCd())
                .status(inq.getInqStCd())
                .viewCnt(inq.getViewCnt())
                .answeredAt(inq.getAnsAt())
                .createdAt(inq.getCrtAt())
                .updatedAt(inq.getUpdAt())
                .build();
    }
}
