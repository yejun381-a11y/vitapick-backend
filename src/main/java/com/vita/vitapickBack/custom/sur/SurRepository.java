package com.vita.vitapickBack.custom.sur;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SurRepository extends JpaRepository<Sur, Long> {

	// userNum으로 설문 목록 조회 (내 설문 리스트)
	List<Sur>findByUserNumOrderBySurIdDesc(Long userNum);
	
}
