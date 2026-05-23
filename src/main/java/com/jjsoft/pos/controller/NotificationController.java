package com.jjsoft.pos.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jjsoft.pos.dto.biztalk.BiztalkRequestDto;
import com.jjsoft.pos.dto.biztalk.BiztalkRequestDto.Attach;
import com.jjsoft.pos.dto.biztalk.BiztalkRequestDto.BiztalkButton;
import com.jjsoft.pos.dto.biztalk.BiztalkSendLogDto;
import com.jjsoft.pos.dto.biztalk.TemplateMstDto;
import com.jjsoft.pos.response.ApiResponse;
import com.jjsoft.pos.service.NotificationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 * 알람 전송 컨트롤러 
 */
@RestController
@RequestMapping("/api/admin/noti")
@RequiredArgsConstructor
@Log4j2
public class NotificationController {

	private final NotificationService notificationService;
	
	@Value("${app.base-url}")
	private String baseUrl;
	
	/** 픽업 메시지 전체 전송 */
	@GetMapping("/sendpickupAlimAll/{storeId}/{locationId}")
	public void sendPickupAlimAll(String tmpltCode) {
		
	}
	
	/** 특가별 픽업 메시지 전송 */
	@GetMapping("/sendpickupAlim/{storeId}/{locationId}/{specialId}")
	public void sendPickupAlim(String userId) {
		
	}
	
	/** 테스트 알림톡 배치 전송 */
	@GetMapping("/sendpickupAlimTest")
	public boolean sendPickupAlimTest() {
		log.info("sendPickupAlimTest ###################################### ");
		return notificationService.sendAllAlimtalk();
	}

	/** 테스트 알림톡 단건 전송 */
	@GetMapping("/sendpickupAlimOneTest")
	public boolean sendpickupAlimOneTest() {
		log.info("sendPickupAlimTest ###################################### ");
		List<BiztalkButton> btnList = new ArrayList<>();
        btnList.add(BiztalkButton.builder().name("채널 추가").type("AC").build());
        btnList.add(BiztalkButton.builder()
        		.name      ("예약 상품 보기")
        		.type      ("WL")
        		.url_mobile(baseUrl)
        		.url_pc    (baseUrl)
        		.build());
		BiztalkRequestDto requestDto = 
				BiztalkRequestDto.builder()
					.userId     ("clubbboy@naver.com")
					.recipient  ("010-4246-7729")
					.tmpltCode  ("rsv_notice")
					.attach     (Attach.builder().button(btnList).build())
					.resMethod  ("PUSH")
					.countryCode("82")
					.title      ("윤죠님 예약상품 픽업안내")
					.message    ("[지구특가 예약 상품 픽업 안내]\n\n안녕하세요 윤죠님,\n금일 예약하신 지구특가 상품은 금일 20시까지 방문 수령이 가능합니다.\n해당 시간 내 매장 방문 및 픽업 부탁드립니다.\n\n만일, 마감 시간 전까지 방문이 어려우실 경우, 다른 고객님들께서 구매하실 수 있도록 마이페이지에서 예약을 취소해 주세요.\n\n■ 픽업 방법\n1. 매장 방문(파인애플 상가 A01호)\n2. 전화번호 뒤 4자리로 예약 확인\n3. 예약 수량 확인 후 결제 및 픽업\n\n■ 매장 운영시간\n10시 ~ 20시 (월~토)\n토요일 13~14시 브레이크 타임")
					.build();
		 notificationService.sendAlimtalk(requestDto);
		 return true;
	}
	
	
	/** 템플릿 리스트 조회  */
    @GetMapping("/tmplList")
    public ResponseEntity<ApiResponse<Object>> getTmplList(@ModelAttribute BiztalkRequestDto dto) {
        List<TemplateMstDto> list = notificationService.getTmplList(dto);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(list));
    }
    /** 전송 로그 리스트 조회  */
    @GetMapping("/alimtalkLogList")
    public ResponseEntity<ApiResponse<Object>> getAlimtalkLogList(@ModelAttribute BiztalkRequestDto dto) {
    	List<BiztalkSendLogDto> list = notificationService.getAlimtalkLogList(dto);
    	Map map = new HashMap();
    	map.put("dataList",list);
    	map.put("dataSize",list.size());
    	return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(map));
    }
	
	
}
