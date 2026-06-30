package com.vita.vitapickBack.cscenter.ntc;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NtcRepository extends JpaRepository<Ntc, Long> {

	// JpaRepository 기본 제공 메서드

	// 전체 공지사항 목록 조회
	// = 관리자 공지사항 목록 조회
	// = use_yn Y/N 전체 조회
	// SELECT * FROM ntc;
	// findAll()

	// 공지사항 상세 조회
	// SELECT * FROM ntc WHERE ntc_id = ?;
	// findById()

	// 공지사항 등록 / 수정
	// INSERT / UPDATE 처리
	// save()

	// 공지사항 번호(ntcId) 기준 삭제
	// DELETE FROM ntc WHERE ntc_id = ?;
	// deleteById()

	// use_yn = 'Y' 공지사항 목록 조회
	// = 일반회원 공지사항 목록 조회
	// = 공개 공지사항만 조회
	// SELECT * FROM ntc WHERE use_yn = 'Y';
	List<Ntc> findByUseYn(Character useYn);

	Page<Ntc> findByUseYn(Character useYn, Pageable pageable);

}
