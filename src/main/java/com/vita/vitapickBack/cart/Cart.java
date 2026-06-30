package com.vita.vitapickBack.cart;

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
@Table(name = "cart")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Cart {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "cart_id")
	private Long cartId;

	@Column(name = "user_num", nullable = false)
	private Long userNum;

	@Column(name = "prd_id")
	private Long prdId;

	@Column(name = "cus_id")
	private Long cusId;

	@Column(name = "it_qty", nullable = false)
	private Integer itQty;

	@Column(name = "selected_yn", nullable = false)
	private Character selectedYn;

	@CreationTimestamp
	@Column(name = "crt_at", updatable = false)
	private LocalDateTime crtAt;

	@UpdateTimestamp
	@Column(name = "upd_at")
	private LocalDateTime updAt;

}