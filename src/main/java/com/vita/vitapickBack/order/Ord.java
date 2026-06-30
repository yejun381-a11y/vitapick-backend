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
@Table(name = "ord")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class Ord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ord_id")
    private Long ordId;

    @Column(name = "user_num", nullable = false)
    private Long userNum;

    @Column(name = "ord_no", nullable = false, unique = true, length = 50)
    private String ordNo;

    @Column(name = "addr_id", nullable = false)
    private Long addrId;

    @Column(name = "ord_st_cd", nullable = false, length = 30)
    private String ordStCd;

    @Column(name = "total_amt", nullable = false)
    @Builder.Default
    private Integer totalAmt = 0;
    
    @Column(name = "req_msg", length = 255)
    private String reqMsg;

    @CreationTimestamp
    @Column(name = "crt_at", updatable = false)
    private LocalDateTime crtAt;

    @UpdateTimestamp
    @Column(name = "upd_at")
    private LocalDateTime updAt;

}