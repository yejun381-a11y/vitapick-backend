package com.vita.vitapickBack.products.prd;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import com.vita.vitapickBack.products.prd_img.PrdImg;
import com.vita.vitapickBack.products.prd_img.PrdImgRepository;
import com.vita.vitapickBack.order.OrdItRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PrdServiceImpl implements PrdService {

    // 상품 DB 접근하는 애
    private final PrdRepository prdRepository;

    // 이미지 DB 접근하는 애
    private final PrdImgRepository prdImgRepository;
    
    // 주문상품 DB 접근하는 애 (상품별 주문 조회할 때 필요)
    private final OrdItRepository ordItRepository;
    
    @Override
    public List<PrdDTO> getAllProducts() {

        List<Prd> prdList = prdRepository.findAll(); // DB에서 전체 상품 조회
        List<PrdDTO> result = new ArrayList<>(); // 최종 결과 담을 리스트

        for (Prd prd : prdList) { // 각 상품마다 썸네일 이미지 찾아서 DTO에 담기

            String thumbUrl = null; // 썸네일 이미지 URL 담을 변수
            List<PrdImg> imgList = prdImgRepository.findByPrdId(prd.getPrdId()); // 해당 상품의 이미지 목록 조회

            for (PrdImg img : imgList) { // 썸네일 이미지 찾기
                if ("THUMB".equals(img.getImgTypeCd())) { // 이미지 타입이 "THUMB"인 경우
                    thumbUrl = img.getImgUrl(); // 썸네일 이미지 URL 저장
                    break;
                }
            }
            // Builder 패턴으로 DTO 생성
            PrdDTO dto = PrdDTO.builder()
                    .prdId(prd.getPrdId())
                    .prdNm(prd.getPrdNm())
                    .price(prd.getPrice())
                    .brand(prd.getBrand())
                    .descTxt(prd.getDescTxt())
                    .ingr(prd.getIngr())
                    .thumbImgUrl(thumbUrl)
                    .build();

            result.add(dto); // 최종 결과 리스트에 DTO 추가
        }

        return result;
    }

    // 카테고리별 상품 목록 + 썸네일 이미지 반환
    @Override
    public List<PrdDTO> getPrdByCategory(int catCd) {
        // DB에서 catCd로 상품 조회
        List<Prd> prdList = prdRepository.findByCatCd(catCd);
        List<PrdDTO> result = new ArrayList<>();

        for (Prd prd : prdList) {
            String thumbUrl = null;
            List<PrdImg> imgList = prdImgRepository.findByPrdId(prd.getPrdId());
            
            // 썸네일 이미지 찾기
            for (PrdImg img : imgList) {
                if ("THUMB".equals(img.getImgTypeCd())) {
                    thumbUrl = img.getImgUrl();
                    break;
                }
            }
            PrdDTO dto = PrdDTO.builder()
                    .prdId(prd.getPrdId())
                    .prdNm(prd.getPrdNm())
                    .price(prd.getPrice())
                    .brand(prd.getBrand())
                    .descTxt(prd.getDescTxt())
                    .ingr(prd.getIngr())
                    .thumbImgUrl(thumbUrl)
                    .build();
            result.add(dto);
        }
        return result;
    }

    // 상품 상세 조회
    @Override
    public PrdDTO getPrdDetail(Long prdId) {
        Optional<Prd> result = prdRepository.findById(prdId);
        if (!result.isPresent()) {
            throw new RuntimeException("상품을 찾을 수 없습니다.");
        }
        Prd prd = result.get();

        // 이미지 목록 가져오기
        List<PrdImg> imgList = prdImgRepository.findByPrdId(prdId);

        // 썸네일이랑 상세 이미지 둘 다 찾기
        String thumbUrl = null;
        String detailUrl = null;
        for (PrdImg img : imgList) {
            // 썸네일 이미지
            if ("THUMB".equals(img.getImgTypeCd())) {
                thumbUrl = img.getImgUrl();
            }
            // 상세 이미지
            if ("DETAIL".equals(img.getImgTypeCd())) {
                detailUrl = img.getImgUrl();
            }
        }

        // 찾은 정보 담아서 반환
        return PrdDTO.builder()
                .prdId(prd.getPrdId())
                .prdNm(prd.getPrdNm())
                .price(prd.getPrice())
                .brand(prd.getBrand())
                .descTxt(prd.getDescTxt())
                .ingr(prd.getIngr())
                .thumbImgUrl(thumbUrl)
                .detailImgUrl(detailUrl)
                .build();
    }
    
    // 상품 검색
    @Override
    	public List<PrdDTO> searchPrd(String keyword) {
        // 검색어가 포함된 상품 목록 가져오기
    	List<Prd> prdList = prdRepository.searchByKeyword(keyword);
    	
        List<PrdDTO> result = new ArrayList<>(); 
        // 각 상품마다 썸네일 이미지 찾아서 DTO에 담기
        for (Prd prd : prdList) {
            String thumbUrl = null;
            List<PrdImg> imgList = prdImgRepository.findByPrdId(prd.getPrdId());
            // 썸네일 이미지 찾기
            for (PrdImg img : imgList) {
                if ("THUMB".equals(img.getImgTypeCd())) {
                    thumbUrl = img.getImgUrl();
                    break; 
                }
            }
            
            // Builder 패턴으로 DTO 생성
            PrdDTO dto = PrdDTO.builder()
                    .prdId(prd.getPrdId())
                    .prdNm(prd.getPrdNm())
                    .price(prd.getPrice())
                    .brand(prd.getBrand())
                    .descTxt(prd.getDescTxt())
                    .ingr(prd.getIngr())
                    .thumbImgUrl(thumbUrl)
                    .build();
            result.add(dto);
        }
        return result;
    }
    
    @Override
    public List<PrdDTO> getNewProducts() {
    	
        List<PrdDTO> result = new ArrayList<>(); // 최종 결과 담을 리스트
        List<Long> catList = List.of(1L, 2L, 3L, 4L, 5L); // 카테고리 코드 리스트 (예시로 1~5번 카테고리)
        
        for (Long catCd : catList) { // 각 카테고리별로 최신 상품 2개씩 조회
            List<Prd> prdList = prdRepository.findTop2ByCatCdOrderByPrdIdDesc(catCd); // 최신 2개 상품 조회
           
            for (Prd prd : prdList) { // 각 상품마다 썸네일 이미지 찾아서 DTO에 담기

                String thumbUrl = null; // 썸네일 이미지 URL 담을 변수

                List<PrdImg> imgList = prdImgRepository.findByPrdId(prd.getPrdId()); // 해당 상품의 이미지 목록 조회

                for (PrdImg img : imgList) { // 썸네일 이미지 찾기
                    if ("THUMB".equals(img.getImgTypeCd())) { // 이미지 타입이 "THUMB"인 경우
                        thumbUrl = img.getImgUrl(); // 썸네일 이미지 URL 저장
                        break; // 썸네일 이미지는 하나만 필요하므로 찾으면 반복 종료
                    }
                }

                PrdDTO dto = PrdDTO.builder()
                        .prdId(prd.getPrdId())
                        .prdNm(prd.getPrdNm())
                        .brand(prd.getBrand())
                        .price(prd.getPrice())
                        .thumbImgUrl(thumbUrl)
                        .build();

                result.add(dto); // 최종 결과 리스트에 DTO 추가
            }
        }

        return result;
    }
    
    @Override
    public List<PrdDTO> getBestProducts() {

        List<PrdDTO> result = new ArrayList<>();

        // 많이 팔린 상품 ID 10개 조회
        List<Long> prdIdList = ordItRepository.findBestProductIdsTop10();

        for (Long prdId : prdIdList) { // 각 상품 ID로 상품 정보 조회

            Optional<Prd> prdResult = prdRepository.findById(prdId); //	상품 ID로 상품 정보 조회

            if (!prdResult.isPresent()) { // 상품 정보가 없는 경우는 넘어가기
                continue;
            }

            Prd prd = prdResult.get(); // 상품 정보 가져오기

            String thumbUrl = null; // 썸네일 이미지 URL 담을 변수

            List<PrdImg> imgList = prdImgRepository.findByPrdId(prd.getPrdId()); // 해당 상품의 이미지 목록 조회

            for (PrdImg img : imgList) { // 썸네일 이미지 찾기
                if ("THUMB".equals(img.getImgTypeCd())) { // 이미지 타입이 "THUMB"인 경우
                    thumbUrl = img.getImgUrl(); // 썸네일 이미지 URL 저장
                    break; // 썸네일 이미지는 하나만 필요하므로 찾으면 반복 종료
                }
            }
            // Builder 패턴으로 DTO 생성
            PrdDTO dto = PrdDTO.builder()
                    .prdId(prd.getPrdId())
                    .prdNm(prd.getPrdNm())
                    .brand(prd.getBrand())
                    .price(prd.getPrice())
                    .thumbImgUrl(thumbUrl)
                    .build();

            result.add(dto);
        }

        return result;
    }
}