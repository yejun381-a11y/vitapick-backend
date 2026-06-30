package com.vita.vitapickBack.order;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PayRepository extends JpaRepository<Pay, Long> {

    // 주문번호로 결제 조회
    // 주문 상세 조회 시 사용
    Pay findByOrdId(Long ordId);

    // 결제번호 조회
    Pay findByPayNo(String payNo);

    // 결제 완료 처리
    // 기본 제공 메서드 사용
    // save(pay)

    // 결제 상세 조회
    // 기본 제공 메서드 사용
    // findById(payId)

    // 전체 결제 목록 조회
    // 기본 제공 메서드 사용
    // findAll()

}