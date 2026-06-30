package com.vita.vitapickBack.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.vita.vitapickBack.jwtToken.JwtAuthenticationFilter;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Configuration
@EnableWebSecurity
@Log4j2
@RequiredArgsConstructor
public class SecurityConfig {
	
	@Bean
	PasswordEncoder getPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Autowired
	private JwtAuthenticationFilter jwtAuthenticationFilter;

	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		
		// Filter 등록
		http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
		
		// HttpSecurity 빌더 설정 & return
		return http
				.httpBasic(httpBasic -> httpBasic.disable()) // HTTP 기본 인증 비활성화
		        .formLogin(formLogin -> formLogin.disable()) // formLogin 비활성화
		        .logout(logout -> logout.disable()) // logout 비활성화
		        .csrf(csrf -> csrf.disable()) // CSRF 비활성화_필수항목
		        .cors(cors -> {}) // CORS설정 활성화(기본값)_필수항목
		        .sessionManagement(session -> session
		            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)) 
		        //=> 세션 비활성화 (무상태, ~풀스택~.ppt 38P 참고)
		        //	서버가 세션을 사용하여 로그인 상태를 기억하지 않겠다는 선언으로
		        //	세션을 사용하지말고 매요청마다 인증을 다시 하라는 뜻.
		        //	그럼에도 세션의 Attribute 를 사용하면 충돌 발생 가능성 있음
		        //	( 예, 인증이 꼬이거나 의도치 않은 상황 발생 )
		        
		       
		        //=> authorizeHttpRequests()
		        //	-> HTTP 요청에 대한 인가 설정을 구성하는 데 사용됨.
		        //	-> 다양한 인가 규칙을 정의할수 있으며, 경로별로 다른 권한 설정이 가능.
		        //permitAll()        = 누구나
		        //authenticated()    = 토큰 있는 사람만
		        //hasRole("ADMIN")   = ADMIN 토큰 있는 사람만

		        .authorizeHttpRequests(auth -> auth
		        	//=> Role 적용이후 정확한 요청명으로 수정  
		        	.requestMatchers(HttpMethod.OPTIONS ,"/**").permitAll()
		        	.requestMatchers("/api/admin/**").hasRole("ADMIN")
		        	.requestMatchers("/admin/**").hasRole("ADMIN")
		        	.requestMatchers("/user/memberlist").hasRole("ADMIN")
		        	.requestMatchers("/api/v1/info","/user/logout").authenticated()
		            //.anyRequest().authenticated()) // 모든 요청 인증 필요
		        	.anyRequest().permitAll())
		        .build();
	} //filterChain

} //class
