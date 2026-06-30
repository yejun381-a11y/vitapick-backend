package com.vita.vitapickBack.order;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrdItRepository extends JpaRepository<OrdIt, Long> {

	List<OrdIt> findByOrdId(Long ordId);

	List<OrdIt> findByPrdId(Long prdId);
	
	// 특정 회원이 특정 상품에 대해 리뷰 작성 가능한 주문 항목 ID 조회
	@Query(value = """
	        SELECT oi.ord_it_id
	        FROM ord o
	        JOIN ord_it oi
	            ON o.ord_id = oi.ord_id
	        WHERE o.user_num = :userNum
	          AND oi.prd_id = :prdId
	          AND o.ord_st_cd = 'PAID'
	        ORDER BY o.crt_at DESC
	        LIMIT 1
	        """, nativeQuery = true)
	Optional<Long> findWritableOrdItId(
	        @Param("userNum") Long userNum,
	        @Param("prdId") Long prdId
	);
	
	// 상품별 판매량 기준 상위 5개 상품 ID, 상품명, 썸네일 이미지 URL, 판매량 조회
	

	// 주문상품 수량 기준 TOP5 상품 조회
	@Query(value = """
			SELECT
			    oi.prd_id,
			    oi.prd_nm,
			    pi.img_url,
			    SUM(oi.it_qty) total_qty
			FROM ord_it oi
			JOIN prd_img pi
			    ON oi.prd_id = pi.prd_id
			WHERE pi.img_type_cd = 'THUMB'
			GROUP BY oi.prd_id, oi.prd_nm, pi.img_url
			ORDER BY total_qty DESC
			LIMIT 5
			""", nativeQuery = true)
	List<Object[]> findTopProducts();
	
	// 결제완료 주문 기준 베스트 상품 ID TOP10 조회
	@Query(value = """
	        SELECT
	            oi.prd_id
	        FROM ord o
	        JOIN ord_it oi
	            ON o.ord_id = oi.ord_id
	        WHERE o.ord_st_cd = 'PAID'
	        GROUP BY oi.prd_id
	        ORDER BY SUM(oi.it_qty) DESC, oi.prd_id ASC
	        LIMIT 10
	        """, nativeQuery = true)
	List<Long> findBestProductIdsTop10();

}
