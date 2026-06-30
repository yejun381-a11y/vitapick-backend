package com.vita.vitapickBack.admin.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vita.vitapickBack.admin.dto.DashboardSummaryDTO;
import com.vita.vitapickBack.admin.dto.DashboardSummaryDTO.InquiryStatsDTO;
import com.vita.vitapickBack.admin.dto.DashboardSummaryDTO.MemberStatsDTO;
import com.vita.vitapickBack.admin.dto.DashboardSummaryDTO.MonthlyCountDTO;
import com.vita.vitapickBack.admin.dto.DashboardSummaryDTO.PopularCategoryDTO;
import com.vita.vitapickBack.admin.dto.DashboardSummaryDTO.ProductSalesTopDTO;
import com.vita.vitapickBack.admin.repository.AdminFaqRepository;
import com.vita.vitapickBack.admin.repository.AdminInqRepository;
import com.vita.vitapickBack.admin.repository.AdminNtcRepository;
import com.vita.vitapickBack.admin.repository.AdminOrdItRepository;
import com.vita.vitapickBack.admin.repository.AdminOrdRepository;
import com.vita.vitapickBack.admin.repository.AdminPrdRepository;
import com.vita.vitapickBack.admin.repository.AdminRvwRepository;
import com.vita.vitapickBack.admin.repository.AdminUsersRepository;
import com.vita.vitapickBack.cscenter.faq.Faq;
import com.vita.vitapickBack.cscenter.inq.Inq;
import com.vita.vitapickBack.cscenter.ntc.Ntc;
import com.vita.vitapickBack.order.Ord;
import com.vita.vitapickBack.products.prd.Prd;
import com.vita.vitapickBack.products.rvw.Rvw;
import com.vita.vitapickBack.users.Users;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminDashboardServiceImpl implements AdminDashboardService {

    private static final String PAID = "PAID";
    private static final String ACTIVE = "ACTIVE";
    private static final String WITHDRAWN = "WITHDRAWN";
    private static final String WAITING = "WAITING";
    private static final String ANSWERED = "ANSWERED";
    private static final int DASHBOARD_MONTH_COUNT = 6;
    private static final int REPORT_LIMIT = 100;
    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");

    private static final Map<Integer, String> CATEGORY_NAMES = Map.ofEntries(
            Map.entry(1, "눈 건강"),
            Map.entry(2, "간 건강"),
            Map.entry(3, "장 건강"),
            Map.entry(4, "피로 회복"),
            Map.entry(5, "면역"),
            Map.entry(6, "피부 건강"),
            Map.entry(7, "혈행 건강"),
            Map.entry(8, "관절 건강"),
            Map.entry(9, "여성 건강"),
            Map.entry(10, "남성 건강"));

    private final AdminOrdRepository adminOrdRepository;
    private final AdminOrdItRepository adminOrdItRepository;
    private final AdminUsersRepository adminUsersRepository;
    private final AdminInqRepository adminInqRepository;
    private final AdminPrdRepository adminPrdRepository;
    private final AdminRvwRepository adminRvwRepository;
    private final AdminNtcRepository adminNtcRepository;
    private final AdminFaqRepository adminFaqRepository;

    // 관리자 대시보드 요약 데이터를 조회한다.
    @Override
    public DashboardSummaryDTO getSummary() {
        LocalDate today = LocalDate.now();
        LocalDateTime todayStart = today.atStartOfDay();
        LocalDateTime tomorrowStart = today.plusDays(1).atStartOfDay();
        LocalDateTime monthStart = YearMonth.from(today).atDay(1).atStartOfDay();
        LocalDateTime nextMonthStart = YearMonth.from(today).plusMonths(1).atDay(1).atStartOfDay();
        LocalDateTime monthlyStart = YearMonth.from(today).minusMonths(DASHBOARD_MONTH_COUNT - 1).atDay(1).atStartOfDay();

        Long todaySalesAmt = defaultLong(
                adminOrdRepository.sumTotalAmtByOrdStCdAndCrtAtRange(PAID, todayStart, tomorrowStart));
        Long monthSalesAmt = defaultLong(
                adminOrdRepository.sumTotalAmtByOrdStCdAndCrtAtRange(PAID, monthStart, nextMonthStart));
        Long todayPaidOrderCount = defaultLong(
                adminOrdRepository.countByOrdStCdAndCrtAtGreaterThanEqualAndCrtAtLessThan(PAID, todayStart, tomorrowStart));

        return DashboardSummaryDTO.builder()
                .todaySalesAmt(todaySalesAmt)
                .monthSalesAmt(monthSalesAmt)
                .todayPaidOrderCount(todayPaidOrderCount)
                .popularCategory(getPopularCategory(monthStart, nextMonthStart))
                .productSalesTop5(getProductSalesTop5(monthStart, nextMonthStart))
                .inquiryStats(getInquiryStats(todayStart, tomorrowStart))
                .memberStats(getMemberStats())
                .monthlyNewUsers(getMonthlyNewUsers(monthlyStart, nextMonthStart))
                .monthlyPaidOrders(getMonthlyPaidOrders(monthlyStart, nextMonthStart))
                .build();
    }

    @Override
    public void downloadDashboardExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=admin_report.xlsx");

        try (Workbook workbook = new XSSFWorkbook()) {
            ReportStyles styles = createReportStyles(workbook);
            DashboardSummaryDTO summary = getSummary();

            writeDashboardSheet(workbook, styles, summary);
            writeUsersSheet(workbook, styles);
            writeProductsSheet(workbook, styles);
            writeOrdersSheet(workbook, styles);
            writeReviewsSheet(workbook, styles);
            writeCsCenterSheet(workbook, styles);

            workbook.write(response.getOutputStream());
        }
    }

    private void writeDashboardSheet(Workbook workbook, ReportStyles styles, DashboardSummaryDTO summary) {
        Sheet sheet = workbook.createSheet("Dashboard");
        int rowNum = 0;

        rowNum = writeHeader(sheet, rowNum, styles, "항목", "값");
        rowNum = writeRow(sheet, rowNum, styles, "오늘 매출", money(summary == null ? null : summary.getTodaySalesAmt()));
        rowNum = writeRow(sheet, rowNum, styles, "이번 달 매출", money(summary == null ? null : summary.getMonthSalesAmt()));
        rowNum = writeRow(sheet, rowNum, styles, "오늘 결제완료 주문 수", numberValue(summary == null ? null : summary.getTodayPaidOrderCount()));

        PopularCategoryDTO popularCategory = summary == null ? null : summary.getPopularCategory();
        rowNum = writeRow(sheet, rowNum, styles, "인기 카테고리", popularCategory == null ? "" : text(popularCategory.getCatNm()));

        InquiryStatsDTO inquiryStats = summary == null ? null : summary.getInquiryStats();
        rowNum = writeRow(sheet, rowNum, styles, "문의 처리율(%)", percent(inquiryStats == null ? null : inquiryStats.getAnswerRate()));

        rowNum++;
        rowNum = writeSectionTitle(sheet, rowNum, styles, "월별 신규 회원", 2);
        rowNum = writeHeader(sheet, rowNum, styles, "월", "회원 수");
        for (MonthlyCountDTO item : emptyList(summary == null ? null : summary.getMonthlyNewUsers())) {
            rowNum = writeRow(sheet, rowNum, styles, text(item.getMonth()), numberValue(item.getCount()));
        }

        rowNum++;
        rowNum = writeSectionTitle(sheet, rowNum, styles, "월별 결제완료 주문", 2);
        rowNum = writeHeader(sheet, rowNum, styles, "월", "주문 수");
        for (MonthlyCountDTO item : emptyList(summary == null ? null : summary.getMonthlyPaidOrders())) {
            rowNum = writeRow(sheet, rowNum, styles, text(item.getMonth()), numberValue(item.getCount()));
        }

        rowNum++;
        rowNum = writeSectionTitle(sheet, rowNum, styles, "상품별 매출 TOP5", 5);
        rowNum = writeHeader(sheet, rowNum, styles, "상품번호", "상품명", "카테고리", "판매수량", "매출액");
        for (ProductSalesTopDTO item : emptyList(summary == null ? null : summary.getProductSalesTop5())) {
            rowNum = writeRow(sheet, rowNum, styles,
                    numberValue(item.getPrdId()),
                    text(item.getPrdNm()),
                    text(item.getCatNm()),
                    numberValue(item.getPaidQty()),
                    money(item.getSalesAmt()));
        }
        finishSheet(sheet, 5000, 9000, 5000, 4000, 5000);
    }

    private void writeUsersSheet(Workbook workbook, ReportStyles styles) {
        Sheet sheet = workbook.createSheet("Users");
        int rowNum = writeHeader(sheet, 0, styles, "회원번호", "아이디", "닉네임", "권한", "상태", "가입일");
        Page<Users> users = adminUsersRepository.findAdminUsers(null, null, null, null,
                PageRequest.of(0, REPORT_LIMIT, Sort.by(Sort.Direction.DESC, "crtAt")));

        for (Users user : users.getContent()) {
            rowNum = writeRow(sheet, rowNum, styles,
                    numberValue(user.getUserNum()),
                    text(user.getLoginId()),
                    text(user.getUserNm()),
                    text(user.getRoleCd()),
                    text(user.getStatusCd()),
                    dateValue(user.getCrtAt()));
        }
        finishListSheet(sheet, rowNum, 3500, 5000, 5000, 3500, 3500, 4000);
    }

    private void writeProductsSheet(Workbook workbook, ReportStyles styles) {
        Sheet sheet = workbook.createSheet("Products");
        int rowNum = writeHeader(sheet, 0, styles, "상품번호", "상품명", "제조사", "카테고리", "상태", "가격", "등록일");
        Page<Prd> products = adminPrdRepository.findAdminProducts(null, null, null,
                PageRequest.of(0, REPORT_LIMIT, Sort.by(Sort.Direction.DESC, "crtAt")));

        for (Prd product : products.getContent()) {
            rowNum = writeRow(sheet, rowNum, styles,
                    numberValue(product.getPrdId()),
                    text(product.getPrdNm()),
                    text(product.getBrand()),
                    text(getCategoryName(product.getCatCd())),
                    text(product.getUseYn()),
                    money(product.getPrice()),
                    dateValue(product.getCrtAt()));
        }
        finishListSheet(sheet, rowNum, 3500, 9000, 6000, 5000, 3000, 4000, 4000);
    }

    private void writeOrdersSheet(Workbook workbook, ReportStyles styles) {
        Sheet sheet = workbook.createSheet("Orders");
        int rowNum = writeHeader(sheet, 0, styles, "주문번호", "구매자", "상품명", "결제수단", "결제금액", "주문일");
        Page<Ord> orders = adminOrdRepository.findAdminOrders(null, null, null, null, null,
                PageRequest.of(0, REPORT_LIMIT, Sort.by(Sort.Direction.DESC, "crtAt")));

        for (Ord order : orders.getContent()) {
            Users buyer = order.getUserNum() == null ? null : adminUsersRepository.findById(order.getUserNum()).orElse(null);
            rowNum = writeRow(sheet, rowNum, styles,
                    numberValue(order.getOrdId()),
                    userText(buyer),
                    text(getOrderProductName(order.getOrdId())),
                    text(adminOrdRepository.findPayMthdCdByOrdId(order.getOrdId())),
                    money(order.getTotalAmt()),
                    dateValue(order.getCrtAt()));
        }
        finishListSheet(sheet, rowNum, 3500, 7000, 9000, 4000, 4500, 4000);
    }

    private void writeReviewsSheet(Workbook workbook, ReportStyles styles) {
        Sheet sheet = workbook.createSheet("Reviews");
        int rowNum = writeHeader(sheet, 0, styles, "리뷰번호", "상품명", "작성자", "평점", "리뷰내용", "답글여부", "작성일");
        Page<Rvw> reviews = adminRvwRepository.findAdminReviews(null, null, null, null,
                PageRequest.of(0, REPORT_LIMIT, Sort.by(Sort.Direction.DESC, "crtAt")));

        for (Rvw review : reviews.getContent()) {
            Prd product = review.getPrdId() == null ? null : adminPrdRepository.findById(review.getPrdId()).orElse(null);
            Users writer = review.getUserNum() == null ? null : adminUsersRepository.findById(review.getUserNum()).orElse(null);
            rowNum = writeRow(sheet, rowNum, styles,
                    numberValue(review.getRvwId()),
                    text(product == null ? null : product.getPrdNm()),
                    userText(writer),
                    numberValue(review.getRating()),
                    text(review.getCmt()),
                    hasText(review.getReplyTxt()) ? "Y" : "N",
                    dateValue(review.getCrtAt()));
        }
        finishListSheet(sheet, rowNum, 3500, 9000, 7000, 3000, 14000, 3500, 4000);
    }

    private void writeCsCenterSheet(Workbook workbook, ReportStyles styles) {
        Sheet sheet = workbook.createSheet("CsCenter");
        int rowNum = 0;

        rowNum = writeSectionTitle(sheet, rowNum, styles, "공지사항", 4);
        rowNum = writeHeader(sheet, rowNum, styles, "제목", "작성자", "공개여부", "작성일");
        Page<Ntc> notices = adminNtcRepository.findAdminNotices(null,
                PageRequest.of(0, REPORT_LIMIT, Sort.by(Sort.Direction.DESC, "crtAt")));
        for (Ntc notice : notices.getContent()) {
            rowNum = writeRow(sheet, rowNum, styles,
                    text(notice.getTtl()),
                    "-",
                    text(notice.getUseYn()),
                    dateValue(notice.getCrtAt()));
        }

        rowNum++;
        rowNum = writeSectionTitle(sheet, rowNum, styles, "FAQ", 4);
        rowNum = writeHeader(sheet, rowNum, styles, "제목", "카테고리", "공개여부", "작성일");
        Page<Faq> faqs = adminFaqRepository.findAdminFaqs(null,
                PageRequest.of(0, REPORT_LIMIT, Sort.by(Sort.Direction.DESC, "crtAt")));
        for (Faq faq : faqs.getContent()) {
            rowNum = writeRow(sheet, rowNum, styles,
                    text(faq.getTtl()),
                    text(faq.getFaqCtgCd()),
                    text(faq.getUseYn()),
                    dateValue(faq.getCrtAt()));
        }

        rowNum++;
        rowNum = writeSectionTitle(sheet, rowNum, styles, "1:1 문의", 4);
        rowNum = writeHeader(sheet, rowNum, styles, "제목", "작성자", "답변상태", "작성일");
        Page<Inq> inquiries = adminInqRepository.findAdminInquiries(null, null, null, null, null,
                PageRequest.of(0, REPORT_LIMIT));
        for (Inq inquiry : inquiries.getContent()) {
            Users writer = inquiry.getUserNum() == null ? null : adminUsersRepository.findById(inquiry.getUserNum()).orElse(null);
            rowNum = writeRow(sheet, rowNum, styles,
                    text(inquiry.getTtl()),
                    userText(writer),
                    text(inquiry.getInqStCd()),
                    dateValue(inquiry.getCrtAt()));
        }
        finishSheet(sheet, 12000, 7000, 3500, 4000);
    }

    // 결제완료 주문 기준 인기 카테고리를 집계한다.
    private PopularCategoryDTO getPopularCategory(LocalDateTime monthStart, LocalDateTime nextMonthStart) {
        List<Object[]> rows = adminOrdItRepository.findPopularCategorySales(monthStart, nextMonthStart);
        if (rows.isEmpty()) {
            return PopularCategoryDTO.builder()
                    .catCd(null)
                    .catNm(null)
                    .salesAmt(0L)
                    .orderQty(0L)
                    .build();
        }

        Object[] row = rows.get(0);
        Integer catCd = toInteger(row[0]);
        return PopularCategoryDTO.builder()
                .catCd(catCd)
                .catNm(getCategoryName(catCd))
                .salesAmt(toLong(row[1]))
                .orderQty(toLong(row[2]))
                .build();
    }

    // 결제완료 주문 기준 상품 매출 상위 목록을 집계한다.
    private List<ProductSalesTopDTO> getProductSalesTop5(LocalDateTime monthStart, LocalDateTime nextMonthStart) {
        return adminOrdItRepository.findMonthlyProductSalesTop5(monthStart, nextMonthStart).stream()
                .map(row -> {
                    Integer catCd = toInteger(row[2]);
                    return ProductSalesTopDTO.builder()
                            .prdId(toLong(row[0]))
                            .prdNm(row[1] == null ? null : row[1].toString())
                            .catCd(catCd)
                            .catNm(getCategoryName(catCd))
                            .paidQty(toLong(row[3]))
                            .salesAmt(toLong(row[4]))
                            .build();
                })
                .toList();
    }

    // 관리자 1:1 문의 상태 통계를 집계한다.
    private InquiryStatsDTO getInquiryStats(LocalDateTime todayStart, LocalDateTime tomorrowStart) {
        Long waitingCount = defaultLong(adminInqRepository.countByInqStCd(WAITING));
        Long answeredCount = defaultLong(adminInqRepository.countByInqStCd(ANSWERED));
        Long todayNewCount = defaultLong(
                adminInqRepository.countByCrtAtGreaterThanEqualAndCrtAtLessThan(todayStart, tomorrowStart));
        Long totalCount = waitingCount + answeredCount;
        Double answerRate = totalCount == 0L ? 0.0 : answeredCount * 100.0 / totalCount;

        return InquiryStatsDTO.builder()
                .waitingCount(waitingCount)
                .answeredCount(answeredCount)
                .todayNewCount(todayNewCount)
                .answerRate(answerRate)
                .build();
    }

    // 관리자 회원 상태 통계를 집계한다.
    private MemberStatsDTO getMemberStats() {
        return MemberStatsDTO.builder()
                .totalCount(adminUsersRepository.count())
                .activeCount(defaultLong(adminUsersRepository.countByStatusCd(ACTIVE)))
                .withdrawnCount(defaultLong(adminUsersRepository.countByStatusCd(WITHDRAWN)))
                .build();
    }

    // 관리자 대시보드 월별 신규 회원 차트 데이터를 조회한다.
    private List<MonthlyCountDTO> getMonthlyNewUsers(LocalDateTime startAt, LocalDateTime endAt) {
        return toMonthlyCounts(adminUsersRepository.findMonthlyNewUserCounts(startAt, endAt), startAt);
    }

    // 관리자 대시보드 월별 결제완료 주문 차트 데이터를 조회한다.
    private List<MonthlyCountDTO> getMonthlyPaidOrders(LocalDateTime startAt, LocalDateTime endAt) {
        return toMonthlyCounts(adminOrdRepository.findMonthlyPaidOrderCounts(startAt, endAt), startAt);
    }

    // 월별 차트의 누락된 월을 0건으로 채운다.
    private List<MonthlyCountDTO> toMonthlyCounts(List<Object[]> rows, LocalDateTime startAt) {
        Map<String, Long> countByMonth = rows.stream()
                .collect(Collectors.toMap(
                        row -> row[0].toString(),
                        row -> toLong(row[1]),
                        Long::sum));
        Map<String, Long> recentMonths = new LinkedHashMap<>();
        YearMonth startMonth = YearMonth.from(startAt);
        for (int i = 0; i < DASHBOARD_MONTH_COUNT; i++) {
            String month = startMonth.plusMonths(i).format(MONTH_FORMATTER);
            recentMonths.put(month, countByMonth.getOrDefault(month, 0L));
        }
        return recentMonths.entrySet().stream()
                .map(entry -> MonthlyCountDTO.builder()
                        .month(entry.getKey())
                        .count(entry.getValue())
                        .build())
                .toList();
    }

    // 관리자 상품 카테고리명을 조회한다.
    private String getCategoryName(Integer catCd) {
        if (catCd == null) {
            return null;
        }
        return CATEGORY_NAMES.getOrDefault(catCd, "카테고리 " + catCd);
    }

    private ReportStyles createReportStyles(Workbook workbook) {
        DataFormat dataFormat = workbook.createDataFormat();
        CellStyle dataStyle = workbook.createCellStyle();
        applyBaseStyle(dataStyle);

        CellStyle headerStyle = workbook.createCellStyle();
        applyBaseStyle(headerStyle);
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        CellStyle sectionStyle = workbook.createCellStyle();
        applyBaseStyle(sectionStyle);
        Font sectionFont = workbook.createFont();
        sectionFont.setBold(true);
        sectionFont.setFontHeightInPoints((short) 12);
        sectionStyle.setFont(sectionFont);
        sectionStyle.setFillForegroundColor(IndexedColors.LEMON_CHIFFON.getIndex());
        sectionStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        CellStyle numberStyle = workbook.createCellStyle();
        numberStyle.cloneStyleFrom(dataStyle);
        numberStyle.setDataFormat(dataFormat.getFormat("#,##0"));

        CellStyle moneyStyle = workbook.createCellStyle();
        moneyStyle.cloneStyleFrom(dataStyle);
        moneyStyle.setDataFormat(dataFormat.getFormat("#,##0"));

        CellStyle percentStyle = workbook.createCellStyle();
        percentStyle.cloneStyleFrom(dataStyle);
        percentStyle.setDataFormat(dataFormat.getFormat("0.0"));

        CellStyle dateStyle = workbook.createCellStyle();
        dateStyle.cloneStyleFrom(dataStyle);
        dateStyle.setDataFormat(dataFormat.getFormat("yyyy-mm-dd"));

        return new ReportStyles(headerStyle, sectionStyle, dataStyle, numberStyle, moneyStyle, percentStyle, dateStyle);
    }

    private void applyBaseStyle(CellStyle style) {
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setWrapText(true);
    }

    private int writeSectionTitle(Sheet sheet, int rowNum, ReportStyles styles, String title, int columnCount) {
        Row row = sheet.createRow(rowNum++);
        for (int i = 0; i < columnCount; i++) {
            Cell cell = row.createCell(i);
            cell.setCellStyle(styles.sectionStyle);
            if (i == 0) {
                cell.setCellValue(title);
            }
        }
        return rowNum;
    }

    private int writeHeader(Sheet sheet, int rowNum, ReportStyles styles, String... values) {
        Row row = sheet.createRow(rowNum++);
        for (int i = 0; i < values.length; i++) {
            row.createCell(i).setCellValue(values[i]);
            row.getCell(i).setCellStyle(styles.headerStyle);
        }
        return rowNum;
    }

    private int writeRow(Sheet sheet, int rowNum, ReportStyles styles, Object... values) {
        Row row = sheet.createRow(rowNum++);
        for (int i = 0; i < values.length; i++) {
            writeCell(row.createCell(i), values[i], styles);
        }
        return rowNum;
    }

    private void writeCell(Cell cell, Object value, ReportStyles styles) {
        if (value instanceof ReportNumber reportNumber) {
            cell.setCellValue(reportNumber.value == null ? 0L : reportNumber.value.doubleValue());
            cell.setCellStyle(styles.numberStyle);
            return;
        }
        if (value instanceof ReportMoney reportMoney) {
            cell.setCellValue(reportMoney.value == null ? 0L : reportMoney.value.doubleValue());
            cell.setCellStyle(styles.moneyStyle);
            return;
        }
        if (value instanceof ReportPercent reportPercent) {
            cell.setCellValue(reportPercent.value == null ? 0.0 : reportPercent.value);
            cell.setCellStyle(styles.percentStyle);
            return;
        }
        if (value instanceof ReportDate reportDate) {
            if (reportDate.value == null) {
                cell.setCellValue("");
                cell.setCellStyle(styles.dataStyle);
                return;
            }
            cell.setCellValue(reportDate.value);
            cell.setCellStyle(styles.dateStyle);
            return;
        }
        cell.setCellValue(value == null ? "" : value.toString());
        cell.setCellStyle(styles.dataStyle);
    }

    private void finishListSheet(Sheet sheet, int rowNum, int... minWidths) {
        if (rowNum > 1 && minWidths.length > 0) {
            sheet.setAutoFilter(new CellRangeAddress(0, rowNum - 1, 0, minWidths.length - 1));
        }
        sheet.createFreezePane(0, 1);
        finishSheet(sheet, minWidths);
    }

    private void finishSheet(Sheet sheet, int... minWidths) {
        for (int i = 0; i < minWidths.length; i++) {
            sheet.autoSizeColumn(i);
            if (sheet.getColumnWidth(i) < minWidths[i]) {
                sheet.setColumnWidth(i, minWidths[i]);
            }
        }
    }

    private String getOrderProductName(Long ordId) {
        if (ordId == null) {
            return null;
        }
        List<String> productNames = adminOrdItRepository.findProductNamesByOrdId(ordId);
        if (productNames.isEmpty()) {
            return null;
        }
        String firstName = productNames.get(0);
        if (productNames.size() == 1) {
            return firstName;
        }
        return firstName + " +" + (productNames.size() - 1);
    }

    private String userText(Users user) {
        if (user == null) {
            return "";
        }
        if (hasText(user.getLoginId()) && hasText(user.getUserNm())) {
            return user.getLoginId() + " / " + user.getUserNm();
        }
        if (hasText(user.getLoginId())) {
            return user.getLoginId();
        }
        return text(user.getUserNm());
    }

    private <T> List<T> emptyList(List<T> values) {
        return values == null ? List.of() : values;
    }

    private String text(Object value) {
        return value == null ? "" : value.toString();
    }

    private ReportNumber numberValue(Number value) {
        return new ReportNumber(value);
    }

    private ReportMoney money(Number value) {
        return new ReportMoney(value);
    }

    private ReportPercent percent(Double value) {
        return new ReportPercent(value);
    }

    private ReportDate dateValue(LocalDateTime value) {
        return new ReportDate(value);
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private static class ReportStyles {
        private final CellStyle headerStyle;
        private final CellStyle sectionStyle;
        private final CellStyle dataStyle;
        private final CellStyle numberStyle;
        private final CellStyle moneyStyle;
        private final CellStyle percentStyle;
        private final CellStyle dateStyle;

        private ReportStyles(
                CellStyle headerStyle,
                CellStyle sectionStyle,
                CellStyle dataStyle,
                CellStyle numberStyle,
                CellStyle moneyStyle,
                CellStyle percentStyle,
                CellStyle dateStyle) {
            this.headerStyle = headerStyle;
            this.sectionStyle = sectionStyle;
            this.dataStyle = dataStyle;
            this.numberStyle = numberStyle;
            this.moneyStyle = moneyStyle;
            this.percentStyle = percentStyle;
            this.dateStyle = dateStyle;
        }
    }

    private static class ReportNumber {
        private final Number value;

        private ReportNumber(Number value) {
            this.value = value;
        }
    }

    private static class ReportMoney {
        private final Number value;

        private ReportMoney(Number value) {
            this.value = value;
        }
    }

    private static class ReportPercent {
        private final Double value;

        private ReportPercent(Double value) {
            this.value = value;
        }
    }

    private static class ReportDate {
        private final LocalDateTime value;

        private ReportDate(LocalDateTime value) {
            this.value = value;
        }
    }

    // null 숫자 값을 0으로 변환한다.
    private Long defaultLong(Long value) {
        return value == null ? 0L : value;
    }

    // 집계 결과 값을 Long으로 변환한다.
    private Long toLong(Object value) {
        if (value == null) {
            return 0L;
        }
        if (value instanceof BigInteger bigInteger) {
            return bigInteger.longValue();
        }
        if (value instanceof BigDecimal bigDecimal) {
            return bigDecimal.longValue();
        }
        if (value instanceof Number number) {
            return number.longValue();
        }
        return Long.valueOf(value.toString());
    }

    // 집계 결과 값을 Integer로 변환한다.
    private Integer toInteger(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number number) {
            return number.intValue();
        }
        return Integer.valueOf(value.toString());
    }
}
