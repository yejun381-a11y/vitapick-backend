package com.vita.vitapickBack.products.rvw;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RvwCanWriteDTO {
	
	// 리뷰 작성 가능 여부
    private boolean canWrite;
    
    // 리뷰 작성이 가능한 주문 상품 ID (canWrite 가 true 인 경우에만 유효)
    private Long ordItId;
}