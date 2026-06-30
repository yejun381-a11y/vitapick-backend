package com.vita.vitapickBack.products.rvw;

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

@Entity
@Table(name = "rvw")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Rvw {

    // 리뷰 ID
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rvw_id")
    private Long rvwId;

    // 회원 ID
    @Column(name = "user_num", nullable = false)
    private Long userNum;

    // 주문 상품 ID
    @Column(name = "ord_it_id", nullable = false)
    private Long ordItId;

    // 상품 ID
    @Column(name = "prd_id", nullable = false)
    private Long prdId;

    // 평점
    @Column(name = "rating", nullable = false)
    private Integer rating;

    // 리뷰 내용
    @Column(name = "cmt", columnDefinition = "TEXT")
    private String cmt;

    // 관리자 답글 내용
    @Column(name = "reply_txt", columnDefinition = "TEXT")
    private String replyTxt;

    // 관리자 답글 작성일시
    @Column(name = "reply_at")
    private LocalDateTime replyAt;

    // 게시 여부
    @Column(name = "use_yn", nullable = false)
    private String useYn;

    // 생성일시
    @Column(name = "crt_at", nullable = false)
    private LocalDateTime crtAt;

    // 수정일시
    @Column(name = "upd_at")
    private LocalDateTime updAt;
}