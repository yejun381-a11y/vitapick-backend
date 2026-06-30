package com.vita.vitapickBack.cscenter.inq;

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
@Table(name = "inq")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Inq {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "inq_id")
	private Long inqId;

	@Column(name = "user_num", nullable = false)
	private Long userNum;

	@Column(name = "inq_tp_cd", length = 30, nullable = false)
	private String inqTpCd;

	@Column(name = "inq_st_cd", length = 30, nullable = false)
	private String inqStCd;

	@Column(name = "ttl", length = 200, nullable = false)
	private String ttl;

	@Column(name = "inq_txt", columnDefinition = "TEXT", nullable = false)
	private String inqTxt;
	
	@Column(name = "ans_txt", columnDefinition = "TEXT")
	private String ansTxt;
	
	@Column(name = "ans_at")
	private LocalDateTime ansAt;

	@Column(name = "view_cnt", nullable = false)
	private Integer viewCnt = 0;

	@CreationTimestamp
	@Column(name = "crt_at")
	private LocalDateTime crtAt;

	@UpdateTimestamp
	@Column(name = "upd_at")
	private LocalDateTime updAt;	

}