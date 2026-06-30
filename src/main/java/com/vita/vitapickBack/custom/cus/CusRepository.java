package com.vita.vitapickBack.custom.cus;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CusRepository extends JpaRepository<Cus, Long> {

	// surId로 추천 결과 조회 (1:1 관계)
	Optional<Cus> findBySurId(Long surId);

	// userNum으로 내 추천 목록 조회 (최신순)
	List<Cus> findByUserNumOrderByCusIdDesc(Long userNum);
	
	// cus 삭제
	void deleteByCusId(Long cusId);
}