package com.vita.vitapickBack.custom.cusIt;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CusItRepository extends JpaRepository<CusIt, Long> {

	// cusId로 추천 아이템 목록 조회 (순서 오름차순)
	List<CusIt> findByCusIdOrderBySortNumAsc(Long cusId);
}