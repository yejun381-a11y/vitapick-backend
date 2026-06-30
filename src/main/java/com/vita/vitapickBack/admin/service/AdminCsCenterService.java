package com.vita.vitapickBack.admin.service;

import java.util.List;
import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vita.vitapickBack.admin.dto.AdminFaqUpdateRequestDTO;
import com.vita.vitapickBack.admin.dto.AdminCsNoticeRequestDTO;
import com.vita.vitapickBack.admin.dto.AdminCsInquiryAnswerRequestDTO;
import com.vita.vitapickBack.admin.dto.AdminCsInquiryDetailResponseDTO;
import com.vita.vitapickBack.admin.repository.AdminFaqRepository;
import com.vita.vitapickBack.admin.repository.AdminInqRepository;
import com.vita.vitapickBack.admin.repository.AdminNtcRepository;
import com.vita.vitapickBack.cscenter.faq.Faq;
import com.vita.vitapickBack.cscenter.faq.FaqDto;
import com.vita.vitapickBack.cscenter.inq.Inq;
import com.vita.vitapickBack.cscenter.ntc.Ntc;
import com.vita.vitapickBack.users.Users;
import com.vita.vitapickBack.users.UsersRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminCsCenterService {

    private final AdminFaqRepository adminFaqRepository;
    private final AdminNtcRepository adminNtcRepository;
    private final AdminInqRepository adminInqRepository;
    private final UsersRepository usersRepository;

    // 관리자 공지사항 목록 조회 조건을 처리한다.
    public Page<Ntc> getNotices(int page, int size, String useYn) {
        int safePage = Math.max(page, 0);
        int safeSize = size <= 0 ? 10 : Math.min(size, 100);
        Pageable pageable = PageRequest.of(safePage, safeSize,
                Sort.by(Sort.Direction.DESC, "crtAt").and(Sort.by(Sort.Direction.DESC, "ntcId")));

        Character noticeUseYn = useYn == null || useYn.isBlank() ? null : useYn.charAt(0);
        return adminNtcRepository.findAdminNotices(noticeUseYn, pageable);
    }

    // 관리자 FAQ 목록 조회 조건을 처리한다.
    // 관리자 공지사항 상세 정보를 조회한다.
    public Ntc getNotice(Long ntcId) {
        return adminNtcRepository.findById(ntcId)
                .orElseThrow(() -> new RuntimeException("Notice not found."));
    }

    // 관리자 공지사항 등록 데이터를 저장한다.
    @Transactional
    public Ntc createNotice(AdminCsNoticeRequestDTO request) {
        Ntc ntc = new Ntc();
        ntc.setTtl(request.getTtl());
        ntc.setNtcTxt(request.getNtcTxt());
        ntc.setViewCnt(0);
        ntc.setUseYn(request.getUseYn() == null ? 'Y' : request.getUseYn());

        return adminNtcRepository.save(ntc);
    }

    // 관리자 공지사항 수정 데이터를 저장한다.
    @Transactional
    public Ntc updateNotice(Long ntcId, AdminCsNoticeRequestDTO request) {
        Ntc ntc = adminNtcRepository.findById(ntcId)
                .orElseThrow(() -> new RuntimeException("Notice not found."));

        ntc.setTtl(request.getTtl());
        ntc.setNtcTxt(request.getNtcTxt());
        if (request.getUseYn() != null) {
            ntc.setUseYn(request.getUseYn());
        }

        return adminNtcRepository.save(ntc);
    }

    // 관리자 공지사항을 삭제한다.
    @Transactional
    public void deleteNotice(Long ntcId) {
        Ntc ntc = adminNtcRepository.findById(ntcId)
                .orElseThrow(() -> new RuntimeException("Notice not found."));
        adminNtcRepository.delete(ntc);
    }

    public Page<Faq> getFaqs(int page, int size, String useYn, String faqCtgCd, String sort) {
        int safePage = Math.max(page, 0);
        int safeSize = size <= 0 ? 10 : Math.min(size, 100);
        Pageable pageable = PageRequest.of(safePage, safeSize,
                Sort.by(Sort.Direction.DESC, "crtAt").and(Sort.by(Sort.Direction.DESC, "faqId")));

        List<String> faqCtgCds = toFaqCategoryCodes(faqCtgCd);
        if (faqCtgCds == null) {
            return adminFaqRepository.findAdminFaqs(useYn, pageable);
        }
        return adminFaqRepository.findAdminFaqsByCategoryCodes(useYn, faqCtgCds, pageable);
    }

    // 관리자 FAQ 상세 정보를 조회한다.
    public Faq getFaq(Long faqId) {
        return adminFaqRepository.findById(faqId)
                .orElseThrow(() -> new RuntimeException("FAQ not found."));
    }

    // 관리자 FAQ 등록 데이터를 저장한다.
    @Transactional
    public Faq createFaq(FaqDto dto) {
        Faq faq = new Faq();
        faq.setFaqCtgCd(dto.getFaqCtgCd());
        faq.setTtl(dto.getTtl());
        faq.setFaqTxt(dto.getFaqTxt());
        faq.setViewCnt(0);
        faq.setUseYn(dto.getUseYn() == null || dto.getUseYn().isBlank() ? "Y" : dto.getUseYn());

        return adminFaqRepository.save(faq);
    }

    // 관리자 FAQ 수정 데이터를 저장한다.
    @Transactional
    public Faq updateFaq(Long faqId, AdminFaqUpdateRequestDTO request) {
        Faq faq = adminFaqRepository.findById(faqId)
                .orElseThrow(() -> new RuntimeException("FAQ not found."));

        faq.setFaqCtgCd(request.getFaqCtgCd());
        faq.setTtl(request.getTtl());
        faq.setFaqTxt(request.getFaqTxt());
        if (request.getUseYn() != null && !request.getUseYn().isBlank()) {
            faq.setUseYn(request.getUseYn());
        }

        return adminFaqRepository.save(faq);
    }

    // 관리자 FAQ를 삭제한다.
    @Transactional
    public void deleteFaq(Long faqId) {
        Faq faq = adminFaqRepository.findById(faqId)
                .orElseThrow(() -> new RuntimeException("FAQ not found."));
        adminFaqRepository.delete(faq);
    }

    // 관리자 1:1 문의 상세 정보를 조회한다.
    public AdminCsInquiryDetailResponseDTO getInquiryDetail(Long inqId) {
        Inq inq = adminInqRepository.findByInqId(inqId)
                .orElseThrow(() -> new RuntimeException("Inquiry not found."));
        return toInquiryDetailResponse(inq);
    }

    // 관리자 1:1 문의 답변을 저장한다.
    @Transactional
    public AdminCsInquiryDetailResponseDTO answerInquiry(Long inqId, AdminCsInquiryAnswerRequestDTO request) {
        String ansTxt = request == null ? null : request.getAnsTxt();
        if (ansTxt == null || ansTxt.isBlank()) {
            throw new RuntimeException("Answer text is required.");
        }

        Inq inq = adminInqRepository.findByInqId(inqId)
                .orElseThrow(() -> new RuntimeException("Inquiry not found."));
        inq.setAnsTxt(ansTxt);
        inq.setAnsAt(LocalDateTime.now());
        inq.setInqStCd("ANSWERED");

        return toInquiryDetailResponse(adminInqRepository.save(inq));
    }

    // 관리자 1:1 문의 상세 응답 DTO로 변환한다.
    private AdminCsInquiryDetailResponseDTO toInquiryDetailResponse(Inq inq) {
        Users writer = usersRepository.findById(inq.getUserNum()).orElse(null);

        return AdminCsInquiryDetailResponseDTO.builder()
                .inquiryId(inq.getInqId())
                .userNum(inq.getUserNum())
                .writerId(writer == null ? null : writer.getLoginId())
                .writerName(writer == null ? null : writer.getUserNm())
                .title(inq.getTtl())
                .category(inq.getInqTpCd())
                .status(inq.getInqStCd())
                .inquiryText(inq.getInqTxt())
                .ansTxt(inq.getAnsTxt())
                .answeredAt(inq.getAnsAt())
                .createdAt(inq.getCrtAt())
                .updatedAt(inq.getUpdAt())
                .build();
    }

    // 관리자 FAQ 분류 검색 코드를 변환한다.
    private List<String> toFaqCategoryCodes(String faqCtgCd) {
        if (faqCtgCd == null || faqCtgCd.isBlank()) {
            return null;
        }
        return switch (faqCtgCd) {
            case "\uC8FC\uBB38/\uBC30\uC1A1" -> List.of("\uC8FC\uBB38/\uBC30\uC1A1", "\uC8FC\uBB38", "\uBC30\uC1A1", "ORDER_DELIVERY", "ORDER", "DELIVERY");
            case "\uC8FC\uBB38" -> List.of("\uC8FC\uBB38", "ORDER");
            case "\uBC30\uC1A1" -> List.of("\uBC30\uC1A1", "DELIVERY");
            case "\uC0C1\uD488" -> List.of("\uC0C1\uD488", "PRODUCT", "PRD");
            case "\uD68C\uC6D0" -> List.of("\uD68C\uC6D0", "USER", "MEMBER");
            case "\uAE30\uD0C0" -> List.of("\uAE30\uD0C0", "ETC");
            default -> List.of(faqCtgCd);
        };
    }
}
