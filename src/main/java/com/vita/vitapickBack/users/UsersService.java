package com.vita.vitapickBack.users;

import org.springframework.http.ResponseEntity;

import jakarta.servlet.http.HttpServletResponse;

public interface UsersService {
    
    // 아이디 중복확인
    boolean checkLoginId(String loginId);
    
    // 이메일 중복확인
    boolean checkEmail(String email);
    
    // 아이디 찾기
    public UsersDTO findId(UsersDTO usersDTO);
    
    // 비밀번호 찾기(인증코드발송)
    public String sendOtpCode(UsersDTO usersDTO);
    
    // 비밀번호 찾기(비밀번호재설정)
    void resetPwd(UsersDTO usersDTO);
    
    // 회원가입
    void signup(UsersDTO usersDTO);

    // 로그인
    public UsersDTO login(HttpServletResponse response, Users entity);
    
    //=> RefreshToken 으로 토큰 재발급
    public ResponseEntity<?> getRefresh(String refreshToken, HttpServletResponse response);
    
    // 로그아웃
    public void logout(HttpServletResponse response, Long userNum);

    // 회원정보 조회
    public UsersDTO getUser(Long userNum);

    // 회원정보 수정
    void updateUser(Long userNum, UsersDTO usersDTO);

    // 회원탈퇴
    void withdraw(Long userNum, UsersDTO usersDTO, HttpServletResponse response);

}
