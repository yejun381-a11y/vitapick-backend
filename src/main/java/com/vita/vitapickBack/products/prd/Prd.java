package com.vita.vitapickBack.products.prd;

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
@Table(name="prd")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Prd {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "prd_id")
    private Long prdId;

    @Column(name = "prd_nm")
    private String prdNm;

    @Column(name = "cat_cd")
    private Integer catCd;

    @Column(name = "brand")
    private String brand;

    @Column(name = "price")
    private Integer price;

    @Column(name = "desc_txt", columnDefinition = "TEXT")
    private String descTxt;

    @Column(name = "dos_txt", columnDefinition = "TEXT")
    private String dosTxt;

    @Column(name = "warn_txt", columnDefinition = "TEXT")
    private String warnTxt;

    @Column(name = "use_yn")
    private String useYn;

    @Column(name = "crt_at")
    private LocalDateTime crtAt;

    @Column(name =  "upd_at")
    private LocalDateTime updAt;

    @Column(name = "wd_at")
    private LocalDateTime wdAt;

    // 성분 정보 JSON
    @Column(name = "ingr", columnDefinition = "TEXT")
    private String ingr;
}