package com.vita.vitapickBack.admin.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vita.vitapickBack.order.Ord;

@Repository
public interface AdminOrdRepository extends JpaRepository<Ord, Long> {

    // 관리자 결제완료 주문 목록을 조건별로 조회한다.
    @Query("""
            SELECT DISTINCT o
            FROM Ord o
            WHERE (:keyword IS NULL OR :keyword = ''
                   OR LOWER(o.ordNo) LIKE LOWER(CONCAT('%', :keyword, '%'))
                   OR EXISTS (
                       SELECT 1
                       FROM Users u
                       WHERE u.userNum = o.userNum
                         AND (LOWER(u.loginId) LIKE LOWER(CONCAT('%', :keyword, '%'))
                              OR LOWER(u.userNm) LIKE LOWER(CONCAT('%', :keyword, '%')))
                   )
                   OR EXISTS (
                       SELECT 1
                       FROM OrdIt oi
                       WHERE oi.ordId = o.ordId
                         AND LOWER(oi.prdNm) LIKE LOWER(CONCAT('%', :keyword, '%'))
                   ))
              AND o.ordStCd = 'PAID'
              AND (:categoryId IS NULL OR EXISTS (
                  SELECT 1
                  FROM OrdIt oi, Prd p
                  WHERE oi.ordId = o.ordId
                    AND p.prdId = oi.prdId
                    AND p.catCd = :categoryId
              ))
              AND (:payMthdCd IS NULL OR :payMthdCd = '' OR EXISTS (
                  SELECT 1
                  FROM Pay py
                  WHERE py.ordId = o.ordId
                    AND py.payMthdCd = :payMthdCd
              ))
              AND (:startAt IS NULL OR o.crtAt >= :startAt)
              AND (:endAt IS NULL OR o.crtAt < :endAt)
            """)
    Page<Ord> findAdminOrders(
            @Param("keyword") String keyword,
            @Param("categoryId") Integer categoryId,
            @Param("payMthdCd") String payMthdCd,
            @Param("startAt") LocalDateTime startAt,
            @Param("endAt") LocalDateTime endAt,
            Pageable pageable);

    // 관리자 주문의 결제수단 코드를 조회한다.
    @Query("""
            SELECT py.payMthdCd
            FROM Pay py
            WHERE py.ordId = :ordId
            """)
    String findPayMthdCdByOrdId(@Param("ordId") Long ordId);

    Long countByUserNumAndOrdStCd(Long userNum, String ordStCd);

    @Query("""
            SELECT COALESCE(SUM(o.totalAmt), 0)
            FROM Ord o
            WHERE o.userNum = :userNum
              AND o.ordStCd = :ordStCd
            """)
    Long sumTotalAmtByUserNumAndOrdStCd(
            @Param("userNum") Long userNum,
            @Param("ordStCd") String ordStCd);

    @Query("""
            SELECT o
            FROM Ord o
            WHERE o.userNum = :userNum
              AND o.ordStCd = :ordStCd
            """)
    List<Ord> findRecentOrdersByUserNumAndOrdStCd(
            @Param("userNum") Long userNum,
            @Param("ordStCd") String ordStCd,
            Pageable pageable);

    // 관리자 대시보드 결제완료 주문 금액을 기간별로 집계한다.
    @Query("""
            SELECT COALESCE(SUM(o.totalAmt), 0)
            FROM Ord o
            WHERE o.ordStCd = :ordStCd
              AND o.crtAt >= :startAt
              AND o.crtAt < :endAt
            """)
    Long sumTotalAmtByOrdStCdAndCrtAtRange(
            @Param("ordStCd") String ordStCd,
            @Param("startAt") LocalDateTime startAt,
            @Param("endAt") LocalDateTime endAt);

    // 관리자 대시보드 월별 결제완료 주문 수를 집계한다.
    @Query(value = """
            SELECT DATE_FORMAT(o.crt_at, '%Y-%m') AS month, COUNT(*) AS count
            FROM ord o
            WHERE o.ord_st_cd = 'PAID'
              AND o.crt_at >= :startAt
              AND o.crt_at < :endAt
            GROUP BY DATE_FORMAT(o.crt_at, '%Y-%m')
            ORDER BY month ASC
            """, nativeQuery = true)
    List<Object[]> findMonthlyPaidOrderCounts(
            @Param("startAt") LocalDateTime startAt,
            @Param("endAt") LocalDateTime endAt);

    // 관리자 대시보드 결제완료 주문 수를 기간별로 집계한다.
    Long countByOrdStCdAndCrtAtGreaterThanEqualAndCrtAtLessThan(
            String ordStCd,
            LocalDateTime startAt,
            LocalDateTime endAt);
}
