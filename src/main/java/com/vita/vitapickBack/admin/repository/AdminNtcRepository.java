package com.vita.vitapickBack.admin.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vita.vitapickBack.cscenter.ntc.Ntc;

@Repository
public interface AdminNtcRepository extends JpaRepository<Ntc, Long> {

    // 관리자 공지사항 목록을 사용여부별로 조회한다.
    @Query("""
            SELECT n
            FROM Ntc n
            WHERE (:useYn IS NULL OR n.useYn = :useYn)
            """)
    Page<Ntc> findAdminNotices(
            @Param("useYn") Character useYn,
            Pageable pageable);
}
