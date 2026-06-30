package com.vita.vitapickBack.admin.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vita.vitapickBack.cscenter.faq.Faq;

@Repository
public interface AdminFaqRepository extends JpaRepository<Faq, Long> {

    // 관리자 FAQ 목록을 사용여부별로 조회한다.
    @Query("""
            SELECT f
            FROM Faq f
            WHERE (:useYn IS NULL OR :useYn = '' OR f.useYn = :useYn)
            """)
    Page<Faq> findAdminFaqs(
            @Param("useYn") String useYn,
            Pageable pageable);

    // 관리자 FAQ 목록을 사용여부와 분류별로 조회한다.
    @Query("""
            SELECT f
            FROM Faq f
            WHERE (:useYn IS NULL OR :useYn = '' OR f.useYn = :useYn)
              AND f.faqCtgCd IN :faqCtgCds
            """)
    Page<Faq> findAdminFaqsByCategoryCodes(
            @Param("useYn") String useYn,
            @Param("faqCtgCds") List<String> faqCtgCds,
            Pageable pageable);
}
