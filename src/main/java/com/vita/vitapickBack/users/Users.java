package com.vita.vitapickBack.users;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="users")
@EntityListeners(value= {AuditingEntityListener.class})
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Users {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long userNum;
	
	@Column(name="login_id", nullable = false)
	private String loginId;
	
	@Column(name="pwd", nullable = false)
	private String pwd;
	
	@Column(name="user_nm", nullable = false)
	private String userNm;
	
	@Column(name="tel", nullable = false)
	private String tel;
	
	@Column(name="email", nullable = false)
	private String email;
	
	@Column(name="gender_cd")
	private String genderCd;
	
	@Column(name="birth_ymd")
	private LocalDate birthYmd;
	
	@Column(name="status_cd", nullable = false)
	private String statusCd;
	
	@Column(name="role_cd", nullable = false)
	private String roleCd;
	
	@CreatedDate
	@Column(name="crt_at", nullable = false, updatable = false)
	private LocalDateTime crtAt;
	
	@LastModifiedDate
	@Column(name="upd_at")
	private LocalDateTime updAt;
	
	@Column(name="wd_dt")
	private LocalDateTime wdDt;
	
	//=> JWT token 발행시 사용됨 
	//    로그인 성공 후 createToken() 에 인자로 사용됨
	//	  인증에 필요한 필수 정보만 보관	
	//=> Map<String, Object> -> roleList 는 List Type 이므로..
	public Map<String, Object> claimList() {
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put("userNum", this.userNum);
		dataMap.put("loginId", this.loginId);
		dataMap.put("userNm", this.userNm);
		dataMap.put("roleCd", this.roleCd);
		return dataMap;
	}
	
	
}
