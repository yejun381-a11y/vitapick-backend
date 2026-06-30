package com.vita.vitapickBack.custom.cus;

import java.time.LocalDateTime;

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
@Table(name = "cus")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cus {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "cus_id")
	private Long cusId;

	@Column(name = "sur_id", unique = true)
	private Long surId;

	@Column(name = "sur_title")
	private String surTitle;
	
	@Column(name = "user_num")
	private Long userNum;

	@Column(name = "ai_model", length = 100)
	private String aiModel;

	@Column(name = "cus_sum", columnDefinition = "TEXT")
	private String cusSum;        // 커스텀 요약

	@Column(name = "cus_reason", columnDefinition = "TEXT")
	private String cusReason;     // 추천 이유

	@Column(name = "cus_dos", columnDefinition = "TEXT")
	private String cusDos;        // 복용 가이드

	@Column(name = "cus_caution", columnDefinition = "TEXT")
	private String cusCaution;    // 주의 사항

	@Column(name = "crt_at")
	private LocalDateTime crtAt;
}