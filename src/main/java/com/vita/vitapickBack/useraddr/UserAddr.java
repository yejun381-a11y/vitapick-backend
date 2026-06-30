package com.vita.vitapickBack.useraddr;

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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_addr")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAddr {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "addr_id")
	private Long addrId;

	@Column(name = "user_num", nullable = false)
	private Long userNum;

	@Column(name = "addr_nm", nullable = false, length = 50)
	private String addrNm;

	@Column(name = "rcv_nm", nullable = false, length = 50)
	private String rcvNm;

	@Column(name = "rcv_tel", nullable = false, length = 20)
	private String rcvTel;

	@Column(name = "zip_cd", nullable = false, length = 10)
	private String zipCd;

	@Column(name = "addr1", nullable = false, columnDefinition = "TEXT")
	private String addr1;

	@Column(name = "addr2", nullable = false, columnDefinition = "TEXT")
	private String addr2;

	@Column(name = "base_yn", nullable = false, length = 1)
	@Builder.Default
	private String baseYn = "N";

	@CreationTimestamp
	@Column(name = "crt_at", updatable = false)
	private LocalDateTime crtAt;

	@UpdateTimestamp
	@Column(name = "upd_at")
	private LocalDateTime updAt;
}