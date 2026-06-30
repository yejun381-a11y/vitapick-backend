package com.vita.vitapickBack.products.prd_img;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// prd_img 테이블 엔티티 - 상품 이미지 (THUMB: 썸네일, DETAIL: 상세이미지)
@Entity
@Table(name="prd_img")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PrdImg {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "prd_img_id")
    private Long prdImgId;

    @Column(name = "prd_id", nullable = false)
    private Long prdId;

    @Column(name = "img_url", nullable = false, length = 500)
    private String imgUrl;

    @Column(name = "img_type_cd", nullable = false, length = 30)
    private String imgTypeCd;
}