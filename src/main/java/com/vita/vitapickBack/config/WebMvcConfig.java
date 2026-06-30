package com.vita.vitapickBack.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

//** WebMvcConfigurer
//=> 스프링부트의 자동설정에 원하는 설정을 추가설정 할수있는 메서드들을 제공하는 인터페이스. 
//=> CORS 방침 설정 
//	-> addCorsMappings() : 프로젝트 전역설정

/* ** [Spring Boot] CORS 해결 방법 3가지
=> WebMvcConfigurer, @CrossOrigin, Filter 
	
=> 방법1 WebMvcConfigurer
	-> Project  전역설정가능
	-> addCorsMappings(...) 메서드를 오버라이딩 해서 CORS를 적용할 URL패턴을 정의할 수 있음 
	-> WebMvcConfig.java 참고
	
=> 방법2 Controller 또는 메소드단에서 annotation을 통해 적용 
	-> @CrossOrigin(origins = "*", allowedHeaders = "*")
	-> UserController.java
	
=> 방법3 Filter
  커스텀필터(CorsFilter) 를 만들어 직접 response에 header를 넣어주기
	- Filter 인터페이스를 구현하여 doFilter 메서드 Override
	- @Component 애너테이션 추가 
	- Filter 는 꼭 javax.servlet 의 Filter를 사용함.
*/

//=> 배포후 Image 처리를 위한 절대경로 설정

@Configuration
public class WebMvcConfig implements WebMvcConfigurer  {
	
	//** React & SpringBoot CORS 프로젝트 단위 설정
	private final long MAX_AGE_SECS = 3600; //단위는 초

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		
		registry.addMapping("/**") //애플리케이션의 모든 엔드포인트에 대한 CORS 매핑추가
		.allowedOrigins("http://localhost:5173", "http://localhost:5174", "http://localhost:3000", "http://43.200.183.245/")
		// => 배포후 참고
		// "Http://52.78.164.109:3000", "http://52.78.164.109:8080", "http://52.78.164.109"
		.allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
		// => CORS정책상 접근 가능한 origin인지 확인하기 위해 preflight를 보내는데, 
		//    이때 메소드가 'OPTIONS' 이므로 반드시 추가	
		.allowedHeaders("*")
		.allowCredentials(true)
		//=> 브라우저가 “쿠키, 세션, 로그인 정보 같은 인증 정보(credential)”를 포함한 요청을 서버에 보낼 수 있게 허용하는 설정
		//	 세션기반 로그인, 쿠키로 인증 처리할 때, JWT를 쿠키에 담아 쓸 때 등등 필요함			
		//=> 단 credentials true 로 이것을 허용하면, allowedOrigins("*") 로 전체허용은 허용하지않음
		//    (그러므로 origins 속성값은 구체적으로 명시함) 
		.maxAge(MAX_AGE_SECS);
	}//addCorsMappings
	
	//=> CORS 설정의 의미
	//	- SOP(Same-Origin Policy) 의 본질은
	//	  “다른 사이트의 데이터를 마음대로 읽지 마라” 즉 JS 가 응답을 읽는것을 제한하는 규칙
	//	- 그래서 브라우져는 Ajax 처리와같은 다른사이트로 데이터 요청처리시에는
	//	  본 요청 처리전 먼저 preflight 요청(OPTIONS 방식) 을 보내어 확인함 
	//	 ( 예, “나 POST로, Authorization 헤더 포함해서 요청하려고 하는데 괜찮아?” )	
	//	- CORS 설정에서 이를 받아 여기서 보내는 response 를 읽어도 된다는 확인을 해주는 과정임.
	//	 ( “응, 그 조건 허용할게” -> 그리고 허용된 범위내에서만 요청이 실행됨. )  
	
} //class
