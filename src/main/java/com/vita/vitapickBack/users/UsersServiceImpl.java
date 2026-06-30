package com.vita.vitapickBack.users;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.vita.vitapickBack.jwtToken.RefreshToken;
import com.vita.vitapickBack.jwtToken.RefreshTokenRepository;
import com.vita.vitapickBack.jwtToken.TokenProvider;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
@RequiredArgsConstructor
public class UsersServiceImpl implements UsersService {

    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refRepository;

    // 아이디 중복확인
    @Override
    public boolean checkLoginId(String loginId) {
        log.info("** checkLoginId => " + loginId);
        return usersRepository.existsByLoginId(loginId);
    }

    // 이메일 중복확인
    @Override
    public boolean checkEmail(String email) {
        log.info("** checkEmail => " + email);
        return usersRepository.existsByEmail(email);
    }

    // 회원가입
    @Override
    public void signup(UsersDTO usersDTO) {
        log.info("** signup => " + usersDTO.getLoginId());

        usersDTO.setPwd(passwordEncoder.encode(usersDTO.getPwd()));

        Users users = Users.builder()
            .loginId(usersDTO.getLoginId())
            .pwd(usersDTO.getPwd())
            .userNm(usersDTO.getUserNm())
            .tel(usersDTO.getTel())
            .email(usersDTO.getEmail())
            .genderCd(usersDTO.getGenderCd())
            .birthYmd(usersDTO.getBirthYmd())
            .statusCd("ACTIVE")
            .roleCd("USER")
            .build();

        usersRepository.save(users);
    }

    // 아이디찾기
    @Override
    public UsersDTO findId(UsersDTO usersDTO) {
        Users users = usersRepository.findByUserNmAndEmail(usersDTO.getUserNm(), usersDTO.getEmail())
            .orElseThrow(() -> new RuntimeException("일치하는 회원 정보가 없습니다."));

        return UsersDTO.builder()
            .loginId(users.getLoginId())
            .build();
    }
    
    // 비밀번호찾기(인증번호발송)
    private Map<String, String> sendOtpCodeStore = new HashMap<>();
    
    @Override
    public String sendOtpCode(UsersDTO usersDTO) {
    	log.info("** sendOtpCode ID => " + usersDTO.getLoginId());
    	Users users = usersRepository.findByLoginIdAndUserNmAndEmail(
    			usersDTO.getLoginId(),
    			usersDTO.getUserNm(),
    			usersDTO.getEmail()
    			).orElseThrow(()->new RuntimeException("일치하는 회원정보가 없습니다."));
    	String sendOtpCode = String.valueOf((int)(Math.random() * 900000) + 100000);
    	log.info("** sendOtpCode => " + sendOtpCode);
    	sendOtpCodeStore.put(users.getLoginId(), sendOtpCode);
    	
    	return sendOtpCode;
    }
    
    // 비밀번호찾기(비밀번호재설정)
    @Override
    @Transactional
    public void resetPwd(UsersDTO usersDTO) {
    	log.info("** resetPwdId => " + usersDTO.getLoginId());
    	String savedCode = sendOtpCodeStore.get(usersDTO.getLoginId());
    	log.info("** savedCode => " + savedCode);
    	log.info("** inputOtpCode =>" + usersDTO.getOtpCode());
    	if(savedCode==null) {
    		throw new RuntimeException("인증번호 발급 내역이 없습니다.");
    	}
    	if(!savedCode.equals(usersDTO.getOtpCode())) {
    		throw new RuntimeException("인증번호가 일치하지 않습니다.");
    	}
    	Users users = usersRepository.findByLoginId(usersDTO.getLoginId())
    			.orElseThrow(() -> new RuntimeException("회원정보가 없습니다."));
    	users.setPwd(passwordEncoder.encode(usersDTO.getPwd()));
    	usersRepository.save(users);
    	sendOtpCodeStore.remove(usersDTO.getLoginId());
    }
    
    // 로그인
    @Override
    @Transactional
    public UsersDTO login(HttpServletResponse response, Users entity) {

        String pwd = entity.getPwd();
        String loginId = entity.getLoginId();

        log.info("** login => " + entity.getLoginId());
        log.info("** login pwd => " + entity.getPwd());

        try {
            entity = usersRepository.findByLoginId(loginId)
                .orElseThrow(() -> new RuntimeException("회원 없음"));

            if (entity != null && passwordEncoder.matches(pwd, entity.getPwd())) {
                final UsersDTO usersDTO = tokenProvider.generateToken(entity.claimList());

                log.info("로그인 성공 => " + HttpStatus.OK + entity.claimList());

                RefreshToken refreshTokenEntity = RefreshToken.builder()
                    .userNum(entity.getUserNum())
                    .loginId(entity.getLoginId())
                    .refreshToken(usersDTO.getRefreshToken())
                    .expiration(usersDTO.getRefreshTokenExpiresln())
                    .build();

                refRepository.save(refreshTokenEntity);

                ResponseCookie cookie = ResponseCookie.from("refreshToken", usersDTO.getRefreshToken())
                    .httpOnly(true)
                    .sameSite("Lax")
                    .secure(false)
                    .path("/")
                    .maxAge(Duration.ofDays(7))
                    .build();

                response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());

                usersDTO.setRefreshToken(null);
                usersDTO.setRefreshTokenExpiresln(null);

                return usersDTO;
            } else {
                throw new Exception("Data Not Found");
            }

        } catch (Exception e) {
            log.error("로그인 실패, Exception => " + e.toString());
            return null;
        }
    }

    // RefreshToken으로 토큰 재발급
    @Override
    public ResponseEntity<?> getRefresh(String refreshToken, HttpServletResponse response) {

        Optional<RefreshToken> rTokenEntity = refRepository.findByRefreshToken(refreshToken);

        if (rTokenEntity.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("isEmpty: 유효하지 않는 RefreshToken");
        }

        log.info("DB조회 성공!! 존재하는 RefreshToken 입니다.");

        try {
            Claims claims = tokenProvider.validateToken(refreshToken);

            Number userNumNumber = (Number) claims.get("userNum");
            Long userNum = userNumNumber.longValue();

            String loginId = (String) claims.get("loginId");
            String roleCd = (String) claims.get("roleCd");
            String userNm = (String) claims.get("userNm");

            Map<String, Object> claimList = new HashMap<>();
            claimList.put("userNum", userNum);
            claimList.put("loginId", loginId);
            claimList.put("userNm", userNm);
            claimList.put("roleCd", roleCd);

            UsersDTO usersDTO = UsersDTO.builder()
                .accessToken(tokenProvider.generateAccessToken(claimList))
                .build();

            log.info("New AccessToken 발급, Token = " + usersDTO.getAccessToken());

            return ResponseEntity.ok(usersDTO);

        } catch (Exception e) {
            log.info("New AccessToken 발급중 RefreshToken 만료 Exception => " + e.toString());

            Cookie refreshCookie = new Cookie("refreshToken", null);
            refreshCookie.setPath("/");
            refreshCookie.setMaxAge(0);
            refreshCookie.setHttpOnly(false);
            refreshCookie.setSecure(true);
            response.addCookie(refreshCookie);

            refRepository.deleteByRefreshToken(refreshToken);

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("401, 만료되었습니다. 다시 로그인 해주세요.");
        }
    }

    // 로그아웃
    @Override
    public void logout(HttpServletResponse response, Long userNum) {

        refRepository.deleteByUserNum(userNum);

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", null)
            .httpOnly(true)
            .sameSite("Lax")
            .secure(false)
            .path("/")
            .maxAge(0)
            .build();

        response.setHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
    }

    // 회원정보 조회
    @Override
    public UsersDTO getUser(Long userNum) {
        log.info("** getUser => " + userNum);

        Users users = usersRepository.findById(userNum)
            .orElseThrow(() -> new RuntimeException("회원정보가 없습니다"));

        return UsersDTO.builder()
            .userNum(users.getUserNum())
            .loginId(users.getLoginId())
            .userNm(users.getUserNm())
            .tel(users.getTel())
            .email(users.getEmail())
            .genderCd(users.getGenderCd())
            .birthYmd(users.getBirthYmd())
            .statusCd(users.getStatusCd())
            .roleCd(users.getRoleCd())
            .crtAt(users.getCrtAt())
            .build();
    }

    // 회원정보 수정
    @Override
    public void updateUser(Long userNum, UsersDTO usersDTO) {
        log.info("** updateUser => userNum" + userNum);

        Users users = usersRepository.findById(userNum)
            .orElseThrow(() -> new RuntimeException("회원정보가 없습니다"));

        if (usersDTO.getPwd() != null && !usersDTO.getPwd().isEmpty()) {
            users.setPwd(passwordEncoder.encode(usersDTO.getPwd()));
        }

        users.setUserNm(usersDTO.getUserNm());
        users.setTel(usersDTO.getTel());
        users.setEmail(usersDTO.getEmail());
        users.setGenderCd(usersDTO.getGenderCd());
        users.setBirthYmd(usersDTO.getBirthYmd());

        usersRepository.save(users);
    }

    // 회원탈퇴
    @Override
    public void withdraw(Long userNum, UsersDTO usersDTO, HttpServletResponse response) {
        log.info("** withdraw => userNum" + userNum);
        //회원번호로 엔티티 생성
        Users users = usersRepository.findById(userNum)
            .orElseThrow(() -> new RuntimeException("회원정보가 없습니다"));
        //비밀번호 확인
        if(!passwordEncoder.matches(usersDTO.getPwd(), users.getPwd())) {
        	throw new RuntimeException("비밀번호가 일치하지 않습니다.");}
        //리프레시토큰 쿠키만료처리
        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", null)
        		.httpOnly(true)
        		.sameSite("Lax")
        		.secure(false)
        		.path("/")
        		.maxAge(0)
        		.build();
        response.setHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
        
        //유저엔티티 DB삭제
        usersRepository.delete(users);
        //리프레시토큰 DB삭제
        refRepository.deleteByUserNum(userNum);
    }

    
}