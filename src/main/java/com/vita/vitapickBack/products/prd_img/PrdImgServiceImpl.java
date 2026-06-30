package com.vita.vitapickBack.products.prd_img;

import java.util.List;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PrdImgServiceImpl implements PrdImgService {

    // DB에 접근하는 애
    private final PrdImgRepository prdImgRepository;

    // 상품번호로 이미지 목록 가져오기
    @Override
    public List<PrdImg> findByPrdId(Long prdId) {
        return prdImgRepository.findByPrdId(prdId);
    }
}