package com.vita.vitapickBack.custom.sur;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SurServiceImpl implements SurService{

	private final SurRepository surRepository;
	
	//저장
	@Override
		public SurDTO saveSur(SurDTO dto) {
		
			//타이틀에 값이 들어오면 그 값을 타이틀로 저장하고 아니면 지금 날짜시간을 타이틀로 함
			String title =(dto.getSurTitle() != null && !dto.getSurTitle().isBlank())
					? dto.getSurTitle()
					: LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm"));
			
			Sur sur = Sur.builder()
					.surTitle(title)
					.userNum(dto.getUserNum())
					.ansJson(dto.getAnsJson())
					.build();
			
			Sur saved = surRepository.save(sur);
			
			return SurDTO.builder()
					.surId(saved.getSurId())
					.message("설문이 저장되었습니다.")
					.build();
		}
	
	//내설문리스트조회
	@Override
	public List<SurDTO> getSurList(Long userNum) {
		
		List<Sur> surList = surRepository.findByUserNumOrderBySurIdDesc(userNum);
		
		List<SurDTO> dtoList = new ArrayList<>();
		for(Sur sur : surList) {
			SurDTO dto = SurDTO.builder()
					.surId(sur.getSurId())
					.surTitle(sur.getSurTitle())
					.userNum(sur.getUserNum())
					.build();
			dtoList.add(dto);
		}
		return dtoList;
	}
	
	//설문디테일보기
	@Override
	public SurDTO getSurDetail(Long SurId) {
		
		Sur sur = surRepository.findById(SurId)
				.orElseThrow(()->new RuntimeException("설문을 찾을 수 없습니다."));
		
		return SurDTO.builder()
				.surId(sur.getSurId())
				.surTitle(sur.getSurTitle())
				.userNum(sur.getUserNum())
				.ansJson(sur.getAnsJson())
				.build();
	}
}
