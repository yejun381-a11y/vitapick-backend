package com.vita.vitapickBack.custom.cusIt;

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
@Table(name = "cus_it")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CusIt {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "cus_it_id")
	private Long cusItId;

	@Column(name = "cus_id")
	private Long cusId;
	
	@Column(name = "sur_title")
	private String surTitle;

	@Column(name = "prd_id")
	private Long prdId;

	@Column(name = "sort_num")
	private Integer sortNum;    // 추천 노출 순서 1~5
}