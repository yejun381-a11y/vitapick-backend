package com.vita.vitapickBack.products.prd_img;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/product")
public class PrdImgController {

    // 이미지 서비스 불러오는 애
    private final PrdImgService prdImgService;

    // 상품번호로 이미지 목록 조회
    @GetMapping("/images/{prdId}")
    public ResponseEntity<List<PrdImg>> findByPrdId(@PathVariable("prdId") Long prdId) {
        return ResponseEntity.ok(prdImgService.findByPrdId(prdId));
    }
}
