package com.vita.vitapickBack.products.wish;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/wish")
public class WishController {

    private final WishService wishService;

    // 내 찜 목록 조회
    @GetMapping
    public ResponseEntity<?> findByUserNum(@AuthenticationPrincipal Long userNum) {
        try {
            List<Wish> result = wishService.findByUserNum(userNum);
            return ResponseEntity.status(HttpStatus.OK).body(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("찜 목록 조회에 실패했습니다.");
        }
    }

    // 찜 여부 확인
    @GetMapping("/check/{prdId}")
    public ResponseEntity<?> isWished(
            @AuthenticationPrincipal Long userNum,
            @PathVariable("prdId") Long prdId) {
        try {
            boolean result = wishService.isWished(userNum, prdId);
            return ResponseEntity.status(HttpStatus.OK).body(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("찜 여부 조회에 실패했습니다.");
        }
    }

    // 찜 추가
    @PostMapping
    public ResponseEntity<?> addWish(
            @AuthenticationPrincipal Long userNum,
            @RequestBody WishDTO dto) {
        try {
            Wish result = wishService.addWish(userNum, dto);
            return ResponseEntity.status(HttpStatus.OK).body(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(e.getMessage());
        }
    }

    // 찜 취소
    @DeleteMapping("/{prdId}")
    public ResponseEntity<?> deleteWish(
            @AuthenticationPrincipal Long userNum,
            @PathVariable("prdId") Long prdId) {
        try {
            wishService.deleteWish(userNum, prdId);
            return ResponseEntity.status(HttpStatus.OK).body("찜 취소 완료");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("찜 취소에 실패했습니다.");
        }
    }

    // 찜 토글
    @PostMapping("/toggle/{prdId}")
    public ResponseEntity<?> toggleWish(
            @AuthenticationPrincipal Long userNum,
            @PathVariable("prdId") Long prdId) {
        try {
            boolean result = wishService.toggleWish(userNum, prdId);
            return ResponseEntity.status(HttpStatus.OK).body(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("찜 처리에 실패했습니다.");
        }
    }
}