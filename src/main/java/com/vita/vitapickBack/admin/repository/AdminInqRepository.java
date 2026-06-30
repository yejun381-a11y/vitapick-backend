package com.vita.vitapickBack.admin.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vita.vitapickBack.cscenter.inq.Inq;

@Repository
public interface AdminInqRepository extends JpaRepository<Inq, Long> {

    // 관리자 1:1 문의 단건을 조회한다.
    Optional<Inq> findByInqId(Long inqId);

    // 관리자 1:1 문의 목록을 답변대기 우선으로 조건별 조회한다.
    @Query("""
            SELECT i
            FROM Inq i
            WHERE (:keyword IS NULL OR :keyword = ''
                   OR LOWER(i.ttl) LIKE LOWER(CONCAT('%', :keyword, '%'))
                   OR LOWER(i.inqTxt) LIKE LOWER(CONCAT('%', :keyword, '%'))
                   OR EXISTS (
                       SELECT 1
                       FROM Users u
                       WHERE u.userNum = i.userNum
                         AND (LOWER(u.loginId) LIKE LOWER(CONCAT('%', :keyword, '%'))
                              OR LOWER(u.userNm) LIKE LOWER(CONCAT('%', :keyword, '%')))
                   ))
              AND (:status IS NULL OR :status = '' OR i.inqStCd = :status)
              AND (:type IS NULL OR :type = '' OR i.inqTpCd = :type)
              AND (:startAt IS NULL OR i.crtAt >= :startAt)
              AND (:endAt IS NULL OR i.crtAt < :endAt)
            ORDER BY CASE WHEN i.inqStCd = 'WAITING' THEN 0 ELSE 1 END ASC,
                     i.crtAt DESC
            """)
    Page<Inq> findAdminInquiries(
            @Param("keyword") String keyword,
            @Param("status") String status,
            @Param("type") String type,
            @Param("startAt") LocalDateTime startAt,
            @Param("endAt") LocalDateTime endAt,
            Pageable pageable);

    // 관리자 대시보드 1:1 문의 상태별 수를 집계한다.
    Long countByInqStCd(String inqStCd);

    // 관리자 대시보드 일별 1:1 문의 수를 집계한다.
    Long countByCrtAtGreaterThanEqualAndCrtAtLessThan(LocalDateTime startAt, LocalDateTime endAt);
}
