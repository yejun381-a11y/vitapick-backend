package com.vita.vitapickBack.useraddr;

import java.util.List;

public interface UserAddrService {

	// 회원 배송지 목록 조회
	List<UserAddr> findByUserNum(Long userNum);

	// 기본 배송지 조회
	UserAddr getBaseAddr(Long userNum);

	// 배송지 등록
	UserAddr createAddr(Long userNum, UserAddrDTO dto);

	// 배송지 수정
	UserAddr updateAddr(Long userNum, Long addrId, UserAddrDTO dto);

	// 배송지 삭제
	void deleteAddr(Long userNum, Long addrId);

	// 기본 배송지 변경
	void updateBaseAddr(Long userNum, Long addrId);
}