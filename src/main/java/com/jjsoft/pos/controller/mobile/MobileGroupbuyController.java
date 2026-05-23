package com.jjsoft.pos.controller.mobile;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 * 모바일 공동구매 페이지
 */
@RestController
@RequestMapping("/api/mobile/groupbuy")
@RequiredArgsConstructor
@Log4j2
public class MobileGroupbuyController {

	/**  공통 필수사항 : store , location 필수 */
	
	/** 공동구매 예약  */
	
	/** 공동구매 예약 취소 */
	
	/** 공동구매 예약 완료처리 : pos완 연동하여 결제 호출 - my page에서 pos 결제창 호출
	 * ******* 중요 !! : 결제창 호출시 pos에서 수량 및 금액 조정 불가하게 셋팅해야함 */
	
	
}
