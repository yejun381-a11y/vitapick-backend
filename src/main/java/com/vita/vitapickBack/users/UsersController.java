package com.vita.vitapickBack.users;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class UsersController {

	private final UsersService usersService;
	
    // 아이디 중복확인
    @GetMapping("/checkid/{loginId}")
    public ResponseEntity<?> checkId(@PathVariable("loginId") String loginId) {
        try {
            boolean isDuplicate = usersService.checkLoginId(loginId);
            if (isDuplicate) {
                // 중복 - 사용불가
                return ResponseEntity.status(HttpStatus.OK)
                    .body(Map.of("idUse", "F", "message", "이미 사용중인 아이디입니다"));
            } else {
                // 사용가능
                return ResponseEntity.status(HttpStatus.OK)
                    .body(Map.of("idUse", "T", "message", "사용가능한 아이디입니다"));
            }
        } catch (Exception e) {
            log.error("** 아이디 중복확인 실패 => " + e.toString());
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body("아이디 중복확인 실패");
        }
    }
    
    // 이메일 중복확인
    @GetMapping("/checkemail/{email}")
    public ResponseEntity<?> checkEmail(@PathVariable("email") String email) {
        try {
            boolean isDuplicate = usersService.checkEmail(email);
            if (isDuplicate) {
                // 중복 - 사용불가
                return ResponseEntity.status(HttpStatus.OK)
                    .body(Map.of("emailUse", "F", "message", "이미 사용중인 email입니다"));
            } else {
                // 사용가능
                return ResponseEntity.status(HttpStatus.OK)
                    .body(Map.of("emailUse", "T", "message", "사용가능한 email입니다"));
            }
        } catch (Exception e) {
            log.error("** 이메일 중복확인 실패 => " + e.toString());
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body("email 중복확인 실패");
        }
    }
    
    // 회원가입
    // POST
    @PostMapping(value="/auth/join",
        consumes=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> signup(@RequestBody UsersDTO usersDTO) {
        try {
            usersService.signup(usersDTO);
            log.info("** 회원가입 성공 => " + usersDTO.getLoginId());
            return ResponseEntity.status(HttpStatus.OK)
                .body("회원가입 성공, 로그인 후 이용하세요");
        } catch (Exception e) {
            log.error("** 회원가입 실패 => " + e.toString());
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body("회원가입 실패 => " + e.getMessage());
        }
    }
    
    // 로그인
    // POST
    @PostMapping(value="/auth/login", consumes=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> login(@RequestBody Users entity, HttpServletResponse response) {
    	UsersDTO usersDTO = usersService.login(response, entity);
    	if(usersDTO != null) {
    		return ResponseEntity.ok(usersDTO);
    	}else return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("id 없음?");
    }
    
	// 로그아웃 (Token, Role 적용)
    // GET
	@GetMapping("/auth/logout")
	public ResponseEntity<?> logout(HttpServletResponse response, @AuthenticationPrincipal Long userNum) {
		usersService.logout(response, userNum);
		return  ResponseEntity.ok("로그아웃 성공");
	} //logout
    
	// 아이디찾기
	@PostMapping("/auth/findid")
	public ResponseEntity<?> findId(@RequestBody UsersDTO usersDTO) {
	    try {
	        UsersDTO result = usersService.findId(usersDTO);
	        return ResponseEntity.ok(result);
	    } catch (RuntimeException e) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
	    }
	}//findId	
	
	// 비밀번호찾기(인증번호발송)
	@PostMapping("/auth/sendotpcode")
	public ResponseEntity<?>sendOtpCode(@RequestBody UsersDTO usersDTO) {
		try {
			String sendOtpCode = usersService.sendOtpCode(usersDTO);
			return ResponseEntity.status(HttpStatus.OK).body(sendOtpCode);
		}catch(Exception e){
			log.error("** 인증번호 발급 실패 =>" + e.toString());
			return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(e.getMessage());
		}
	}
	// 비밀번호찾기(비밀번호재설정)
	@PostMapping("/auth/resetpwd")
	public ResponseEntity<?>resetPwd(@RequestBody UsersDTO usersDTO) {
		try {
			usersService.resetPwd(usersDTO);
			return ResponseEntity.status(HttpStatus.OK).body("비밀번호가 변경되었습니다.");
		}catch(Exception e) {
			log.error("** 비밀번호 재설정 실패 =>" + e.toString());
			return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(e.getMessage());
		}
	}

	//=> 리프레쉬 토큰
	//	AccessToken 만료시 front에서 요청함
    @GetMapping("/getrefresh")
    public ResponseEntity<?> getRefresh(@CookieValue("refreshToken") String refreshToken, HttpServletResponse response) {
        log.info("RefreshToken으로 토큰 재발급을 시도합니다.");
        return  usersService.getRefresh(refreshToken, response);
    }
	
    // 회원정보 조회
    // GET
    @GetMapping("/info")
    public ResponseEntity<?> getUser(@AuthenticationPrincipal Long userNum) {
    	try {
    		UsersDTO result = usersService.getUser(userNum);
    		log.info("userdetail, 전달된 loginId 확인 => "+result.getLoginId());
    		return ResponseEntity.status(HttpStatus.OK).body(result);
    	}catch(Exception e){
            log.error("** 회원정보조회실패 => " + e.toString());
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body("회원정보조회 실패 => " + e.getMessage());
    	}
    }
    
    // 회원정보 수정
    // PUT
    @PutMapping(value="/update", consumes=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateUser(
    		@AuthenticationPrincipal Long userNum,
            @RequestBody UsersDTO usersDTO) {
        try {
            usersService.updateUser(userNum, usersDTO);
            log.info("** 회원정보 수정 성공 => userNum" + userNum);
            return ResponseEntity.status(HttpStatus.OK)
                .body("회원정보 수정 성공");
        } catch (Exception e) {
            log.error("** 회원정보 수정 실패 => " + e.toString());
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body("회원정보 수정 실패 => " + e.getMessage());
        }
    }
    
    // 회원탈퇴
    // POST: 비번확인 후 탈퇴
    @PostMapping(value="/withdraw", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> withdraw(
    		@AuthenticationPrincipal Long userNum,
    		@RequestBody UsersDTO usersDTO,
    		HttpServletResponse response) {
        try {
            usersService.withdraw(userNum, usersDTO, response);
            log.info("** 회원탈퇴 성공 => userNum" + userNum);
            return ResponseEntity.status(HttpStatus.OK).body("회원탈퇴 성공");
        } catch (Exception e) {
            log.error("** 회원탈퇴 실패 => " + e.toString());
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body("회원탈퇴 실패 => " + e.getMessage());
        }
    }
    
}//controller
