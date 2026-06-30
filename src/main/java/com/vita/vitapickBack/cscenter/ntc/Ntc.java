package com.vita.vitapickBack.cscenter.ntc;

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
@Table(name = "ntc")

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Ntc{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ntc_id")
	private Long ntcId;
	
	@Column(nullable = false, length = 200)
	private String ttl;
	
	@Column(name="ntc_txt", nullable = false, columnDefinition = "text")
	private String ntcTxt;
	
	@Column(name="view_cnt", nullable = false)
	private Integer viewCnt = 0;
	
	@Column(name="use_yn",nullable = false)
	private Character useYn;
	
	@CreationTimestamp
	@Column(name="crt_at")
	private LocalDateTime crtAt;
	
	@UpdateTimestamp
	@Column(name="upd_at")
	private LocalDateTime updAt;
	
}
