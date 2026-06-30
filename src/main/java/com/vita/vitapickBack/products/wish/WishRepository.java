package com.vita.vitapickBack.products.wish;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WishRepository extends JpaRepository<Wish, Long> {

    // 특정 회원이 특정 상품을 이미 찜했는지 확인
    Optional<Wish> findByUserNumAndPrdId(Long userNum, Long prdId);

    // 특정 회원의 찜 목록 조회
    List<Wish> findByUserNumOrderByCrtAtDesc(Long userNum);

    // 특정 회원이 특정 상품을 찜했는지 여부 확인
    boolean existsByUserNumAndPrdId(Long userNum, Long prdId);

    // 특정 회원의 특정 상품 찜 삭제
    void deleteByUserNumAndPrdId(Long userNum, Long prdId);
}