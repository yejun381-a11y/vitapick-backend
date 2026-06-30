package com.vita.vitapickBack.cscenter.faq;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FaqRepository extends JpaRepository<Faq, Long> {

	// JpaRepository 기본 제공 메서드

	// 전체 FAQ 목록 조회
	// = 관리자 FAQ 목록 조회
	// = use_yn Y/N 전체 조회
	// SELECT * FROM faq;
	// findAll()

	// FAQ 번호(faqId) 기준 단건 조회
	// = FAQ 상세 조회
	// SELECT * FROM faq WHERE faq_id = ?;
	// findById()

	// FAQ 등록 / 수정
	// INSERT / UPDATE 처리
	// save()

	// FAQ 번호(faqId) 기준 삭제
	// DELETE FROM faq WHERE faq_id = ?;
	// deleteById()

	// FAQ 카테고리별 조회
	// = 카테고리 선택 시 조회
	// SELECT * FROM faq WHERE faq_ctg_cd = ?;
	List<Faq> findByFaqCtgCd(String faqCtgCd);

	// use_yn = 'Y' FAQ 조회
	// = 일반회원 FAQ 목록 조회
	// = 공개 FAQ만 조회
	// SELECT * FROM faq WHERE use_yn = 'Y';
	List<Faq> findByUseYn(String useYn);

	Page<Faq> findByUseYn(String useYn, Pageable pageable);

	// FAQ 카테고리 + use_yn 조회
	// = 일반회원 카테고리별 FAQ 조회
	// = 공개 FAQ만 조회
	// SELECT * FROM faq
	// WHERE faq_ctg_cd = ?
	// AND use_yn = ?;
	List<Faq> findByFaqCtgCdAndUseYn(String faqCtgCd, String useYn);

}
