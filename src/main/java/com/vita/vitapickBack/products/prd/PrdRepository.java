package com.vita.vitapickBack.products.prd;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface PrdRepository extends JpaRepository<Prd, Long> {
	
	// 카테고리별 상품 조회
	List<Prd> findByCatCd(int catCd);
	
	// useYn 에 따라 조회
	List<Prd> findByUseYn(String useYn);
	
	// 카테고리별 최신 상품 조회 (예: 최신 2개)
	List<Prd> findTop2ByCatCdOrderByPrdIdDesc(Long catCd);
	
	// 상품명으로 검색
	@Query("SELECT p FROM Prd p WHERE p.prdNm LIKE %:keyword%")
	List<Prd> searchByKeyword(@Param("keyword") String keyword);
	
}
