package com.vita.vitapickBack.cscenter.inq;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InqRepository extends JpaRepository<Inq, Long> {

	// JpaRepository 기본 제공 메서드

	// 전체 1:1 문의 목록 조회
	// = 고객센터 1:1 문의 페이지 접근 시 전체 목록 조회
	// = 회원 + 관리자 모두 조회 가능
	// SELECT * FROM inq;
	// findAll()

	// 문의번호(inqId) 기준 단건 조회
	// = 관리자 전체 문의 상세 조회
	// SELECT * FROM inq WHERE inq_id = ?;
	// findById()

	// 1:1 문의 등록 / 수정 / 관리자 답변 등록
	// INSERT / UPDATE 처리
	// save()

	// 문의번호(inqId) 기준 삭제
	// DELETE FROM inq WHERE inq_id = ?;
	// deleteById()

	// 회원번호(userNum) 기준 문의 목록 조회
	// = 마이페이지 > 내가 작성한 1:1 문의 내역 조회
	// SELECT * FROM inq WHERE user_num = ?;
	List<Inq> findByUserNum(Long userNum);

	// 문의번호(inqId) + 회원번호(userNum) 기준 단건 조회
	// = 회원 본인 글 상세 조회
	// = 회원 본인 글 수정 / 삭제 권한 체크
	// SELECT * FROM inq WHERE inq_id = ? AND user_num = ?;
	Optional<Inq> findByInqIdAndUserNum(Long inqId, Long userNum);

	Long countByInqStCd(String inqStCd);

	Long countByCrtAtGreaterThanEqualAndCrtAtLessThan(LocalDateTime startAt, LocalDateTime endAt);

}
