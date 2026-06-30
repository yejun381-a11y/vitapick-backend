package com.vita.vitapickBack.users;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersRepository extends JpaRepository<Users, Long>{
	
	//로그인 아이디로 회원 찾기 (로그인, 중복확인)
	Optional<Users> findByLoginId(String loginId);
	
    // 아이디 중복확인
    boolean existsByLoginId(String loginId);

    // 이메일 중복확인
    boolean existsByEmail(String email);
    
    // 아이디찾기(이름, 이메일)
    Optional<Users> findByUserNmAndEmail(String userNm, String email);

    // 비밀번호찾기(아이디, 이름, 이메일)
    Optional<Users> findByLoginIdAndUserNmAndEmail(String loginId, String userNm, String email);

    Long countByStatusCd(String statusCd);

}
