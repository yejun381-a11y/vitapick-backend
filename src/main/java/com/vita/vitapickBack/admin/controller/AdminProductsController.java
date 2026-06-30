package com.vita.vitapickBack.admin.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vita.vitapickBack.admin.dto.AdminProductDetailDTO;
import com.vita.vitapickBack.admin.dto.AdminProductUpdateDTO;
import com.vita.vitapickBack.admin.dto.AdminProductsResponseDTO;
import com.vita.vitapickBack.admin.service.AdminProductsService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/products")
public class AdminProductsController {

    private final AdminProductsService adminProductsService;

    // 관리자 상품 목록 조회 API
    @GetMapping
    public ResponseEntity<AdminProductsResponseDTO> getProducts(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "status", required = false) String status,
            @RequestParam(name = "categoryId", required = false) Integer categoryId) {

        return ResponseEntity.ok(adminProductsService.getProducts(page, size, keyword, status, categoryId));
    }

    @GetMapping("/{prdId}")
    public ResponseEntity<AdminProductDetailDTO> getProductDetail(@PathVariable("prdId") Long prdId) {
        return ResponseEntity.ok(adminProductsService.getProductDetail(prdId));
    }

    @PatchMapping("/{prdId}")
    public ResponseEntity<AdminProductDetailDTO> updateProduct(
            @PathVariable("prdId") Long prdId,
            @RequestBody AdminProductUpdateDTO request) {

        return ResponseEntity.ok(adminProductsService.updateProduct(prdId, request));
    }
}
