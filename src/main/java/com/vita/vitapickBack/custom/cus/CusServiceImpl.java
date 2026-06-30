package com.vita.vitapickBack.custom.cus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vita.vitapickBack.custom.cusIt.CusIt;
import com.vita.vitapickBack.custom.cusIt.CusItDTO;
import com.vita.vitapickBack.custom.cusIt.CusItRepository;
import com.vita.vitapickBack.custom.sur.Sur;
import com.vita.vitapickBack.custom.sur.SurRepository;
import com.vita.vitapickBack.products.prd.Prd;
import com.vita.vitapickBack.products.prd.PrdRepository;
import com.vita.vitapickBack.products.prd_img.PrdImgRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
@RequiredArgsConstructor
public class CusServiceImpl implements CusService {

	private final CusRepository cusRepository;
	private final CusItRepository cusItRepository;
	private final SurRepository surRepository;
	private final PrdRepository prdRepository;
	private final PrdImgRepository prdImgRepository;
	private final OpenAiChatModel openAiChatModel;
	private final ObjectMapper objectMapper = new ObjectMapper();

	// 1. 설문 기반 AI 추천 실행 및 저장
	@Override
	@Transactional
	public CusDTO recommend(Long surId, Long userNum) {

		// 1-1) sur 테이블에서 ansJson 조회
		Sur sur = surRepository.findById(surId)
				.orElseThrow(() -> new RuntimeException("설문을 찾을 수 없습니다. surId=" + surId));

		// 1-2) ansJson -> GPT 프롬프트 변환
		String promptText = buildPrompt(sur.getAnsJson());
		log.info("** [CUS] GPT 프롬프트 생성 완료 surId={}", surId);

		// 1-3) GPT 호출
		String gptRaw = openAiChatModel.call(promptText);
		log.info("** [CUS] GPT 응답 수신 surId={}", surId);
		log.debug("** [CUS] GPT raw =\n{}", gptRaw);

		// 1-4) GPT 응답(JSON) 파싱
		GptResult gptResult = parseGptResponse(gptRaw);

		// 1-5) cus 저장
		Cus cus = Cus.builder()
				.surId(surId)
				.surTitle(sur.getSurTitle())
				.userNum(userNum)
				.aiModel("gpt-4o-mini")
				.cusSum(gptResult.summary)
				.cusReason(gptResult.reason)
				.cusDos(gptResult.dosage)
				.cusCaution(gptResult.caution)
				.crtAt(LocalDateTime.now())
				.build();
		Cus savedCus = cusRepository.save(cus);
		log.info("** [CUS] cus 저장 완료 cusId={}", savedCus.getCusId());

		// 1-6) cus_it 저장 (추천 상품 최대 5개)
		List<CusItDTO> itemDTOList = new ArrayList<>();
		int sortNum = 1;
		for (GptResult.ProductItem item : gptResult.products) {
			if (sortNum > 5) break;

			CusIt cusIt = CusIt.builder()
					.cusId(savedCus.getCusId())
					.surTitle(savedCus.getSurTitle())
					.prdId(item.prdId)
					.sortNum(sortNum)
					.build();
			CusIt savedItem = cusItRepository.save(cusIt);

			itemDTOList.add(fetchPrdDetail(savedItem.getCusItId(), item.prdId, sortNum, savedItem.getSurTitle()));
			sortNum++;
		}
		log.info("** [CUS] cus_it 저장 완료 {}건 cusId={}", itemDTOList.size(), savedCus.getCusId());

		return CusDTO.builder()
				.cusId(savedCus.getCusId())
				.surId(surId)
				.surTitle(savedCus.getSurTitle())
				.userNum(userNum)
				.aiModel("gpt-4o-mini")
				.cusSum(gptResult.summary)
				.cusReason(gptResult.reason)
				.cusDos(gptResult.dosage)
				.cusCaution(gptResult.caution)
				.crtAt(savedCus.getCrtAt())
				.items(itemDTOList)
				.message("AI 추천이 완료되었습니다.")
				.build();
	}

	// 2. 추천 결과 상세 조회
	@Override
	public CusDTO getCusDetail(Long cusId, Long userNum) {

		Cus cus = cusRepository.findById(cusId)
				.orElseThrow(() -> new RuntimeException("추천 결과를 찾을 수 없습니다. cusId=" + cusId));
		
		if(!cus.getUserNum().equals(userNum)) {
			throw new RuntimeException("접근 권한이 없습니다.");
		}
		List<CusIt> cusItList = cusItRepository.findByCusIdOrderBySortNumAsc(cusId);

		List<CusItDTO> itemDTOList = cusItList.stream()
				.map(it -> fetchPrdDetail(it.getCusItId(), it.getPrdId(), it.getSortNum(), it.getSurTitle()))
				.collect(Collectors.toList());

		return CusDTO.builder()
				.cusId(cus.getCusId())
				.surId(cus.getSurId())
				.surTitle(cus.getSurTitle())
				.userNum(cus.getUserNum())
				.aiModel(cus.getAiModel())
				.cusSum(cus.getCusSum())
				.cusReason(cus.getCusReason())
				.cusDos(cus.getCusDos())
				.cusCaution(cus.getCusCaution())
				.crtAt(cus.getCrtAt())
				.items(itemDTOList)
				.build();
	}

	// 3. 내 추천 목록 조회
	@Override
	public List<CusDTO> getCusList(Long userNum) {

		List<Cus> cusList = cusRepository.findByUserNumOrderByCusIdDesc(userNum);

		List<CusDTO> dtoList = new ArrayList<>();
		for (Cus cus : cusList) {
			CusDTO dto = CusDTO.builder()
					.cusId(cus.getCusId())
					.surId(cus.getSurId())
					.surTitle(cus.getSurTitle())
					.userNum(cus.getUserNum())
					.cusSum(cus.getCusSum())
					.crtAt(cus.getCrtAt())
					.build();
			dtoList.add(dto);
		}
		return dtoList;
	}
	
	// 4. 내 추천 삭제
	@Override
	@Transactional
	public void deleteCus(Long cusId, Long userNum) {
		// 추천조회
		Cus cus = cusRepository.findById(cusId)
				.orElseThrow(()->new RuntimeException("추천결과를 찾을 수 없습니다. cusId"+cusId));
		// 유저번호 매칭확인
		if(!cus.getUserNum().equals(userNum)) {
			throw new RuntimeException("접근권한이 없습니다.");
		}
		// 추천아이템내역 삭제
		cusItRepository.deleteById(cusId);
		// 추천 삭제
		cusRepository.deleteByCusId(cusId);
	}
	
	// [내부] ansJson -> GPT 프롬프트 변환
	private String buildPrompt(String ansJson) {
		try {
			JsonNode root = objectMapper.readTree(ansJson);

			String gender        = getText(root, "gender");
			String ageGroup      = getText(root, "ageGroup");
			String height        = getText(root, "height");
			String weight        = getText(root, "weight");
			String activity      = getText(root, "activityLevel");
			String status        = joinArray(root, "currentStatus");
			String statusEtc     = getText(root, "currentStatusEtc");
			String diseases      = joinArray(root, "diseases");
			String diseasesEtc   = getText(root, "diseasesEtc");
			String meds          = joinArray(root, "medications");
			String medsEtc       = getText(root, "medicationsEtc");
			String curSupp       = joinArray(root, "currentSupplements");
			String curSuppEtc    = getText(root, "currentSupplementsEtc");
			String allergy       = joinArray(root, "allergies");
			String allergyEtc    = getText(root, "allergiesEtc");
			String effects       = joinArray(root, "desiredEffects");
			String effectsEtc    = getText(root, "desiredEffectsEtc");
			String interested    = joinArray(root, "interestedSupplements");
			String interestedEtc = getText(root, "interestedSupplementsEtc");

			String prdContext = buildPrdContext();

			return "당신은 영양제 전문 상담사입니다.\n"
				+ "아래 고객 설문 결과를 분석하여 우리 쇼핑몰의 상품 중 최대 5개를 추천해 주세요.\n\n"
				+ "[고객 설문 정보]\n"
				+ "- 성별: " + gender + "\n"
				+ "- 나이대: " + ageGroup + "\n"
				+ "- 키: " + height + "cm / 몸무게: " + weight + "kg\n"
				+ "- 활동량: " + activity + "\n"
				+ "- 현재 건강 상태: " + status + " " + etcSuffix(statusEtc) + "\n"
				+ "- 지병: " + diseases + " " + etcSuffix(diseasesEtc) + "\n"
				+ "- 복용 중인 약: " + meds + " " + etcSuffix(medsEtc) + "\n"
				+ "- 현재 복용 영양제: " + curSupp + " " + etcSuffix(curSuppEtc) + "\n"
				+ "- 알러지: " + allergy + " " + etcSuffix(allergyEtc) + "\n"
				+ "- 원하는 효과: " + effects + " " + etcSuffix(effectsEtc) + "\n"
				+ "- 관심 영양제 군: " + interested + " " + etcSuffix(interestedEtc) + "\n\n"
				+ "[우리 쇼핑몰 상품 목록]\n"
				+ "아래 상품 중에서만 추천하세요. (prd_id | 상품명 | 브랜드 | 가격)\n"
				+ prdContext + "\n\n"
				+ "[응답 규칙]\n"
				+ "1. 반드시 아래 JSON 형식으로만 응답하세요. 설명 텍스트와 마크다운 코드블럭(```) 없이 순수 JSON만 출력하세요.\n"
				+ "2. products 배열은 추천 우선순위 순으로 최대 5개, 최소 1개.\n"
				+ "3. 알러지·복용 약 상호작용을 반드시 고려하세요.\n"
				+ "4. 이미 복용 중인 영양제와 중복 추천은 피하세요.\n"
				+ "5. 한국어로 작성하세요.\n\n"
				+ "{\n"
				+ "  \"summary\": \"고객 상태 한 줄 요약 (50자 이내)\",\n"
				+ "  \"reason\": \"추천 이유 설명 (200자 이내)\",\n"
				+ "  \"dosage\": \"복용 가이드 (100자 이내)\",\n"
				+ "  \"caution\": \"주의 사항 (100자 이내)\",\n"
				+ "  \"products\": [\n"
				+ "    {\"prd_id\": 숫자, \"sort_num\": 1},\n"
				+ "    {\"prd_id\": 숫자, \"sort_num\": 2}\n"
				+ "  ]\n"
				+ "}";

		} catch (Exception e) {
			throw new RuntimeException("프롬프트 생성 실패: " + e.getMessage(), e);
		}
	}

	// [내부] prd 테이블 -> 판매중(use_yn=Y) 상품만 프롬프트 컨텍스트로 변환
	private String buildPrdContext() {
		try {
			List<Prd> prdList = prdRepository.findByUseYn("Y");
			return prdList.stream()
					.map(prd -> prd.getPrdId() + " | " + prd.getPrdNm() + " | "
							+ (prd.getBrand() != null ? prd.getBrand() : "") + " | "
							+ (prd.getPrice() != null ? prd.getPrice() : 0) + "원")
					.collect(Collectors.joining("\n"));
		} catch (Exception e) {
			log.warn("** [CUS] 상품 목록 로드 실패: {}", e.getMessage());
			return "(상품 목록을 불러올 수 없습니다)";
		}
	}

	// [내부] GPT 응답 JSON 파싱
	private GptResult parseGptResponse(String raw) {
		try {
			String cleaned = raw.trim()
					.replaceAll("(?s)^```json\\s*", "")
					.replaceAll("(?s)```\\s*$", "")
					.trim();

			JsonNode root = objectMapper.readTree(cleaned);

			GptResult result = new GptResult();
			result.summary  = root.path("summary").asText("");
			result.reason   = root.path("reason").asText("");
			result.dosage   = root.path("dosage").asText("");
			result.caution  = root.path("caution").asText("");
			result.products = new ArrayList<>();

			JsonNode products = root.path("products");
			if (products.isArray()) {
				for (JsonNode p : products) {
					GptResult.ProductItem item = new GptResult.ProductItem();
					item.prdId   = p.path("prd_id").asLong();
					item.sortNum = p.path("sort_num").asInt();
					result.products.add(item);
				}
			}
			return result;

		} catch (Exception e) {
			throw new RuntimeException("GPT 응답 파싱 실패: " + e.getMessage() + "\nraw=" + raw, e);
		}
	}

	// [내부] prd + prd_img 조회 후 CusItDTO 빌드
	private CusItDTO fetchPrdDetail(Long cusItId, Long prdId, int sortNum, String surTtile) {

		CusItDTO dto = new CusItDTO();
		dto.setCusItId(cusItId);
		dto.setPrdId(prdId);
		dto.setSortNum(sortNum);
		dto.setSurTitle(surTtile);

		try {
			// prd 기본 정보
			prdRepository.findById(prdId).ifPresent(prd -> {
				dto.setPrdName(prd.getPrdNm());
				dto.setPrdPrice(prd.getPrice());
			});

			// prd_img 에서 썸네일 이미지 조회 (img_type_cd = 'THUMB')
			prdImgRepository.findByPrdIdAndImgTypeCd(prdId, "THUMB")
					.ifPresent(img -> dto.setPrdImg(img.getImgUrl()));

		} catch (Exception e) {
			log.warn("** [CUS] 상품 상세 조회 실패 prdId={}: {}", prdId, e.getMessage());
		}
		return dto;
	}

	// [내부] JSON 배열 -> 쉼표 문자열
	private String joinArray(JsonNode root, String field) {
		JsonNode node = root.path(field);
		if (node.isArray()) {
			List<String> values = new ArrayList<>();
			node.forEach(v -> values.add(v.asText()));
			return String.join(", ", values);
		}
		return node.asText("");
	}

	// [내부] JSON 텍스트 필드
	private String getText(JsonNode root, String field) {
		return root.path(field).asText("");
	}

	// [내부] 기타 입력값이 있으면 "(기타: xxx)" 접미사 추가
	private String etcSuffix(String etc) {
		return (etc != null && !etc.isBlank()) ? "(기타: " + etc + ")" : "";
	}

	// [내부] GPT 파싱 결과 임시 홀더
	private static class GptResult {
		String summary;
		String reason;
		String dosage;
		String caution;
		List<ProductItem> products;

		static class ProductItem {
			long prdId;
			int sortNum;
		}
	}
}