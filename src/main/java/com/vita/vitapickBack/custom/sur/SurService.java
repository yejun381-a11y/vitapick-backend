package com.vita.vitapickBack.custom.sur;

import java.util.List;

public interface SurService {

	SurDTO saveSur(SurDTO dto); //저장
	List<SurDTO> getSurList(Long userNum); //자기설문리스트조회
	SurDTO getSurDetail(Long SurId); //설문상세보기
}
