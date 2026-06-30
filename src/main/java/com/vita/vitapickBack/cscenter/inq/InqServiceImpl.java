package com.vita.vitapickBack.cscenter.inq;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InqServiceImpl implements InqService {

	private final InqRepository inqRepository;

	// 전체 1:1 문의 목록 조회
	@Override
	public List<Inq> getAllInq() {
		return inqRepository.findAll();
	}

	// 회원 본인 문의 목록 조회 (마이페이지)
	@Override
	public List<Inq> getMyInq(Long userNum) {
		return inqRepository.findByUserNum(userNum);
	}

	// 관리자 문의 상세 조회
	@Override
	public Inq selectOne(Long inqId) {
		Optional<Inq> result = inqRepository.findById(inqId);
		if (result.isPresent()) {
			return result.get();
		}
		throw new RuntimeException("문의 상세 정보를 불러오지 못했습니다.");
	}

	// 회원 본인 문의 상세 조회
	@Override
	public Inq selectMyOne(Long inqId, Long userNum) {
		Optional<Inq> result = inqRepository.findByInqIdAndUserNum(inqId, userNum);
		if (result.isPresent()) {
			return result.get();
		}
		throw new RuntimeException("문의 상세 정보를 불러오지 못했습니다.");
	}

	// 문의 등록
	@Override
	public Inq createInq(Inq inq, Long userNum) {
		// 제목 또는 내용 미입력 체크
		if (inq.getTtl() == null || inq.getTtl().isBlank() || inq.getInqTxt() == null || inq.getInqTxt().isBlank()) {
			throw new RuntimeException("문의 제목과 내용을 입력해주세요.");
		}

		// 토큰에서 꺼낸 회원번호 강제 세팅
		inq.setUserNum(userNum);

		// 기본값 세팅
		inq.setInqStCd("WAITING");
		inq.setViewCnt(0);
		inq.setAnsTxt(null);
		inq.setAnsAt(null);

		// DB 저장
		return inqRepository.save(inq);
	}

	// 회원 본인 문의 수정
	@Override
	public Inq updateInq(Long inqId, Long userNum, Inq inq) {
		Optional<Inq> result = inqRepository.findByInqIdAndUserNum(inqId, userNum);
		if (result.isPresent()) {
			Inq dbInq = result.get();
			dbInq.setInqTpCd(inq.getInqTpCd());
			dbInq.setTtl(inq.getTtl());
			dbInq.setInqTxt(inq.getInqTxt());
			return inqRepository.save(dbInq);
		}
		throw new RuntimeException("본인이 작성한 문의만 수정할 수 있습니다.");
	}

	// 회원 본인 문의 삭제
	@Override
	public void deleteInq(Long inqId, Long userNum) {
		Optional<Inq> result = inqRepository.findByInqIdAndUserNum(inqId, userNum);
		if (result.isPresent()) {
			Inq dbInq = result.get();
			inqRepository.delete(dbInq);
			return;
		}
		throw new RuntimeException("본인이 작성한 문의만 삭제할 수 있습니다.");
	}

	// 관리자 답변 등록
	@Override
	public Inq answerInq(Long inqId, String ansTxt) {
		if (ansTxt == null || ansTxt.isBlank()) {
			throw new RuntimeException("댓글 내용을 입력해주세요.");
		}

		Optional<Inq> result = inqRepository.findById(inqId);
		if (result.isPresent()) {
			Inq dbInq = result.get();
			dbInq.setAnsTxt(ansTxt);
			dbInq.setAnsAt(LocalDateTime.now());
			dbInq.setInqStCd("ANSWERED");
			return inqRepository.save(dbInq);
		}

		throw new RuntimeException("문의 상세 정보를 불러오지 못했습니다.");
	}

}