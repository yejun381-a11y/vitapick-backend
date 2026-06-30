package com.vita.vitapickBack.jwtToken;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.google.gson.Gson;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

//커스텀인증필터 클래스

@Component
@Log4j2
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final TokenProvider tokenProvider;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
    	
		//=> Preflight요청은 체크하지 않음 
	    if(request.getMethod().equals("OPTIONS")) { return true; }

	    String path = request.getRequestURI(); // "/auth/userdetail"
	    //log.info("check uri => " + path);
	    
	    //=> 체크하지 않을 경로 설정
	    //-> 기존 코드 때문 추가함: http://localhost:8080/ 등등 
	    if(path.startsWith("/v1/checkid")) { return true; }
	    if(path.startsWith("/v1/checkemail")) { return true; }
	    //-> 회원가입, 로그인, 로그아웃, 아이디찾기, 비밀번호찾기, 비밀번호재설정
	    if(path.startsWith("/v1/auth/")) { return true; }

	    //-> 이미지 조회 경로는 체크하지 않는다면 (예시)
	    if(path.startsWith("/resources/uploadImages/")) { return true; }
	    //-> 상품 조회 경로는 체크하지 않는다면 (예시)
	    if(path.startsWith("/api/v1/product/")) { return true; }
	    //-> 인기상품 TOP5 조회는 비회원도 허용
	    if(path.equals("/order/top-products")) { return true; }
	    
    	return false;
    } //shouldNotFilter
    
	//*** parseBearerToken()
	//=> Request 객체의 Header를 파싱해서 token 을 return
	private String parseBearerToken(HttpServletRequest request) {
		
		String bearerToken = request.getHeader("Authorization");
		if ( StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ") ) {
			return bearerToken.substring(7);
		}
		return null;
		//=> 없다면 현재 아무런 정보가 없는 상태 (새토큰 발행 필요)
        //	 따라서 null이 반환 되고 null일 경우 인증할 필요가 없으니
		//	 바로 filterChain.doFilter(request,response) 로 건너간다
	} //parseBearerToken
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
    		throws ServletException, IOException {
    	
		try {
			//1) request 에서 AccessToken 가져오기.
			String accessToken = parseBearerToken(request);
			//=> "Bearer" 를 제외한 문자열 제공
			
			log.info("** JwtAuthenticationFilter, doFilterInternal(), accessToken 확인=> "+accessToken);
			
			if (!StringUtils.hasText(accessToken) || accessToken.equalsIgnoreCase("null")) {
				filterChain.doFilter(request, response);
				return;
			}

			if (accessToken != null && !accessToken.equalsIgnoreCase("null")) {
				//=> 토큰 없다고 예외 던지지 말 것
				//	 Token값 null 인경우에도 return 되어야 다음 체인으로 넘어감.
				//   Exception 처리하게되면 로그인 처리도 안됨.
				
				// 2) 토큰 검증 & claims 가져오기
				//=> 검증 과정에서 만료시 ExpiredJwtException 발생 
				Claims claims = tokenProvider.validateToken(accessToken);
				
				//=> 검증 성공시 claims 에서 id 와 roleList 가져와 시큐리티 인증정보에 저장함.
				log.info("** Authenticated 결과 JWT claims: " + claims);
				Number userNumNumber = (Number)claims.get("userNum");
				Long userNum = userNumNumber.longValue();
				String loginId = (String)claims.get("loginId");
				String userNm = (String)claims.get("userNm");
				String roleCd = (String)claims.get("roleCd");
				
				// 3) 인증 완료
				// => 인증결과를 UsernamePasswordAuthenticationToken 에 담아 시큐리티가 사용하는 인증토큰을 만들고
				// => 이 인증토큰 값(Authentication)을 SecurityContextHolder를 이용하여 SecurityContext에 등록
				//	  ( SecurityContextHolder에 등록해야 인증된 user로 인식함)
				
				List<SimpleGrantedAuthority> authorities = new ArrayList<>();
				if("ADMIN".equals(roleCd)) {
					authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
					authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
				}else if("USER".equals(roleCd)) {
					authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
				}
				
				AbstractAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
						userNum, // 컨트롤러에서 @AuthenticationPrincipal 로 사용가능 (AuthController userDetail() 확인) 
						null, // Password를 의미하며 보통은 null 로 처리
						authorities );
				
				authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

				// => details 필드에 인증 소스인 request 값 set 
				
				// => SecurityContextHolder에 인증된 user등록.
				//	  ( 그래야만 인증된 user로 인식함)
				//	-> 비어있는 SecurityContext를 SecurityContextHolder로부터 생성
				//	-> 여기에 인증정보를 넣고
				//	-> 이렇게 인증정보를 담은 SecurityContext 를 SecurityContextHolder에 등록함.
				SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
				securityContext.setAuthentication(authentication);
				SecurityContextHolder.setContext(securityContext);
				
				filterChain.doFilter(request, response);
			} //if_token 존재
		}catch (Exception e) {
			//=> Jwt 필터는 시큐리티체인에서 검증하므로 Exception 메시지를 response에 직접 작성함. 
			log.error("doFilterInternal() Exception => "+e.toString());
			Gson gson = new Gson();
			String msg = gson.toJson(Map.of("error", "ERROR_ACCESS_TOKEN"));
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.setContentType("application/json");
			PrintWriter printWriter = response.getWriter();
			printWriter.println(msg);
			printWriter.close();
		}//catch
	} //doFilterInternal
    	
}//class
