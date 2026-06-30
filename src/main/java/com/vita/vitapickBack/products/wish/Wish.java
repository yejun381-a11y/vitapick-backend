package com.vita.vitapickBack.products.wish;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// 찜하기 엔티티
@Entity
@Table(name = "wish")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Wish {

    // 찜 ID
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "wish_id")
    private Long wishId;

    // 회원 번호
    @Column(name = "user_num", nullable = false)
    private Long userNum;

    // 상품 ID
    @Column(name = "prd_id", nullable = false)
    private Long prdId;

    // 생성일시
    @Column(name = "crt_at", nullable = false)
    private LocalDateTime crtAt;
}