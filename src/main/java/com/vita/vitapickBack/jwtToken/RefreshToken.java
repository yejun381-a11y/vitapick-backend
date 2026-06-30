package com.vita.vitapickBack.jwtToken;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken {

	@Id
	private Long userNum;
	
	private String loginId;
	
	@Column(length=1000, nullable = false)
	private String refreshToken;
	
	private Long expiration;
}
