package com.vita.vitapickBack.cscenter.inq;

import java.util.List;

public interface InqService {

	// 전체 1:1 문의 목록 조회
	List<Inq> getAllInq();

	// 회원 본인 문의 목록 조회 (마이페이지)
	List<Inq> getMyInq(Long userNum);

	// 관리자 문의 상세 조회
	Inq selectOne(Long inqId);

	// 회원 본인 문의 상세 조회
	Inq selectMyOne(Long inqId, Long userNum);

	// 문의 등록
	Inq createInq(Inq inq, Long userNum);

	// 회원 본인 문의 수정
	Inq updateInq(Long inqId, Long userNum, Inq inq);

	// 회원 본인 문의 삭제
	void deleteInq(Long inqId, Long userNum);

	// 관리자 답변 등록
	Inq answerInq(Long inqId, String ansTxt);

}