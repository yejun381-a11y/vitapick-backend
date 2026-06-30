package com.vita.vitapickBack.admin.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vita.vitapickBack.order.OrdIt;

@Repository
public interface AdminOrdItRepository extends JpaRepository<OrdIt, Long> {

    // 관리자 대시보드 월별 인기 카테고리 매출을 집계한다.
    @Query(value = """
            SELECT
                p.cat_cd,
                COALESCE(SUM(oi.it_amt), 0) AS sales_amt,
                COALESCE(SUM(oi.it_qty), 0) AS total_qty
            FROM ord o
            JOIN ord_it oi
                ON o.ord_id = oi.ord_id
            JOIN prd p
                ON oi.prd_id = p.prd_id
            WHERE o.ord_st_cd = 'PAID'
                AND o.crt_at >= :startAt
                AND o.crt_at < :endAt
            GROUP BY p.cat_cd
            ORDER BY sales_amt DESC
            LIMIT 1
            """, nativeQuery = true)
    List<Object[]> findPopularCategorySales(
            @Param("startAt") LocalDateTime startAt,
            @Param("endAt") LocalDateTime endAt);

    // 관리자 대시보드 월별 상품 매출 TOP 5를 집계한다.
    @Query(value = """
            SELECT
                oi.prd_id,
                COALESCE(oi.prd_nm, p.prd_nm) AS prd_nm,
                p.cat_cd,
                COALESCE(SUM(oi.it_qty), 0) AS paid_qty,
                COALESCE(SUM(oi.it_amt), 0) AS sales_amt
            FROM ord o
            JOIN ord_it oi
                ON o.ord_id = oi.ord_id
            JOIN prd p
                ON oi.prd_id = p.prd_id
            WHERE o.ord_st_cd = 'PAID'
                AND o.crt_at >= :startAt
                AND o.crt_at < :endAt
            GROUP BY oi.prd_id, COALESCE(oi.prd_nm, p.prd_nm), p.cat_cd
            ORDER BY sales_amt DESC, paid_qty DESC, oi.prd_id ASC
            LIMIT 5
            """, nativeQuery = true)
    List<Object[]> findMonthlyProductSalesTop5(
            @Param("startAt") LocalDateTime startAt,
            @Param("endAt") LocalDateTime endAt);

    @Query("""
            SELECT oi.prdNm
            FROM OrdIt oi
            WHERE oi.ordId = :ordId
            ORDER BY oi.ordItId ASC
            """)
    List<String> findProductNamesByOrdId(@Param("ordId") Long ordId);
}
