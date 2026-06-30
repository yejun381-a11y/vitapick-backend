package com.vita.vitapickBack.order;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vita.vitapickBack.cart.Cart;
import com.vita.vitapickBack.cart.CartRepository;
import com.vita.vitapickBack.products.prd.Prd;
import com.vita.vitapickBack.products.prd.PrdRepository;
import com.vita.vitapickBack.products.prd_img.PrdImg;
import com.vita.vitapickBack.products.prd_img.PrdImgRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrdServiceImpl implements OrdService {

	private final OrdRepository ordRepository;
	private final OrdItRepository ordItRepository;
	private final PayRepository payRepository;
	private final CartRepository cartRepository;
	private final PrdRepository prdRepository;
	private final PrdImgRepository prdImgRepository;

	// 회원 주문 목록 조회
	@Override
	public List<Ord> findByUserNumOrderByOrdIdDesc(Long userNum) {
		return ordRepository.findByUserNumOrderByOrdIdDesc(userNum);
	}

	// 주문번호 조회
	@Override
	public Ord findByOrdNo(String ordNo) {
		return ordRepository.findByOrdNo(ordNo);
	}

	// 주문상품 목록 조회
	@Override
	public List<OrdItDTO> findOrdItByOrdId(Long ordId) {

		List<OrdIt> ordItList = ordItRepository.findByOrdId(ordId);

		return ordItList.stream().map(ordIt -> {

			String thumbImgUrl = prdImgRepository.findByPrdIdAndImgTypeCd(ordIt.getPrdId(), "THUMB")
					.map(PrdImg::getImgUrl).orElse(null);

			return OrdItDTO.builder().ordItId(ordIt.getOrdItId()).ordId(ordIt.getOrdId()).prdId(ordIt.getPrdId())
					.cusId(ordIt.getCusId()).prdNm(ordIt.getPrdNm()).itQty(ordIt.getItQty()).price(ordIt.getPrice())
					.itAmt(ordIt.getItAmt()).thumbImgUrl(thumbImgUrl).build();
		}).toList();
	}

	// 상품별 주문 조회
	@Override
	public List<OrdIt> findByPrdId(Long prdId) {
		return ordItRepository.findByPrdId(prdId);
	}

	// 주문번호로 결제 조회
	@Override
	public Pay findPayByOrdId(Long ordId) {
		return payRepository.findByOrdId(ordId);
	}

	// 결제번호 조회
	@Override
	public Pay findByPayNo(String payNo) {
		return payRepository.findByPayNo(payNo);
	}

	// 주문 생성 + 결제
	@Override
	@Transactional
	public Ord createOrder(Long userNum, OrdDTO orddto) {

		/* 주문 데이터 검증 */
		if (orddto.getAddrId() == null) {
			throw new RuntimeException("배송지를 선택해주세요.");
		}

		if (orddto.getPayDto() == null || orddto.getPayDto().getPayMthdCd() == null) {
			throw new RuntimeException("결제수단을 선택해주세요.");
		}

		/* 주문번호 / 결제번호 생성 */
		String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

		String ordNo = "ORD" + now;
		String payNo = "PAY" + now;

		/* 주문 정보 저장 */
		Ord ord = Ord.builder().userNum(userNum).ordNo(ordNo).addrId(orddto.getAddrId()).totalAmt(orddto.getTotalAmt())
				.reqMsg(orddto.getReqMsg()).ordStCd("PAID").build();

		Ord savedOrd = ordRepository.save(ord);

		/* 바로구매 주문 */
		if (orddto.getPrdList() != null && !orddto.getPrdList().isEmpty()) {

			for (OrdItDTO prdDto : orddto.getPrdList()) {

				Integer itQty = prdDto.getItQty();
				Integer price = prdDto.getPrice();
				Integer itAmt = price * itQty;

				/* 주문상품 저장 */
				OrdIt ordIt = OrdIt.builder().ordId(savedOrd.getOrdId()).prdId(prdDto.getPrdId())
						.cusId(prdDto.getCusId()).prdNm(prdDto.getPrdNm()).itQty(itQty).price(price).itAmt(itAmt)
						.build();

				ordItRepository.save(ordIt);
			}

		} else {

			/* 장바구니 선택상품 조회 */
			List<Cart> cartList = cartRepository.findByUserNumAndSelectedYn(userNum, 'Y');

			if (cartList == null || cartList.isEmpty()) {
				throw new RuntimeException("선택된 장바구니 상품이 없습니다.");
			}

			for (Cart cart : cartList) {

				/* 상품 정보 조회 */
				Prd prd = prdRepository.findById(cart.getPrdId())
						.orElseThrow(() -> new RuntimeException("상품 정보를 찾을 수 없습니다."));

				Integer itQty = cart.getItQty();
				Integer price = prd.getPrice();
				Integer itAmt = price * itQty;

				/* 주문상품 저장 */
				OrdIt ordIt = OrdIt.builder().ordId(savedOrd.getOrdId()).prdId(cart.getPrdId()).cusId(cart.getCusId())
						.prdNm(prd.getPrdNm()).itQty(itQty).price(price).itAmt(itAmt).build();

				ordItRepository.save(ordIt);
			}

			/* 주문 완료 상품 장바구니 삭제 */
			cartRepository.deleteByUserNumAndSelectedYn(userNum, 'Y');
		}

		/* 결제 정보 저장 */
		Pay pay = Pay.builder().ordId(savedOrd.getOrdId()).payNo(payNo).payMthdCd(orddto.getPayDto().getPayMthdCd())
				.payAmt(savedOrd.getTotalAmt()).payStCd("PAID").build();

		payRepository.save(pay);

		/* 주문 완료 */
		return savedOrd;
	}

	// 상품별 판매량 TOP5 조회
	@Override
	public List<Object[]> findTopProducts() {
		return ordItRepository.findTopProducts();
	}
}