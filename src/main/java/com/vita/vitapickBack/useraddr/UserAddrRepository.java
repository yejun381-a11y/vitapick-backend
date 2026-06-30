package com.vita.vitapickBack.useraddr;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAddrRepository extends JpaRepository<UserAddr, Long> {

	// 회원 배송지 목록 조회
	@Query("""
			SELECT a
			FROM UserAddr a
			WHERE a.userNum = :userNum
			ORDER BY
			    CASE WHEN a.baseYn = 'Y' THEN 0 ELSE 1 END,
			    a.addrId DESC
			""")
	List<UserAddr> findByUserNum(@Param("userNum") Long userNum);

	// 기본 배송지 조회
	Optional<UserAddr> findByUserNumAndBaseYn(Long userNum, String baseYn);

	// 회원 배송지 개수 조회
	int countByUserNum(Long userNum);

	// 내 배송지 단건 조회
	Optional<UserAddr> findByAddrIdAndUserNum(Long addrId, Long userNum);

	// 배송지 등록
	// 기본 제공 메서드 사용
	// save(addr)

	// 배송지 수정
	// 기본 제공 메서드 사용
	// findById(addrId)
	// save(addr)

	// 배송지 삭제
	// 기본 제공 메서드 사용
	// deleteById(addrId)

	// 기본 배송지 변경
	// 기본 배송지 Y -> N 변경 후
	// 새로운 배송지 Y 설정
	// save(addr)
}