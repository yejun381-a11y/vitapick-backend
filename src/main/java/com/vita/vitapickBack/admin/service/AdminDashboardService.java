package com.vita.vitapickBack.admin.service;

import java.io.IOException;

import com.vita.vitapickBack.admin.dto.DashboardSummaryDTO;

import jakarta.servlet.http.HttpServletResponse;

public interface AdminDashboardService {

    // 관리자 대시보드 요약 데이터를 조회한다.
    DashboardSummaryDTO getSummary();

    void downloadDashboardExcel(HttpServletResponse response) throws IOException;
}
