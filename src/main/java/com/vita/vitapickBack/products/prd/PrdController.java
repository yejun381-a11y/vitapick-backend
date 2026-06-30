package com.vita.vitapickBack.products.prd;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/product")
public class PrdController {

    private final PrdService prdService;
    
    // 전체 상품 조회
    @GetMapping("/list")
    public ResponseEntity<List<PrdDTO>> getAllProducts() {
        return ResponseEntity.ok(prdService.getAllProducts());
    }

    // 카테고리별 상품 조회
    @GetMapping("/list/category/{catCd}")
    public ResponseEntity<List<PrdDTO>> getPrdByCategory(@PathVariable("catCd") int catCd) {
        return ResponseEntity.ok(prdService.getPrdByCategory(catCd));
    }
    
    // 상품 상세 조회
    @GetMapping("/detail/{prdId}")
    public ResponseEntity<PrdDTO> getPrdDetail(@PathVariable("prdId") Long prdId) {
        return ResponseEntity.ok(prdService.getPrdDetail(prdId));
    }
    
    // 상품 검색
    @GetMapping("/search")
    public ResponseEntity<List<PrdDTO>> searchPrd(@RequestParam("keyword") String keyword) {
        return ResponseEntity.ok(prdService.searchPrd(keyword));
    }
    
    // 신상품 조회
    @GetMapping("/latest")
    public ResponseEntity<List<PrdDTO>> getNewProducts() {
        return ResponseEntity.ok(prdService.getNewProducts());
    }
    
    // 베스트 상품 조회
    @GetMapping("/best")
    public ResponseEntity<List<PrdDTO>> getBestProducts() {
        return ResponseEntity.ok(prdService.getBestProducts());
    }
}