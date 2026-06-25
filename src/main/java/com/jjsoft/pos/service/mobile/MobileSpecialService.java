package com.jjsoft.pos.service.mobile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.jjsoft.pos.dto.mobile.ReservationApplyRequestDto;
import com.jjsoft.pos.dto.product.ProductMstDto;
import com.jjsoft.pos.dto.special.SpecialDtlDto;
import com.jjsoft.pos.dto.special.SpecialMstDto;
import com.jjsoft.pos.dto.special.condition.SpecialSearchCondition;
import com.jjsoft.pos.entity.AuditLogEntity;
import com.jjsoft.pos.entity.SpecialDtlEntity;
import com.jjsoft.pos.entity.SpecialMstEntity;
import com.jjsoft.pos.entity.SpecialRsvDtlEntity;
import com.jjsoft.pos.entity.SpecialRsvMstEntity;
import com.jjsoft.pos.entity.UserMstEntity;
import com.jjsoft.pos.enums.ReservationStatus;
import com.jjsoft.pos.enums.ResponseCode;
import com.jjsoft.pos.exception.GlobalException;
import com.jjsoft.pos.keycloak.KakaoUserInfoDto;
import com.jjsoft.pos.keycloak.KeycloakService;
import com.jjsoft.pos.mapper.MobileSpecialMapper;
import com.jjsoft.pos.repository.AuditLogRepository;
import com.jjsoft.pos.repository.SpecialDtlRepository;
import com.jjsoft.pos.repository.SpecialMstRepository;
import com.jjsoft.pos.repository.SpecialRsvDtlRepository;
import com.jjsoft.pos.repository.SpecialRsvMstRepository;
import com.jjsoft.pos.repository.UserMstRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MobileSpecialService {
	private final MobileSpecialMapper mobileSpecialMapper;
	
	private final SpecialRsvMstRepository specialRsvMstRepository;
	private final SpecialRsvDtlRepository specialRsvDtlRepository;
	private final SpecialDtlRepository specialDtlRepository;
	private final SpecialMstRepository specialMstRepository;
	private final UserMstRepository userMstRepository;
	private final KeycloakService keycloakService;
	
	private final AuditLogRepository auditLogRepository;
	
	
	/** 현재 로그인 사용자의 가입 점포 ID. 신규(미생성)/미지정이면 null.
	 *  모바일 멀티점포 진입 가드(/store/:storeId)에서 본인 가입 점포로 강제할 때 사용. */
	public Long getSignupStoreId() {
		Map userMap = getUserInfo();
		String userId = userMap.get("userId") != null ? userMap.get("userId").toString() : "";
		return userMstRepository.getUserByUserId(userId)
				.map(UserMstEntity::getSignupStoreId)
				.orElse(null);
	}

	//최초 로그인인지 확인.
	public boolean isFirstJoin() {
		Map userMap = getUserInfo();
		String userId = userMap.get("userId") != null ? userMap.get("userId").toString(): "";//토큰정보 userID
		UserMstEntity userEntity = userMstRepository.getUserByUserId(userId).orElse(null);
		if(userEntity == null)  	
		{//신규 회원 등록      
			return true;
		}
		return false;
	}
	
	public List<SpecialMstDto> specialList(@ModelAttribute SpecialSearchCondition condition){
//		log.error("Mobile specialList start ################################################################ info");
//		System.out.println("Mobile specialList start ################################################################ " );
		try {
			
			Map userMap = getUserInfo();
			String userId = userMap.get("userId") != null ? userMap.get("userId").toString(): "";//토큰정보 userID
				
					UserMstEntity userEntity = userMstRepository.getUserByUserId(userId).orElse(null);
					if(userEntity == null)  	
					{//신규 회원 등록      
						KakaoUserInfoDto info = keycloakService.kakaoToken(userId);
						KakaoUserInfoDto.KakaoAccount acc = info != null ? info.getKakao_account() : null;
						// 생일(MMDD)→BIRTHDAY, 출생년도(YYYY)→BIRTHYEAR 로 분리 저장
						String birthday  = acc != null && acc.getBirthday()  != null ? acc.getBirthday()  : "";
						String birthYear = acc != null && acc.getBirthyear() != null ? acc.getBirthyear() : "";
						String address  = info != null? info.getAddress() : "";
						userEntity = UserMstEntity.builder()
				        		.userId     (userId) // 키클락 아이디
				        		.email      (userId)
				        		.phone      (userMap.get("phone")     != null ? userMap.get("phone").toString(): "")
				        		.name       (userMap.get("name")      != null ? userMap.get("name").toString(): "" )
				        		.gender     (userMap.get("gender")    != null ? userMap.get("gender").toString(): "" )
				        		.ageRange   (userMap.get("age_range") != null ? userMap.get("age_range").toString(): "" )
				        		.address    (address)
				        		.birthday   (birthday)
				        		.birthYear  (birthYear)
				        		.useYn      ("Y")
				        		.snsType    ("KAKAO")
				        		.createUser ("mobile main")
				        		.signupStoreId(condition.getStoreId()) // 가입 점포 고정(최초 1회)
				        		.lastLoginDate(LocalDateTime.now())
				        		.build();
				        	
			        	userMstRepository.save(userEntity);
					}else if(userEntity.getAddress() == null || userEntity.getAddress().isEmpty() ) {
						System.out.println("Mobile 주소정보 없음 카카오 요청 ################################################################ " );
						KakaoUserInfoDto info = keycloakService.kakaoToken(userId);
						if(info != null) {
							userEntity.setAddress(info.getAddress());
							userEntity.setUpdateDate(LocalDateTime.now());
							userEntity.setUseYn("Y");
							
						}
						userEntity.setLastLoginDate(LocalDateTime.now());
						userMstRepository.save(userEntity);
					}
					else {
						userEntity.setUseYn("Y");
						userEntity.setLastLoginDate(LocalDateTime.now());
						userMstRepository.save(userEntity);
					}
					
					//로그인 시 하루 1회만 로그인 로그 저장
					saveLoginLogOncePerDay(userEntity);
		}catch(Exception e){
			
			e.printStackTrace();
		}
		
		return mobileSpecialMapper.selectMobileSpecialList(condition);
	}
	
	
	/**
     * 로그인 시 하루 1회만 로그인 로그 저장
     */
    @Transactional
    public void saveLoginLogOncePerDay(UserMstEntity userEntity) {

    	try {
    		LocalDate today = LocalDate.now();

            LocalDateTime startOfDay = today.atStartOfDay();
            LocalDateTime endOfDay   = today.atTime(LocalTime.MAX);

            boolean existsToday = auditLogRepository
                    .existsByUserIdAndActionTypeAndCreateDateBetween(
                    		userEntity.getUserId(),
                            "LOGIN",
                            startOfDay,
                            endOfDay
                    );

            if (existsToday) {
                return;
            }

            AuditLogEntity log = AuditLogEntity.builder()
                    .userId(userEntity.getUserId())
                    .userName(userEntity.getName())
                    .actionType("LOGIN")
                    .build();

            auditLogRepository.save(log);
		} catch (Exception e) {
			// TODO: handle exception
			log.error("saveLoginLogOncePerDay error login id = {}",userEntity.getUserId());
		}
        
    }
	
	
	public List<SpecialDtlDto> specialDetailList(SpecialSearchCondition condition ){
		
		return mobileSpecialMapper.selectMobileSpecialDtlList(condition);
	}
	
	public List<SpecialDtlDto> specialRemainQtyList(SpecialSearchCondition condition ){
		
		return mobileSpecialMapper.selectMobileSpecialRemainQtyList(condition);
	}
	
	public List<SpecialDtlDto> productDetailImageList(SpecialSearchCondition condition ){
		
		return mobileSpecialMapper.productDetailImageList(condition);
	}
	
	
	public void reservatinSave() {
		
	}
	
	/**
     * 특가 예약 저장(덮어쓰기 업서트)
     * - 프론트에서 넘어온 최종 수량(qty)을 그대로 반영
     * - unitPrice/reservationPrice 는 서버에서 재조회/재계산(보안/정합성)
     * - (SPECIAL_ID, USER_ID) 기준으로 MST 단일화, DTL은 (MST_ID, DTL_ID) 단일화
	 * @throws Exception 
     */
    @Transactional
    public boolean reservationListSave(ReservationApplyRequestDto dto) throws Exception {

        // ─────────────────────────────────────────
        // 0) 파라미터 정리
        // ─────────────────────────────────────────
        String userId    = dto.getUserId();
        Long   specialId = dto.getSpecialId();

        // ─────────────────────────────────────────
        // 1) MST 조회/생성  (uq_special_user: SPECIAL_ID + USER_ID 고유)
        // ─────────────────────────────────────────
        SpecialRsvMstEntity mst;
        
        // ─────────────────────────────────────────
        //  USER 조회/생성  
        // ─────────────────────────────────────────
        UserMstEntity userEntity = userMstRepository.getUserByUserId(dto.getUserId()).orElse(null);
        if(userEntity == null) {
        	Map userMap = getUserInfo();
        	// 예약 경로엔 storeId 직접 파라미터가 없어 특가의 점포로 가입 점포를 확정(최초 1회)
        	Long signupStoreId = specialMstRepository.findById(specialId)
        			.map(SpecialMstEntity::getStoreId)
        			.orElse(null);
        	userEntity = UserMstEntity.builder()
        		.userId     (userMap.get("email")     != null ? userMap.get("email").toString() : dto.getUserId()) // 키클락 아이디
        		.email      (userMap.get("email")     != null ? userMap.get("email").toString() : dto.getUserId())
        		.phone      (userMap.get("phone")     != null ? userMap.get("phone").toString(): "")
        		.name       (userMap.get("name")      != null ? userMap.get("name").toString(): "" )
        		.gender     (userMap.get("gender")    != null ? userMap.get("gender").toString(): "" )
        		.ageRange   (userMap.get("age_range") != null ? userMap.get("age_range").toString(): "" )
        		.address    (userMap.get("address")   != null ? userMap.get("address").toString(): "" )
        		.useYn      ("Y")
        		.snsType    ("KAKAO")
        		.createUser ("system")
        		.signupStoreId(signupStoreId) // 가입 점포 고정(최초 1회)
        		.build();
        	
        	userMstRepository.save(userEntity);
        }else {
        	userEntity.setUseYn("Y");
        }

        // 우선 dto.rsvMstId 가 넘어왔으면 우선 사용(정합성 체크)
        if (dto.getRsvMstId() != null && dto.getRsvMstId() > 0L)
        {
            mst = specialRsvMstRepository.findById(dto.getRsvMstId())
                    .orElseThrow(() -> new IllegalArgumentException("예약 마스터 없음: " + dto.getRsvMstId()));
            
            if (!mst.getSpecialId().equals(specialId) || !mst.getUserId().equals(userId)) {
                throw new IllegalStateException("예약 마스터/요청 정보 불일치(specialId/userId)");
            }
            //기존 예약 수정
            mst.setReservationStatus(ReservationStatus.RESERVATION);
            mst.setUpdateUser(userId);
        } 
        else 
        {
        	//방어코드 예약 마스터에 특가아이디, 유저아이디 , 상태가 reservation인 건이 있으면 신규 만들면 안됨.
        	mst = specialRsvMstRepository.findBySpecialIdAndUserIdAndStatus(dto.getRsvMstId() ,userId , ReservationStatus.RESERVATION );
        	if(mst == null) 
        	{
        		//신규 예약
            	mst = SpecialRsvMstEntity.builder()
                        .specialId         (specialId)
                        .userId            (userId)
                        .reservationStatus (ReservationStatus.RESERVATION)
                        .reservationCnt    (dto.getTotalQty())
                        .reservationPrice  (dto.getTotalPrice())
                        .salesCnt          (0)
                        .salesPrice        (0)
                        // VISIT_DATE NOT NULL: 당장은 now로 기본값, 필요시 dto에서 받아서 세팅
                        .visitDate         (LocalDateTime.now())
                        .cancelYn          ("N")
                        .visitYn           ("N")
                        .description       ("sns 예약")
                        .createUser        (userId)
                        .build();
            	specialRsvMstRepository.save(mst);
        	}
        	
        	
        	//10 30일 백업코드
            // (SPECIAL_ID, USER_ID)로 존재 확인 → 없으면 생성
//            mst = specialRsvMstRepository.findFirstBySpecialIdAndUserId(specialId, userId)
//                    .orElseGet(() -> {
//                        SpecialRsvMstEntity created = SpecialRsvMstEntity.builder()
//                                .specialId         (specialId)
//                                .userId            (userId)
//                                .reservationStatus (ReservationStatus.RESERVATION)
//                                .reservationCnt    (dto.getTotalQty())
//                                .reservationPrice  (dto.getTotalPrice())
//                                .salesCnt          (0)
//                                .salesPrice        (0)
//                                // VISIT_DATE NOT NULL: 당장은 now로 기본값, 필요시 dto에서 받아서 세팅
//                                .visitDate         (LocalDateTime.now())
//                                .cancelYn          ("N")
//                                .visitYn           ("N")
//                                .description       ("sns 예약")
//                                .build();
//                        return specialRsvMstRepository.save(created);
//                    });
        }

        // ─────────────────────────────────────────
        // 2) DTL 업서트(최종 수량 덮어쓰기)
        //    - qty == 0 → 삭제(또는 CANCEL_YN='Y'로 소프트 취소 정책 가능)
        //    - unitPrice 는 반드시 서버에서 재조회(special_dtl.SALES_PRICE)
        // ─────────────────────────────────────────
        if (dto.getReservations() != null) {
        	
        	
        	//dto.getTotalQty() 해당 상품이 예약 가능한지 확인하고 예약 불가시 리턴
        	//기존 예약 자라면 기존 예약수량을 뺀 수량을 가지고 처리해야함.
        	// mst = specialRsvMstRepository.findFirstBySpecialIdAndUserId(specialId, userId) //기존예약정보 가져옴qty만 사용
        	boolean isRsv = true;//예약진행 가능여부
        	String tempProductNm  = "";
        	Integer tempReqQty    = 0;  // 기존 예약 + 신규 요청수량
        	Integer tempRemainQty = 0; // 남은수량
        	for (ReservationApplyRequestDto.Item item : dto.getReservations()) {

                Long    specialDtlId = item.getSpecialDtlId();
                Integer oldQty   = item.getOldQty() == null ? 0 : item.getOldQty() ;
                Integer addQty   = item.getAddQty() == null ? 0 : item.getAddQty() ;
                Integer finalQty = oldQty + addQty;
                
            	Map<String, Object> paramMap = new HashMap<>();
            	paramMap.put("userId"      , userId);
            	paramMap.put("specialId"   , specialId);
            	paramMap.put("specialDtlId", specialDtlId);
            	paramMap.put("oldQty"      , item.getOldQty());   //기존 수량
            	paramMap.put("addQty"      , item.getAddQty());//신규 요청 수량
            	
            	boolean isUpdate = mobileSpecialMapper.isUpdate(paramMap);
            	if(isUpdate) 
            	{//업데이트 가능여부 확인 
            		mobileSpecialMapper.updateReservationQty(paramMap);
            	}
            	else {
            		isRsv = false;
            		tempReqQty = finalQty;
            		SpecialDtlDto sd = specialDtlRepository.findProductNmAndRemainQty(specialDtlId);
            		tempProductNm = sd.getProductNm();
            		tempRemainQty = sd.getRemainQty();
            		break;
            	}
        	}
        	//예약가능스량 초과로 인한 롤백
            if(!isRsv) {
            	String msg = tempProductNm+" 잔여수량 부족으로 예약이 불가합니다"  ;
            	throw new GlobalException( ResponseCode.RESERVATION_OVER , msg);
            }
        	
        	
            for (ReservationApplyRequestDto.Item item : dto.getReservations()) {

                Long    specialDtlId = item.getSpecialDtlId();
                Integer oldQty   = item.getOldQty() == null ? 0 : item.getOldQty() ;
                Integer addQty   = item.getAddQty() == null ? 0 : item.getAddQty() ;
                Integer finalQty = oldQty + addQty;
                
                // 특가 단가 재조회
                SpecialDtlEntity sd = specialDtlRepository.findById(specialDtlId)
                        .orElseThrow(() -> new GlobalException(ResponseCode.SPECIAL_PRICE_ZERO,"특가 상새 단가 조회중 에러: " + specialDtlId));
                
                int unitPrice        = sd.getSalesPrice() == null ? 0 : sd.getSalesPrice();
                int reservationPrice = finalQty * unitPrice;

                Optional<SpecialRsvDtlEntity> optDtl =
                        specialRsvDtlRepository.findBySpecialRsvMstIdAndSpecialDtlId(mst.getSpecialRsvMstId(), specialDtlId);


             
                if (finalQty == 0) {
                    // 0 → 삭제(하드 삭제). 소프트 취소 정책 필요하면 여기서 UPDATE로 전환 가능.
                	final long tt = mst.getSpecialRsvMstId();
                    optDtl.ifPresent(existing ->
                            specialRsvDtlRepository.deleteBySpecialRsvMstIdAndSpecialDtlId(tt, specialDtlId)
                    );
                    continue;
                }
                
                //예약 저장시 qty만 있고 예약금이 없는 경우가 발생하여 로그 추가
                if(finalQty > 0 && reservationPrice == 0) {
            		log.error("개인 예약 에러 ::: reservationListSave >> specialDtlId={}, unitPrice = {} , finalQty ={} , reservationPrice= {} , getSpecialId = {} ,getUserId = {} " ,
            				specialDtlId, unitPrice , finalQty , reservationPrice , dto.getSpecialId() , dto.getUserId());
            	}

                if (optDtl.isPresent()) {
                    // UPDATE
                    SpecialRsvDtlEntity dtl = optDtl.get();
                    dtl.setReservationCnt   (finalQty);
                    dtl.setUnitPrice        (unitPrice);
                    dtl.setReservationPrice (reservationPrice);
                    dtl.setCancelYn         ("N");
                    dtl.setUpdateUser       (userId);
                    dtl.setUpdateDate       (LocalDateTime.now());
                    specialRsvDtlRepository.save(dtl);
                } else {
                    // INSERT
                    SpecialRsvDtlEntity dtl = SpecialRsvDtlEntity.builder()
                            .specialRsvMstId (mst.getSpecialRsvMstId())
                            .specialDtlId    (specialDtlId)
                            .unitPrice       (unitPrice)
                            .reservationCnt  (finalQty)
                            .reservationPrice(reservationPrice)
                            .salesCnt        (0)
                            .salesPrice      (0)
                            .cancelYn        ("N")
                            .createUser      (userId)
                            .build();
                    specialRsvDtlRepository.save(dtl);
                }
            }
            
            
            
        }

        // ─────────────────────────────────────────
        // 3) MST 합계 재계산(RESERVATION_CNT / RESERVATION_PRICE)
        // ─────────────────────────────────────────
        Integer sumCnt   = specialRsvDtlRepository.sumReservationCntByMstId(mst.getSpecialRsvMstId());
        Integer sumPrice = specialRsvDtlRepository.sumReservationPriceByMstId(mst.getSpecialRsvMstId());

        mst.setReservationCnt   (sumCnt   == null ? 0 : sumCnt);
        mst.setReservationPrice (sumPrice == null ? 0 : sumPrice);
        // 상태/설명 등은 필요시 dto로부터 갱신 가능: mst.setReservationStatus(...)
        mst.setReservationStatus(ReservationStatus.RESERVATION);
        specialRsvMstRepository.save(mst);

        return true;
    }
    
    /** 상품별 예약 취소
     * 해당 스페셜아이디에 모든 상품이 0이면 예약 취소로 상태값 변경해야함
     *  */
    @Transactional
    public boolean cancelReservationItems(ReservationApplyRequestDto dto) {

        String userId    = dto.getUserId();
        Long   specialId = dto.getSpecialId();

        
        log.info("1 cancelReservationItems   dto = {}" , dto.toString());
//        // (SPECIAL_ID, USER_ID)로 MST 조회 (없으면 에러)
//        SpecialRsvMstEntity mst = specialRsvMstRepository
//                .findFirstBySpecialIdAndUserId(specialId, userId)
//                .orElseThrow(() -> new IllegalArgumentException("예약 마스터 없음(specialId/userId)"));
//        log.info("1-1 cancelReservationItems ");
//        // rsvMstId가 넘어왔으면 정합성 체크(옵션)
//        if (dto.getRsvMstId() != null && dto.getRsvMstId() > 0
//                && !dto.getRsvMstId().equals(mst.getSpecialRsvMstId())) {
//            throw new IllegalStateException("요청 rsvMstId 불일치");
//        }
        log.info("2 cancelReservationItems ");

        // 개별 DTL 취소
        if (dto.getReservations() != null) 
        {
            for (var it : dto.getReservations()) 
            {
                Long dtlId = it.getSpecialDtlId();

                // 현재 예약 상세 조회
                var rsvDtlOpt = specialRsvDtlRepository.findBySpecialRsvMstIdAndSpecialDtlId(it.getRsvMstId(), dtlId);
                if (rsvDtlOpt.isPresent())
                {
                    var rsvDtl = rsvDtlOpt.get();

                    // ▶ 소프트 취소 (cancel_yn = 'Y')
                    int updated = specialRsvDtlRepository.softCancel(it.getRsvMstId(), dtlId);

                    if (updated > 0) {
                        // 취소 수량 복원 처리
                        int cancelQty = rsvDtl.getReservationCnt() == null ? 0 : rsvDtl.getReservationCnt();

                        specialDtlRepository.findById(dtlId).ifPresent(specialDtl -> {
                            int remain = specialDtl.getRemainQty() == null ? 0 : specialDtl.getRemainQty();
                            specialDtl.setRemainQty(remain + cancelQty);
                            specialDtlRepository.save(specialDtl);
                        });
                    }
                    
                    SpecialRsvMstEntity mst = specialRsvMstRepository
                            .findById(it.getRsvMstId())
                            .orElseThrow(() -> new IllegalArgumentException("예약 마스터 없음 : " + it.getRsvMstId()));
                    
                 // MST 합계 재계산
                    Integer sumCnt   = specialRsvDtlRepository.sumReservationCntByMstId(it.getRsvMstId());
                    Integer sumPrice = specialRsvDtlRepository.sumReservationPriceByMstId(it.getRsvMstId());
                    mst.setReservationCnt   (sumCnt   == null ? 0 : sumCnt);
                    mst.setReservationPrice (sumPrice == null ? 0 : sumPrice);
                    mst.setUpdateDate       (LocalDateTime.now()); // @UpdateTimestamp 쓰면 자동
                    mst.setUpdateUser       (userId);
                 // ▶ 모든 예약 상품 수량이 0이면 MST 상태를 CANCEL로 변경
                    //    - 상태 컬럼명이 예: RSV_STATUS (VARCHAR) 라면 아래처럼 사용
                    //    - ENUM을 쓰는 경우엔 ReservationStatus.CANCEL 등으로 세팅
                    if (mst.getReservationCnt() == null || mst.getReservationCnt() <= 0) {
                        // 주의: 컬럼/필드명은 프로젝트 실제 명칭에 맞춰 변경
                        mst.setReservationStatus(ReservationStatus.CANCEL) ;  // ← 여기 한 줄로 요구사항 충족
                        // 필요 시 취소 일자/사용자도 함께 업데이트
                        
                         
                    } else {
                        // 수량이 남아있다면 정상 진행 상태로 유지/복구 (옵션)
                        // mst.setRsvStatus("ACTIVE");
                    }
                }
            }
        }

        log.info("3 cancelReservationItems ");
        

        
        
        log.info("4 cancelReservationItems ");
        
        
        log.info("5 cancelReservationItems ");

        
        
        return true;
    }
    
    
    /**토큰에서 user정보 조회 */
    public Map<String, String> getUserInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Map<String, String> userInfo = new HashMap<>();

        if (authentication != null && authentication.getPrincipal() instanceof Jwt jwt) {
            String userId = jwt.getClaim("preferred_username"); // Keycloak 기본 claim
            String email  = jwt.getClaim("email");
            String name   = jwt.getClaim("user_name");
            String phone   = jwt.getClaim("phone_number");
            String gender   = jwt.getClaim("gender");
            String address   = jwt.getClaim("address");
            String age_range   = jwt.getClaim("age_range");
            String kakao_token   = jwt.getClaim("kakao_token");
            
            
            System.out.println("신규회원 토큰 정보 start==============================================");
            System.out.println("userId======" + userId);
            System.out.println("kakao_token======" + kakao_token);
            System.out.println("email=======" + email);
            System.out.println("user_name===" + name);
            System.out.println("phone=======" + phone);
            System.out.println("gender======" + gender);
            System.out.println("address=====" + address);
            System.out.println("age_range===" + age_range);
            System.out.println("신규회원 토큰 정보 start==============================================");

            userInfo.put("userId", userId);
            userInfo.put("email", email);
            userInfo.put("name", name);
            userInfo.put("phone", phone);
            userInfo.put("gender", gender);
            userInfo.put("address", address);
            userInfo.put("age_range", age_range);
        }

        return userInfo;
    }
	
    
    
    
    
    /** 모바이 상품 조회 - 라스트픽 , 신규상품 , 매장 제고 */
	public List<ProductMstDto>  selectMobileProductList(SpecialSearchCondition condition){
		
		return mobileSpecialMapper.selectMobileProductList(condition);
	}
	
	/** 모바일 주문내역 조회 2025 10 29 추가내역 */
	public List<SpecialDtlDto> mobileOrderList(SpecialSearchCondition condition ){
		
		return mobileSpecialMapper.selectMobileOrderList(condition);
	}
	
}
