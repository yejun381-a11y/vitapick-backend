package com.vita.vitapickBack.order;

import java.util.List;

public interface OrdService {

    // 회원 주문 목록 조회
    List<Ord> findByUserNumOrderByOrdIdDesc(Long userNum);

    // 주문번호 조회
    Ord findByOrdNo(String ordNo);

    // 주문상품 목록 조회
    List<OrdItDTO> findOrdItByOrdId(Long ordId);

    // 상품별 주문 조회
    List<OrdIt> findByPrdId(Long prdId);

    // 주문번호로 결제 조회
    Pay findPayByOrdId(Long ordId);

    // 결제번호 조회
    Pay findByPayNo(String payNo);

    // 주문 생성 + 결제
    Ord createOrder(Long userNum, OrdDTO orddto);
    
    // 상품별 판매량 TOP5 조회
    List<Object[]> findTopProducts();
}