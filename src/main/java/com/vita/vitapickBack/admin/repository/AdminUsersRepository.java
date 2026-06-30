package com.vita.vitapickBack.admin.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vita.vitapickBack.users.Users;

@Repository
public interface AdminUsersRepository extends JpaRepository<Users, Long> {

	// 관리자 회원 목록을 조건별로 조회한다.
	@Query("""
			SELECT u
			FROM Users u
			WHERE (:keyword IS NULL OR :keyword = ''
			       OR LOWER(u.loginId) LIKE LOWER(CONCAT('%', :keyword, '%'))
			       OR LOWER(u.userNm) LIKE LOWER(CONCAT('%', :keyword, '%')))
			  AND (:statusCd IS NULL OR :statusCd = '' OR u.statusCd = :statusCd)
			  AND u.roleCd = 'USER'
			  AND (:startAt IS NULL OR u.crtAt >= :startAt)
			  AND (:endAt IS NULL OR u.crtAt < :endAt)
			""")
	Page<Users> findAdminUsers(@Param("keyword") String keyword, @Param("statusCd") String statusCd,
			@Param("startAt") LocalDateTime startAt, @Param("endAt") LocalDateTime endAt, Pageable pageable);

	// 관리자 대시보드 월별 신규 회원 수를 집계한다.
	@Query(value = """
			SELECT DATE_FORMAT(u.crt_at, '%Y-%m') AS month, COUNT(*) AS count
			FROM users u
			WHERE u.role_cd = 'USER'
			  AND u.crt_at >= :startAt
			  AND u.crt_at < :endAt
			GROUP BY DATE_FORMAT(u.crt_at, '%Y-%m')
			ORDER BY month ASC
			""", nativeQuery = true)
	List<Object[]> findMonthlyNewUserCounts(@Param("startAt") LocalDateTime startAt,
			@Param("endAt") LocalDateTime endAt);

	// 관리자 대시보드 회원 상태별 수를 집계한다.
	Long countByStatusCd(String statusCd);

	// 관리자 회원 엑셀 다운로드 목록을 조건별로 조회한다.
	@Query("""
			SELECT u
			FROM Users u
			WHERE (:keyword IS NULL OR :keyword = ''
			       OR LOWER(u.loginId) LIKE LOWER(CONCAT('%', :keyword, '%'))
			       OR LOWER(u.userNm) LIKE LOWER(CONCAT('%', :keyword, '%')))
			  AND (:statusCd IS NULL OR :statusCd = '' OR u.statusCd = :statusCd)
			  AND u.roleCd = 'USER'
			  AND (:startAt IS NULL OR u.crtAt >= :startAt)
			  AND (:endAt IS NULL OR u.crtAt < :endAt)
			ORDER BY u.crtAt DESC
			""")
	List<Users> findAdminUsersForExcel(@Param("keyword") String keyword, @Param("statusCd") String statusCd,
			@Param("startAt") LocalDateTime startAt, @Param("endAt") LocalDateTime endAt);
}
