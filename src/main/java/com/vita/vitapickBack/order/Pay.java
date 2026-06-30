package com.vita.vitapickBack.order;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "pay")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class Pay {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pay_id")
    private Long payId;

    @Column(name = "ord_id", nullable = false)
    private Long ordId;

    @Column(name = "pay_no", nullable = false, unique = true, length = 50)
    private String payNo;

    @Column(name = "pay_mthd_cd", nullable = false, length = 30)
    private String payMthdCd;

    @Column(name = "pay_st_cd", nullable = false, length = 30)
    private String payStCd;

    @Column(name = "pay_amt", nullable = false)
    @Builder.Default
    private Integer payAmt = 0;

    @CreationTimestamp
    @Column(name = "paid_at", updatable = false)
    private LocalDateTime paidAt;

    @CreationTimestamp
    @Column(name = "crt_at", updatable = false)
    private LocalDateTime crtAt;

    @UpdateTimestamp
    @Column(name = "upd_at")
    private LocalDateTime updAt;

}