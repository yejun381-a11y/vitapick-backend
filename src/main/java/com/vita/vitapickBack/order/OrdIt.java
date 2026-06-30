package com.vita.vitapickBack.order;

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
@Table(name = "ord_it")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class OrdIt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ord_it_id")
    private Long ordItId;

    @Column(name = "ord_id", nullable = false)
    private Long ordId;

    @Column(name = "prd_id", nullable = false)
    private Long prdId;
    
    @Column(name = "cus_id")
    private Long cusId;

    @Column(name = "prd_nm", nullable = false, length = 100)
    private String prdNm;

    @Column(name = "it_qty", nullable = false)
    @Builder.Default
    private Integer itQty = 1;

    @Column(name = "price", nullable = false)
    @Builder.Default
    private Integer price = 0;

    @Column(name = "it_amt", nullable = false)
    @Builder.Default
    private Integer itAmt = 0;

}