package com.vita.vitapickBack.admin.repository;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vita.vitapickBack.products.rvw.Rvw;

@Repository
public interface AdminRvwRepository extends JpaRepository<Rvw, Long> {

    // 관리자 리뷰 목록을 조건별로 조회한다.
    @Query("""
            SELECT r
            FROM Rvw r
            WHERE (:keyword IS NULL OR :keyword = ''
                   OR LOWER(r.cmt) LIKE LOWER(CONCAT('%', :keyword, '%'))
                   OR EXISTS (
                       SELECT 1
                       FROM Users u
                       WHERE u.userNum = r.userNum
                         AND (LOWER(u.loginId) LIKE LOWER(CONCAT('%', :keyword, '%'))
                              OR LOWER(u.userNm) LIKE LOWER(CONCAT('%', :keyword, '%')))
                   )
                   OR EXISTS (
                       SELECT 1
                       FROM Prd p
                       WHERE p.prdId = r.prdId
                         AND LOWER(p.prdNm) LIKE LOWER(CONCAT('%', :keyword, '%'))
                   ))
              AND (:rating IS NULL OR r.rating = :rating)
              AND (:startAt IS NULL OR r.crtAt >= :startAt)
              AND (:endAt IS NULL OR r.crtAt < :endAt)
            """)
    Page<Rvw> findAdminReviews(
            @Param("keyword") String keyword,
            @Param("rating") Integer rating,
            @Param("startAt") LocalDateTime startAt,
            @Param("endAt") LocalDateTime endAt,
            Pageable pageable);
}
