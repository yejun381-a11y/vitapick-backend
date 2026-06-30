package com.vita.vitapickBack.cscenter.faq;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FaqServiceImpl implements FaqService {

	private final FaqRepository faqRepository;

	// FAQ 전체 목록 조회
	// = 관리자 FAQ 목록 조회
	// = use_yn Y/N 전체 조회
	@Override
	public List<Faq> findFaqAll() {

		return faqRepository.findAll();
	}

	// FAQ 단건 조회
	@Override
	public Faq findOne(Long faqId) {

		return faqRepository.findById(faqId).orElseThrow(() -> new RuntimeException("FAQ 상세 정보를 불러오지 못했습니다."));
	}

	// FAQ 상세 조회
	@Override
	public Faq selectOne(Long faqId) {

		Faq dbFaq = faqRepository.findById(faqId).orElseThrow(() -> new RuntimeException("FAQ 상세 정보를 불러오지 못했습니다."));

		dbFaq.setViewCnt(dbFaq.getViewCnt() + 1);

		return faqRepository.save(dbFaq);
	}

	// FAQ 등록(관리자)
	@Override
	public Faq createFaq(FaqDto dto) {

		// 제목 또는 내용 미입력 체크
		if (dto.getTtl() == null || dto.getTtl().isBlank() || dto.getFaqTxt() == null || dto.getFaqTxt().isBlank()) {

			throw new RuntimeException("FAQ 제목과 내용을 입력해주세요.");
		}

		Faq faq = new Faq();

		faq.setFaqCtgCd(dto.getFaqCtgCd());
		faq.setTtl(dto.getTtl());
		faq.setFaqTxt(dto.getFaqTxt());
		faq.setViewCnt(0);
		faq.setUseYn(dto.getUseYn() == null || dto.getUseYn().isBlank() ? "Y" : dto.getUseYn());

		return faqRepository.save(faq);
	}

	// FAQ 수정(관리자)
	@Override
	public Faq updateFaq(Long faqId, FaqDto dto) {

		Faq dbFaq = faqRepository.findById(faqId).orElseThrow(() -> new RuntimeException("FAQ 상세 정보를 불러오지 못했습니다."));

		dbFaq.setFaqCtgCd(dto.getFaqCtgCd());
		dbFaq.setTtl(dto.getTtl());
		dbFaq.setFaqTxt(dto.getFaqTxt());

		return faqRepository.save(dbFaq);
	}

	// FAQ 삭제(관리자)
	@Override
	public void deleteFaq(Long faqId) {

		faqRepository.deleteById(faqId);
	}

	// FAQ 카테고리별 조회
	// = 관리자 카테고리별 FAQ 조회
	// = 해당 카테고리의 use_yn Y/N 전체 조회
	@Override
	public List<Faq> findByFaqCtgCd(String faqCtgCd) {

		return faqRepository.findByFaqCtgCd(faqCtgCd);
	}

	// use_yn = 'Y' FAQ 조회
	// = 일반회원 FAQ 목록 조회
	// = 공개 FAQ만 조회
	@Override
	public List<Faq> findByUseYn(String useYn) {

		return faqRepository.findByUseYn(useYn);
	}

	@Override
	public Page<Faq> findAdminFaqPage(int page, int size, String useYn, String sort) {
		int safePage = Math.max(page, 0);
		int safeSize = size <= 0 ? 10 : Math.min(size, 100);
		Sort.Direction direction = "oldest".equalsIgnoreCase(sort) ? Sort.Direction.ASC : Sort.Direction.DESC;
		Pageable pageable = PageRequest.of(safePage, safeSize, Sort.by(direction, "crtAt"));

		if (useYn == null || useYn.isBlank()) {
			return faqRepository.findAll(pageable);
		}

		return faqRepository.findByUseYn(useYn, pageable);
	}

	// FAQ 카테고리 + use_yn 조회
	// = 일반회원 카테고리별 FAQ 조회
	// = 공개 FAQ만 조회
	@Override
	public List<Faq> findByFaqCtgCdAndUseYn(String faqCtgCd, String useYn) {

		return faqRepository.findByFaqCtgCdAndUseYn(faqCtgCd, useYn);
	}
}
