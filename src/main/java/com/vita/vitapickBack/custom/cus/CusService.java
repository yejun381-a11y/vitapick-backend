package com.vita.vitapickBack.custom.cus;

import java.util.List;

public interface CusService {

	CusDTO recommend(Long surId, Long userNum);     // 설문 기반 AI 추천 실행 및 저장
	CusDTO getCusDetail(Long cusId, Long userNum);  // 추천 결과 상세 조회
	List<CusDTO> getCusList(Long userNum);          // 내 추천 목록 조회
	void deleteCus(Long cusID, Long userNum);		// 추천 삭제
}