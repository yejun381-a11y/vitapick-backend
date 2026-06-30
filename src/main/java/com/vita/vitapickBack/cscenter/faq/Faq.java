package com.vita.vitapickBack.cscenter.faq;

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
@Table(name = "faq")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Faq {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "faq_id")
	private Long faqId;

	@Column(name = "faq_ctg_cd", length = 30, nullable = false)
	private String faqCtgCd;

	@Column(name = "ttl", length = 200, nullable = false)
	private String ttl;

	@Column(name = "faq_txt", columnDefinition = "TEXT", nullable = false)
	private String faqTxt;

	@Column(name = "view_cnt", nullable = false)
	private Integer viewCnt = 0;

	@Column(name = "use_yn", length = 1, nullable = false)
	private String useYn;

	@CreationTimestamp
	@Column(name = "crt_at")
	private LocalDateTime crtAt;

	@UpdateTimestamp
	@Column(name = "upd_at")
	private LocalDateTime updAt;

}