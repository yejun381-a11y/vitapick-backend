package com.vita.vitapickBack.admin.controller;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vita.vitapickBack.admin.dto.AdminFaqUpdateRequestDTO;
import com.vita.vitapickBack.admin.dto.AdminCsNoticeRequestDTO;
import com.vita.vitapickBack.admin.dto.AdminCsInquiryAnswerRequestDTO;
import com.vita.vitapickBack.admin.dto.AdminCsInquiryDetailResponseDTO;
import com.vita.vitapickBack.admin.dto.AdminCsInquiriesResponseDTO;
import com.vita.vitapickBack.admin.service.AdminCsCenterService;
import com.vita.vitapickBack.admin.service.AdminCsInquiriesService;
import com.vita.vitapickBack.cscenter.faq.Faq;
import com.vita.vitapickBack.cscenter.faq.FaqDto;
import com.vita.vitapickBack.cscenter.ntc.Ntc;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/cscenter")
public class AdminCsCenterController {

    private final AdminCsCenterService adminCsCenterService;
    private final AdminCsInquiriesService adminCsInquiriesService;

    // 관리자 공지사항 목록 조회 API
    @GetMapping("/notices")
    public ResponseEntity<Page<Ntc>> getNotices(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "useYn", required = false) String useYn,
            @RequestParam(name = "sort", defaultValue = "latest") String sort) {
        return ResponseEntity.ok(adminCsCenterService.getNotices(page, size, useYn));
    }

    // 관리자 FAQ 목록 조회 API
    // 관리자 공지사항 상세 조회 API
    @GetMapping("/notices/{ntcId}")
    public ResponseEntity<Ntc> getNotice(@PathVariable("ntcId") Long ntcId) {
        return ResponseEntity.ok(adminCsCenterService.getNotice(ntcId));
    }

    // 관리자 공지사항 등록 API
    @PostMapping("/notices")
    public ResponseEntity<Ntc> createNotice(@RequestBody AdminCsNoticeRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminCsCenterService.createNotice(request));
    }

    // 관리자 공지사항 수정 API
    @PatchMapping("/notices/{ntcId}")
    public ResponseEntity<Ntc> updateNotice(
            @PathVariable("ntcId") Long ntcId,
            @RequestBody AdminCsNoticeRequestDTO request) {
        return ResponseEntity.ok(adminCsCenterService.updateNotice(ntcId, request));
    }

    // 관리자 공지사항 삭제 API
    @DeleteMapping("/notices/{ntcId}")
    public ResponseEntity<Void> deleteNotice(@PathVariable("ntcId") Long ntcId) {
        adminCsCenterService.deleteNotice(ntcId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/faqs")
    public ResponseEntity<Page<Faq>> getFaqs(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "useYn", required = false) String useYn,
            @RequestParam(name = "faqCtgCd", required = false) String faqCtgCd,
            @RequestParam(name = "sort", defaultValue = "latest") String sort) {
        return ResponseEntity.ok(adminCsCenterService.getFaqs(page, size, useYn, faqCtgCd, sort));
    }

    // 관리자 FAQ 상세 조회 API
    @GetMapping("/faqs/{faqId}")
    public ResponseEntity<Faq> getFaq(@PathVariable("faqId") Long faqId) {
        return ResponseEntity.ok(adminCsCenterService.getFaq(faqId));
    }

    // 관리자 FAQ 등록 API
    @PostMapping("/faqs")
    public ResponseEntity<Faq> createFaq(@RequestBody FaqDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminCsCenterService.createFaq(dto));
    }

    // 관리자 FAQ 수정 API
    @PatchMapping("/faqs/{faqId}")
    public ResponseEntity<Faq> updateFaq(
            @PathVariable("faqId") Long faqId,
            @RequestBody AdminFaqUpdateRequestDTO request) {
        return ResponseEntity.ok(adminCsCenterService.updateFaq(faqId, request));
    }

    // 관리자 FAQ 삭제 API
    @DeleteMapping("/faqs/{faqId}")
    public ResponseEntity<Void> deleteFaq(@PathVariable("faqId") Long faqId) {
        adminCsCenterService.deleteFaq(faqId);
        return ResponseEntity.noContent().build();
    }

    // 관리자 1:1 문의 목록 조회 API
    @GetMapping("/inquiries")
    public ResponseEntity<AdminCsInquiriesResponseDTO> getInquiries(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "status", required = false) String status,
            @RequestParam(name = "type", required = false) String type,
            @RequestParam(name = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(name = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        return ResponseEntity.ok(adminCsInquiriesService.getInquiries(page, size, keyword, status, type, startDate, endDate));
    }

    // 관리자 1:1 문의 상세 조회 API
    @GetMapping("/inquiries/{inqId}")
    public ResponseEntity<AdminCsInquiryDetailResponseDTO> getInquiryDetail(@PathVariable("inqId") Long inqId) {
        return ResponseEntity.ok(adminCsCenterService.getInquiryDetail(inqId));
    }

    // 관리자 1:1 문의 답변 저장 API
    @PatchMapping("/inquiries/{inqId}/answer")
    public ResponseEntity<AdminCsInquiryDetailResponseDTO> answerInquiry(
            @PathVariable("inqId") Long inqId,
            @RequestBody AdminCsInquiryAnswerRequestDTO request) {
        return ResponseEntity.ok(adminCsCenterService.answerInquiry(inqId, request));
    }
}
