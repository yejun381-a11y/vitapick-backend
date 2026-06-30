package com.vita.vitapickBack.cscenter.faq;

import java.util.List;

import org.springframework.data.domain.Page;

public interface FaqService {

	// FAQ 전체 목록 조회
	// = 관리자 FAQ 목록 조회
	// = use_yn Y/N 전체 조회
	List<Faq> findFaqAll();

	// FAQ 상세 조회
	Faq selectOne(Long faqId);

	// FAQ 단건 조회
	Faq findOne(Long faqId);

	// FAQ 등록(관리자)
	Faq createFaq(FaqDto dto);

	// FAQ 수정(관리자)
	Faq updateFaq(Long faqId, FaqDto dto);

	// FAQ 삭제(관리자)
	void deleteFaq(Long faqId);

	// FAQ 카테고리별 조회
	// = 카테고리 선택 시 조회
	List<Faq> findByFaqCtgCd(String faqCtgCd);

	// use_yn = 'Y' FAQ 조회
	// = 일반회원 FAQ 목록 조회
	// = 공개 FAQ만 조회
	List<Faq> findByUseYn(String useYn);

	Page<Faq> findAdminFaqPage(int page, int size, String useYn, String sort);

	// FAQ 카테고리 + use_yn 조회
	// = 일반회원 카테고리별 FAQ 조회
	// = 공개 FAQ만 조회
	List<Faq> findByFaqCtgCdAndUseYn(String faqCtgCd, String useYn);

}
