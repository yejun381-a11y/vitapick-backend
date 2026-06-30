package com.vita.vitapickBack.jwtToken;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.vita.vitapickBack.users.UsersDTO;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;


@Service
@Log4j2
public class TokenProvider {

	@Value("${jwt.secret}")
	private String KEY;
	
	private SecretKey key;
	
	@PostConstruct
	public void initKey() {
		this.key = Keys.hmacShaKeyFor(KEY.getBytes(StandardCharsets.UTF_8));
	}
	
	//Jwt토큰발행
	public UsersDTO generateToken(Map<String, Object> claimList) {
//		Date refreshTokenExpiresIn = new Date(System.currentTimeMillis()+1000*60*5);
		Date refreshTokenExpiresIn = new Date(System.currentTimeMillis()+1000*60*60*24*10);
		//-> refreshToken 만료시간: 7일 (실무에서 일반적으로 7~14일 설정) 
		
		return UsersDTO.builder()
				.accessToken(generateAccessToken(claimList))
				.refreshToken(generateRefreshToken(claimList, refreshTokenExpiresIn))
				.refreshTokenExpiresln(refreshTokenExpiresIn.getTime())  
				.userNum((Long)claimList.get("userNum"))
				.userNm((String)claimList.get("userNm"))
				.loginId((String)claimList.get("loginId"))
				.roleCd((String)claimList.get("roleCd"))
				.build();
	}
	
	//AccessToken 생성
	public String generateAccessToken(Map<String, Object> claimList) {
		log.info("** AccessToken 발행 **");
		Date accessTokenExpiresIn = Date.from(Instant.now().plus(Duration.ofMinutes(5)));
		//-> accessToken 만료시간: 30분 설정 (Test 중에는 2분 설정)
		
		return Jwts.builder()
				.claims(claimList)
				.subject((String)claimList.get("loginId"))
				.issuer("vitapick app")
				.issuedAt(new Date())
				.expiration(accessTokenExpiresIn)
				.signWith(key, Jwts.SIG.HS512)
				.compact();
	}
	
	//=> RefreshToken 생성 : 만료기간 7일
	public String generateRefreshToken(Map<String, Object> claimList, Date refreshTokenExpiresIn) {
		log.info("** RefreshToken 발급???? **");
		return Jwts.builder()
				.claims(claimList) //추후 accessToken 재발급시 필요함 (id, roleCd)
				.subject((String)claimList.get("loginId"))
		        .issuedAt(new Date())
		        .expiration(refreshTokenExpiresIn) // +7일(generateToken에 정의)
		        .signWith(key, Jwts.SIG.HS512) 
		        .compact();
	}//generateRefreshToken
	
	
	//프론트에서 전달받은 Token 검증 (Role 포함) 
	//=> Claims 값 분석
	//	- Claim 정보 (사용자정보, 만료시간 등)을 추출해서 Claims 형식_Map<String,Object> 형태로 반환
	//	- 아래 메서드 parseClaims(String token) 에 구현 
	public Claims validateToken(String token) {
		return Jwts.parser()
				.verifyWith(key)
				.build()
				.parseSignedClaims(token)
				.getPayload();
	}//validateToken
	
	//로그인 실패 공통 응답 메서드
    private void writeErrorResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        //=> SC_UNAUTHORIZED = 401 : JWT오류(인증실패), 재로그인 필요한 경우 대부분사용
        response.setContentType("application/json");
        response.getWriter().write(message);
    }
    
}//TokenProvider
