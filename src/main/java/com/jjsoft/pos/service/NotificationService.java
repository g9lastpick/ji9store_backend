package com.jjsoft.pos.service;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.jjsoft.pos.dto.biztalk.BiztalkRequestDto;
import com.jjsoft.pos.dto.biztalk.BiztalkRequestDto.Attach;
import com.jjsoft.pos.dto.biztalk.BiztalkRequestDto.BiztalkButton;
import com.jjsoft.pos.dto.biztalk.BiztalkResponseDto;
import com.jjsoft.pos.dto.biztalk.BiztalkResponseDto.UserResponse;
import com.jjsoft.pos.dto.biztalk.BiztalkSendLogDto;
import com.jjsoft.pos.dto.biztalk.TemplateMstDto;
import com.jjsoft.pos.entity.BiztalkSendLogEntity;
import com.jjsoft.pos.mapper.NotificationMapper;
import com.jjsoft.pos.repository.BiztalkSendLogRepository;
import com.nimbusds.jose.shaded.gson.Gson;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
@Slf4j
public class NotificationService {

	@Qualifier("biztalkWebClient")
	private final WebClient webClient;
    private final BiztalkSendLogRepository logRepository;
    private final NotificationMapper notificationMapper;
    
    
    private String bsid      = "g9store";
    private String passwd    = "38a9b73809b07b8d24e4533bf497eab161c9c9e0";//api token 용 PWD
    private String senderKey = "3e554f9c34146e714b89ee5e56ce0b9dd79712b4"; // g9store 식별자
    
    //유효시간 하루 
    //하루 한번 요청해서 놔야함.
    private String adminToken = "";
    
    /** 비즈톡 admin token 요청 
	 * default 24시간이라 하루 한번 요청해서 db에 담아둬야함. 
	 * 아니면 호출 할때마다 요청할까?? 
	 * 토큰 만료라고 나올때마다 요청할까??
	 * */
	private String getTokenFromAdminBizTalk() {
		
		Map<String, String> req = Map.of( "bsid", bsid, "passwd", passwd );
	        BiztalkResponseDto res = 
	        		webClient.post()
							.uri("/v2/auth/getToken")
							.bodyValue(req)
							.retrieve()
							.bodyToMono(BiztalkResponseDto.class)
							.block();

	        if (res != null && "1000".equals(res.getResponseCode())) {
	            log.info("✅ Biztalk token 발급 완료: {}", res.getToken());
	            return res.getToken();
	        }
	        throw new RuntimeException("비즈톡 토큰 발급 실패");
	}
	
	/** biztalk api 호출 */
	public BiztalkResponseDto callBiztalk(BiztalkRequestDto requestDto , String uri , String methodType , String curAdminToken) {
    	BiztalkResponseDto response = null;
    	
    	
    	if(curAdminToken != null) {
    		adminToken = curAdminToken;
    	}else if(adminToken.isEmpty()) {//admin token  없는경우 토큰 생성
    		adminToken = getTokenFromAdminBizTalk();
    	}
    	
    	// 비즈톡 API 호출
    	if(methodType.equals("POST")) {
    		response = webClient.post()
    				.uri(uri)//"/v2/kko/sendAlimTalk"
    				.header("bt-token", adminToken)
                    .bodyValue(requestDto)
                    .retrieve()
                    .bodyToMono(BiztalkResponseDto.class)
                    .block();
    		
    		//토큰 만료의 경우 토큰 다시 요청후 다시 호출
    		if(response != null && response.getResponseCode().equals("B199") && adminToken == null) {
    			String getAdminToken = getTokenFromAdminBizTalk();
    			return callBiztalk(requestDto , uri , methodType , getAdminToken);
    		}
    	}else {
    		response = webClient.get()
                    .uri(uri)
                    .header("bt-token", adminToken)
                    .retrieve()
                    .bodyToMono(BiztalkResponseDto.class)
                    .block();
    		
    		//토큰 만료의 경우 토큰 다시 요청후 다시 호출\
    		//B199 : 인증 실패(Token 정보 인증 실패)
    		if(response != null && response.getResponseCode().equals("B199") && adminToken == null) {
    			String getAdminToken = getTokenFromAdminBizTalk();
    			return callBiztalk(requestDto , uri , methodType , getAdminToken);
    		}
    	}
    	
    	log.info("callBiztalk uri = {} response === {}" , uri , response);
    	return response;
    }
    
    
    /**
     * 비즈톡 알림톡 문자 전송  메서드
     */
    @Transactional
    public void sendAlimtalk(BiztalkRequestDto requestDto) {
    	
    	if(adminToken.isEmpty()) {
    		adminToken = getTokenFromAdminBizTalk();
    	}
    	String uuid = UUID.randomUUID().toString();
    	requestDto.setMsgIdx(uuid);
    	requestDto.setSenderKey(senderKey);
    	if(requestDto.getTmpltCode() == null || requestDto.getTmpltCode().isEmpty())
    		requestDto.setTmpltCode("rsv_notice");
    	
        // 요청 JSON 문자열화
        String requestJson = new Gson().toJson(requestDto);

        // DB 로그 저장 (READY 상태)
        BiztalkSendLogEntity logEntity = BiztalkSendLogEntity.builder()
        		.msgIdx        (uuid)
                .userId        (requestDto.getUserId())
                .phoneNo       (normalizeKoreanPhone(requestDto.getRecipient()))
                .tmplCode      (requestDto.getTmpltCode())
                .messageContent(requestDto.getMessage())
                .requestJson   (requestJson)
                .sendStatus    ("SEND")
                .createUser    ("SYSTEM")
                .build();
        logRepository.save(logEntity);

        try {
            // 비즈톡 API 호출
        	BiztalkResponseDto response = callBiztalk(requestDto , "/v2/kko/sendAlimTalk" , "POST" , null); 
        	log.info("sendAlimtalk response === {}" , response);
        	
            // 응답 성공 저장
            logEntity.setResponseJson(response.toString());
            logEntity.setSendStatus("SUCCESS");

        } catch (Exception e) {
            // 실패 시 로그 상태 변경
            logEntity.setSendStatus("FAIL");
            logEntity.setResponseJson(e.getMessage());
            log.error("❌ 비즈톡 알림톡 전송 실패: {}", e.getMessage());
        }

        logRepository.save(logEntity);
    }
    
    
    /** biztalk 메시지 전송결과 확인  5분에 한번 돌면서 메시지 완료 처리 해줌 
     * 진짜 좆같이 되어있네 api
     * 
     * 주기적으로 한번씩 돌면서 완료 처리 해줘야하나.... 아 짱나네 진짜 api
     * 5분에 한번 
     * 나중에 배치쪽으로 옮기자
     * */
    @Scheduled(cron = "0 */5 * * * *") // 5분마다
    @Transactional
    public void getResultPoll() {
    	
    	try {
    		
    		//요청 메시지 확인
    		BiztalkResponseDto response = callBiztalk(null , "v2/kko/getResultPoll" , "GET" , null); 
    		//완료처리
    		log.info("getResultPoll ~~~~~~~~~~~~~~~~~ 메시지 결과 확인 response = {}" , response);
    		if(response == null ) {
    			log.error("getResultPoll response null error " );
    			return ;
    		}
    		if(!"1000".equals(response.getResponseCode())) {
    			log.error("getResultPoll RESPONSE CODE ERROR response = {}" , response.toString());
    			return ;
    		}
    					
    		List<UserResponse> list = response.getResponse();//응답 배열임
    		if(list == null ) {
    			log.error("getResultPoll response.getResponse() null ERROR response.getResponse() = {}" , response.getResponse());
    			return;
    		}
    		
    		for(UserResponse item : list) {
    			//우리 쪽에서 관리하는 메시지 고유값
    			try {
    				BiztalkSendLogEntity entity = logRepository.findByMsgIdx(item.getMsgIdx()).orElse(null);
        			
    				if(entity == null) {
    					log.error("getResultPoll item.getMsgIdx() error  item.getMsgIdx() = {}" ,item.getMsgIdx());
    					continue;
    				}
    				
        			entity.setBiztalkId(item.getUid());//biz talk에서 반든 메시지 고유값
        			entity.setResCode(item.getResultCode());
        			if(item.getResultCode().equals("1000")) {
        				entity.setSendMsgStatus("SUCCESS");
        			}else {
        				entity.setSendMsgStatus("FAIL");
        			}
        	        entity.setSendMsgDate(LocalDateTime.now());
        	        entity.setUpdateDate(LocalDateTime.now());
        	        entity.setUpdateUser("AUTO SYSTEM");
				} catch (Exception e) {
					// TODO: handle exception
					log.error("getResultPoll save error MSG_IDX = {} ",item.getMsgIdx());
				}
    		}
    		//모두 완료 처리하고 api에 완료 처리함.
    		ackResultPoll(response.getPk());
		} catch (Exception e) {
			// TODO: handle exception
			log.error("getResultPoll ERROR = {} " , e.getMessage());
		}
    	
    	
    }
    
    /** biztalk 메시지 전송결과 완료처리 */
    public void ackResultPoll(String pk) {
    	try {
    		//메시지 완료처리 
//        	BiztalkResponseDto response = 
			callBiztalk( BiztalkRequestDto.builder().pk(pk).build() 
					   , "v2/kko/ackResultPoll" 
					   , "POST" 
					   , null); 
        	
//        	log.info("ackResultPoll response.getResponseCode() === {}",response.getResponseCode());
		} catch (Exception e) {
			// TODO: handle exception
		}
    }
    
    
    //전화번호 변환
    public static String normalizeKoreanPhone(String phone) {
        if (phone == null) return null;
        // 공백, 하이픈 제거 후 국가번호 제거
        phone = phone.replaceAll("[^0-9]", "");   // 숫자만 남김
        if (phone.startsWith("82")) {
            phone = "0" + phone.substring(2);
        }
        // 형식 다시 하이픈 넣기 (010-xxxx-xxxx 기준)
        if (phone.length() == 11) {
            return phone.replaceFirst("(\\d{3})(\\d{4})(\\d{4})", "$1-$2-$3");
        }
        return phone;
    }
    
    
    /** 픽업 배치 알림톡 전송 매서드
     *   모두에게 보냄.
     *   배치에서 호출함.
     * */
    public boolean sendAllAlimtalk() {
    	return sendAllAlimtalk(null);
    }
    
    /** 픽업 배치 알림톡 전송 매서드
     *   모두에게 보냄.
     * */
    @Transactional
    public boolean sendAllAlimtalk(List<String> users) {
    	
    	try {
    		String reqApiUri = "/v2/kko/sendAlimTalkBatch";
    		String tmplCode  = "reserved_notice";//reserved_notice 이게 최종버전임 아직 승인전
    		
            List<Map<String, Object>> targetList = notificationMapper.findTodayPickupReservationUsers();
            Map<String, Object>          tmplMap = notificationMapper.getBiztalkTmpl(tmplCode);
            
            
            log.info("🚚 오늘 픽업 알림 대상자 수: {}", targetList.size());

            String tmplName    = tmplMap.get("TMPL_NAME")    != null ? tmplMap.get("TMPL_NAME")    .toString() : "";
            String tmplTitle   = tmplMap.get("TMPL_TITLE")   != null ? tmplMap.get("TMPL_TITLE")   .toString() : "";
            String tmplContent = tmplMap.get("TMPL_CONTENT") != null ? tmplMap.get("TMPL_CONTENT") .toString() : "";
            String webBtn      = tmplMap.get("WEB_BTN")      != null ? tmplMap.get("WEB_BTN")      .toString() : "";
            String resMethod   = tmplMap.get("SNS_TYPE")     != null ? tmplMap.get("SNS_TYPE")     .toString() : "PUSH";
            
            
            if("".equals(tmplName)) {
            	log.error("############################# 템플릿 정보 없음 ##########################");
            	return false;
            }
            
            /** <<< biztalk 배치 정책 >>> 
             *  배치는 최대 500개까지 한번에 보낼수 있음
             *  500개 넘어가면 다시 셋팅해서 보내야함
             * 
             * */
            List<BiztalkRequestDto> sendList = new ArrayList<>();
            for (Map<String, Object> user : targetList) 
            {
                try {
                    String userId   = (String) user.get("USER_ID");
                    String phone    = String.valueOf(user.get("PHONE"));//전번
                    String name     = (String) user.get("NAME");//이름 
                    String totalCnt = String.valueOf(user.get("TOT_CNT"));//전채 예약 수량
                    
                    if(phone == null || phone.isEmpty()) continue;
                    
                    //본문 내용
                    String msg = String.format(tmplContent)
                    				   .replace("#{name}"    , name)
                    				   .replace("#{totalQty}", totalCnt)
                    				   .replace("\\n", "\n")
                    				   ;
                    String title = tmplTitle.replace("#{name}", name);//강조표기타이틀
                    String uuid = UUID.randomUUID().toString();
                    
                    /* webBtn
                     * 샘플 : AC@채널추가@@WL@예약상품보기@https://g9system.com/mobile/orderList@@
                     * @@ 이거로 자르고 @ 한번더 잘라서 써야람.
                     * 버튼명 , 버튼타입  , url 
                     * 버튼타입이 WL이면 웹링크 / AC면 채널 추가 : 이건 링크 없음
                     * */ 
                    List<BiztalkButton> btnList = new ArrayList<>();
                    if(webBtn != null && !webBtn.isEmpty()) {
                    	List<String> webBtnList = Arrays.asList(webBtn.split("@@"));
                    	for(String item :webBtnList) {
                    		List<String> itemList = Arrays.asList(item.split("@"));
                    		BiztalkButton bizBtn = new BiztalkButton(); 
                    		
                    		if(itemList.size() == 2) {
                    			bizBtn.setName      (itemList.get(0));
                    			bizBtn.setType      (itemList.get(1));
                    			btnList.add(bizBtn);
                    		}
                    		if(itemList.size() == 3) {
                    			bizBtn.setName      (itemList.get(0));
                    			bizBtn.setType      (itemList.get(1));
                    			bizBtn.setUrl_mobile(itemList.get(2));
                    			bizBtn.setUrl_pc    (itemList.get(2));
                    			btnList.add(bizBtn);
                    		}
                    	}
                    }
                    
                    BiztalkRequestDto dto = BiztalkRequestDto.builder()
                    		.msgIdx     (uuid)
                    		.countryCode("82")
                    		.resMethod  (resMethod)
                    		.tmpltCode  (tmplCode)
                    		.recipient  (normalizeKoreanPhone(phone))      
                    		.title      (title)
                    		.message    (msg)
                    		.senderKey  (senderKey)
                    		.build();
                    if(btnList.size() > 0) {
                    	dto.setAttach(Attach.builder().button(btnList).build());
                    }

                    sendList.add(dto);
                    //알림톡 전송 전 저장 
                    BiztalkSendLogEntity logEntity = BiztalkSendLogEntity.builder()
                    		.storeId       (1L)
                    		.locationId    (1L)
                    		.msgIdx        (uuid)
                            .userId        (userId)
                            .phoneNo       (normalizeKoreanPhone(dto.getRecipient()))
                            .tmplCode      (tmplCode)
                            .messageContent(msg)
                            .requestJson   (dto.getAttach().toString())
                            .reqApiUri     (reqApiUri)
                            .sendStatus    ("SEND")
                            .createUser    ("SYSTEM")
                            .build();
                    logRepository.save(logEntity);
                    
                    if(sendList.size() == 500) 
                    {//500개씩 전송
                    	HashMap<String , Object> sendMap = new HashMap<>();
                    	sendMap.put("msgList", sendList);
                    	Object response = webClient.post()
                				.uri      (reqApiUri)
                				.header   ("bt-token", getTokenFromAdminBizTalk())
                                .bodyValue(sendMap)
                                .retrieve()
                                .bodyToMono(Object.class)
                                .block();
                    	sendList = new ArrayList<>();
                    	log.info(" batch sendList.size() == 500 전송 성공 response = {}" , response.toString());
                    }

                } catch (Exception e) {
                    log.error("❌ 픽업 알림톡 발송 실패: {}", e.getMessage());
                }
            }
            
            if(sendList.size() > 0) {
            	HashMap<String , Object> sendMap = new HashMap<>();
            	sendMap.put("msgList", sendList);
            	
            	/**  BODY 로그 확인용 START */
//            	ObjectMapper mapper = new ObjectMapper();
//            	mapper.setPropertyNamingStrategy(PropertyNamingStrategies.LOWER_CAMEL_CASE);
//            	mapper.enable(SerializationFeature.INDENT_OUTPUT); // 보기 좋게 포매팅
//            	// JSON 직렬화
//            	String json = mapper.writeValueAsString(sendMap);
            	// 로그로 확인
//            	log.info("📦 [BATCH BIZTALK REQUEST BODY]\n{}", json);
            	/**  BODY 로그 확인용 END */
            	
            	Object response = webClient.post()
        				.uri      (reqApiUri)
        				.header   ("bt-token", getTokenFromAdminBizTalk())
                        .bodyValue(sendMap)
                        .retrieve()
                        .bodyToMono(Object.class)
                        .block();
            	sendList = new ArrayList<>();
            	
            	log.info(" batch 전송 성공 response = {}" , response.toString());
            }
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return false;
		}
    	
    	
//    	try {
//			Thread.sleep(1000L);
//			getResultPoll();
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
        
        return true;
    }

    
	

    /**
     * 템플릿 리스트 조회
     */
    public List<TemplateMstDto> getTmplList(BiztalkRequestDto dto) {
        return notificationMapper.selectTemplateList(dto);
    }
	
    /**
     * 알림톡 전송 로그 리스트 조회
     * 조건: 템플릿 코드, 전송일자, 유저명, 유저아이디
     */
    public List<BiztalkSendLogDto> getAlimtalkLogList(BiztalkRequestDto dto) {
        return notificationMapper.selectAlimtalkLogList(dto);
    }
	
	
}
