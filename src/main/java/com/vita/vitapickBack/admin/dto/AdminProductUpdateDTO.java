package com.vita.vitapickBack.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminProductUpdateDTO {

    private String prdNm;
    private String brand;
    private Integer catCd;
    private Integer price;
    private String descTxt;
    private String dosTxt;
    private String warnTxt;
    private String useYn;
}
