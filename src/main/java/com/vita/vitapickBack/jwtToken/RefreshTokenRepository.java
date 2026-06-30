package com.vita.vitapickBack.jwtToken;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import jakarta.transaction.Transactional;

@Transactional
@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

	Optional<RefreshToken>findByRefreshToken(String refreshToken);

    void deleteByRefreshToken(String refreshToken);

    void deleteByloginId(String loginId);
    
    void deleteByUserNum(Long userNum);
    
}
