package com.vita.vitapickBack.cscenter.ntc;

import java.util.List;

import org.springframework.data.domain.Page;

public interface NtcService {

	// 전체 공지사항 목록 조회
	// = 관리자 공지사항 목록 조회
	// = use_yn Y/N 전체 조회
	List<Ntc> allNtcList();

	// use_yn = 'Y' 공지사항 목록 조회
	// = 일반회원 공지사항 목록 조회
	// = 공개 공지사항만 조회
	List<Ntc> useYNtcList(Character useYn);

	Page<Ntc> findAdminNtcPage(int page, int size, String useYn, String sort);

	// 공지사항 상세 조회
	Ntc selectOne(Long ntcId);

	// 공지사항 단건 조회
	Ntc findOne(Long ntcId);

	// 공지사항 등록(관리자)
	Ntc createNtc(Ntc ntc);

	// 공지사항 수정(관리자)
	Ntc updateNtc(Long ntcId, Ntc ntc);

	// 공지사항 삭제(관리자)
	void deleteNtc(Long ntcId);

}
