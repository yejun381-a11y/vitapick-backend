package com.vita.vitapickBack.admin.controller;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vita.vitapickBack.admin.dto.AdminOrdersResponseDTO;
import com.vita.vitapickBack.admin.service.AdminOrdersService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/orders")
public class AdminOrdersController {

    private final AdminOrdersService adminOrdersService;

    // 관리자 주문 목록 조회 API
    @GetMapping
    public ResponseEntity<AdminOrdersResponseDTO> getOrders(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "categoryId", required = false) Integer categoryId,
            @RequestParam(name = "payMthdCd", required = false) String payMthdCd,
            @RequestParam(name = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(name = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        return ResponseEntity.ok(adminOrdersService.getOrders(page, size, keyword, categoryId, payMthdCd, startDate, endDate));
    }
}
