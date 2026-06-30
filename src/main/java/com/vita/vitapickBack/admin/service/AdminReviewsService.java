package com.vita.vitapickBack.admin.service;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.vita.vitapickBack.admin.dto.AdminReviewsResponseDTO;
import com.vita.vitapickBack.admin.dto.AdminReviewsResponseDTO.AdminReviewDTO;
import com.vita.vitapickBack.admin.dto.AdminReviewsResponseDTO.AdminReviewReplyRequestDTO;
import com.vita.vitapickBack.admin.repository.AdminRvwRepository;
import com.vita.vitapickBack.products.prd.Prd;
import com.vita.vitapickBack.products.prd.PrdRepository;
import com.vita.vitapickBack.products.rvw.Rvw;
import com.vita.vitapickBack.users.Users;
import com.vita.vitapickBack.users.UsersRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminReviewsService {

    private final AdminRvwRepository adminRvwRepository;
    private final UsersRepository usersRepository;
    private final PrdRepository prdRepository;

    // 관리자 리뷰 목록 조회 조건을 처리한다.
    public AdminReviewsResponseDTO getReviews(
            int page,
            int size,
            String keyword,
            Integer rating,
            LocalDate startDate,
            LocalDate endDate) {

        int safePage = Math.max(page, 0);
        int safeSize = size <= 0 ? 10 : Math.min(size, 100);
        Pageable pageable = PageRequest.of(safePage, safeSize, Sort.by(Sort.Direction.DESC, "crtAt"));

        LocalDateTime startAt = startDate == null ? null : startDate.atStartOfDay();
        LocalDateTime endAt = endDate == null ? null : endDate.plusDays(1).atStartOfDay();

        Page<Rvw> reviewsPage = adminRvwRepository.findAdminReviews(keyword, rating, startAt, endAt, pageable);

        return AdminReviewsResponseDTO.builder()
                .content(reviewsPage.getContent().stream()
                        .map(this::toAdminReviewDTO)
                        .toList())
                .page(reviewsPage.getNumber())
                .size(reviewsPage.getSize())
                .totalElements(reviewsPage.getTotalElements())
                .totalPages(reviewsPage.getTotalPages())
                .build();
    }

    // 관리자 리뷰 상세 정보를 조회한다.
    public AdminReviewDTO getReview(Long rvwId) {
        return toAdminReviewDTO(findReview(rvwId));
    }

    // 관리자 리뷰 답글을 저장한다.
    @Transactional
    public AdminReviewDTO saveReply(Long rvwId, AdminReviewReplyRequestDTO request) {
        Rvw rvw = findReview(rvwId);
        rvw.setReplyTxt(request == null ? null : request.getReplyTxt());
        rvw.setReplyAt(LocalDateTime.now());
        return toAdminReviewDTO(rvw);
    }

    // 관리자 리뷰 답글을 삭제한다.
    @Transactional
    public AdminReviewDTO deleteReply(Long rvwId) {
        Rvw rvw = findReview(rvwId);
        rvw.setReplyTxt(null);
        rvw.setReplyAt(null);
        return toAdminReviewDTO(rvw);
    }

    // 관리자 리뷰 단건을 조회한다.
    private Rvw findReview(Long rvwId) {
        return adminRvwRepository.findById(rvwId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Review not found"));
    }

    // 관리자 리뷰 응답 DTO로 변환한다.
    private AdminReviewDTO toAdminReviewDTO(Rvw rvw) {
        Users writer = usersRepository.findById(rvw.getUserNum()).orElse(null);
        Prd product = prdRepository.findById(rvw.getPrdId()).orElse(null);

        return AdminReviewDTO.builder()
                .reviewId(rvw.getRvwId())
                .productId(rvw.getPrdId())
                .productName(product == null ? null : product.getPrdNm())
                .writerId(writer == null ? null : writer.getLoginId())
                .writerName(writer == null ? null : writer.getUserNm())
                .rating(rvw.getRating())
                .content(rvw.getCmt())
                .replyTxt(rvw.getReplyTxt())
                .useYn(rvw.getUseYn())
                .createdAt(rvw.getCrtAt())
                .updatedAt(rvw.getUpdAt())
                .build();
    }
}
