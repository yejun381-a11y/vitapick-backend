package com.vita.vitapickBack.products.wish;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WishServiceImpl implements WishService {

    private final WishRepository wishRepository;

    // 내 찜 목록 조회
    @Override
    public List<Wish> findByUserNum(Long userNum) {
        return wishRepository.findByUserNumOrderByCrtAtDesc(userNum);
    }

    // 찜 여부 확인
    @Override
    public boolean isWished(Long userNum, Long prdId) {
        return wishRepository.existsByUserNumAndPrdId(userNum, prdId);
    }

    // 찜 추가
    @Override
    public Wish addWish(Long userNum, WishDTO dto) {

        boolean exists = wishRepository.existsByUserNumAndPrdId(userNum, dto.getPrdId());

        if (exists) {
            throw new RuntimeException("이미 찜한 상품입니다.");
        }

        Wish wish = Wish.builder()
                .userNum(userNum)
                .prdId(dto.getPrdId())
                .crtAt(LocalDateTime.now())
                .build();

        return wishRepository.save(wish);
    }

    // 찜 취소
    @Override
    @Transactional
    public void deleteWish(Long userNum, Long prdId) {
        wishRepository.deleteByUserNumAndPrdId(userNum, prdId);
    }

    // 찜 토글
    @Override
    @Transactional
    public boolean toggleWish(Long userNum, Long prdId) {

        boolean exists = wishRepository.existsByUserNumAndPrdId(userNum, prdId);

        if (exists) {
            wishRepository.deleteByUserNumAndPrdId(userNum, prdId);
            return false;
        }

        Wish wish = Wish.builder()
                .userNum(userNum)
                .prdId(prdId)
                .crtAt(LocalDateTime.now())
                .build();

        wishRepository.save(wish);
        return true;
    }
}