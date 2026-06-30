package com.vita.vitapickBack.admin.controller;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vita.vitapickBack.admin.service.AdminDashboardService;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/dashboard")
public class AdminDashboardController {

    private final AdminDashboardService adminDashboardService;

    // 관리자 대시보드 요약 데이터 조회 API
    @GetMapping("/summary")
    public ResponseEntity<?> getSummary(Authentication authentication) {
        if (!isAdmin(authentication)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("관리자만 이용할 수 있습니다.");
        }

        return ResponseEntity.ok(adminDashboardService.getSummary());
    }

    @GetMapping("/excel")
    public void downloadDashboardExcel(Authentication authentication, HttpServletResponse response) throws IOException {
        if (!isAdmin(authentication)) {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            return;
        }

        adminDashboardService.downloadDashboardExcel(response);
    }

    // 관리자 권한 확인
    private boolean isAdmin(Authentication authentication) {
        if (authentication == null) {
            return false;
        }
        return authentication.getAuthorities().stream()
                .anyMatch(auth -> "ROLE_ADMIN".equals(auth.getAuthority()));
    }
}
