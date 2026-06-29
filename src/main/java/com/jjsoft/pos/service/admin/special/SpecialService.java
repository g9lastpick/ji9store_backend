package com.jjsoft.pos.service.admin.special;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jjsoft.pos.dto.sales.SalesDtlDto;
import com.jjsoft.pos.dto.special.ReservationItemDto;
import com.jjsoft.pos.dto.special.SpecialCalendarEventDto;
import com.jjsoft.pos.dto.special.SpecialColumnsDto;
import com.jjsoft.pos.dto.special.SpecialDtlDto;
import com.jjsoft.pos.dto.special.SpecialMstDto;
import com.jjsoft.pos.dto.special.condition.SpecialSearchCondition;
import com.jjsoft.pos.entity.SalesMstEntity;
import com.jjsoft.pos.entity.SpecialDtlEntity;
import com.jjsoft.pos.entity.SpecialMstEntity;
import com.jjsoft.pos.entity.SpecialRsvDtlEntity;
import com.jjsoft.pos.entity.SpecialRsvMstEntity;
import com.jjsoft.pos.entity.UserMstEntity;
import com.jjsoft.pos.enums.PaymentType;
import com.jjsoft.pos.enums.ProgressType;
import com.jjsoft.pos.enums.ReservationStatus;
import com.jjsoft.pos.enums.ResponseCode;
import com.jjsoft.pos.enums.SalesStatus;
import com.jjsoft.pos.enums.SalesType;
import com.jjsoft.pos.exception.GlobalException;
import com.jjsoft.pos.mapper.SpecialMapper;
import com.jjsoft.pos.mapper.GroupbuyAdminMapper;
import com.jjsoft.pos.service.admin.groupbuy.GroupbuyAdminService;
import com.jjsoft.pos.dto.special.UserPickupItemDto;
import com.jjsoft.pos.dto.special.PickupBatchRequestDto;
import com.jjsoft.pos.dto.special.PickupUserDto;
import com.jjsoft.pos.repository.ProductDtlRepository;
import com.jjsoft.pos.repository.SalesDtlRepository;
import com.jjsoft.pos.repository.SalesMstRepository;
import com.jjsoft.pos.repository.SpecialDtlRepository;
import com.jjsoft.pos.repository.SpecialMstRepository;
import com.jjsoft.pos.repository.SpecialRsvDtlRepository;
import com.jjsoft.pos.repository.SpecialRsvMstRepository;
import com.jjsoft.pos.repository.UserMstRepository;
import com.jjsoft.pos.service.admin.sales.SalesService;
import com.jjsoft.pos.util.PiiMaskUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SpecialService {

	
	private final SpecialMstRepository specialMstRepository;
	private final SpecialDtlRepository specialDtlRepository;
	private final SpecialRsvMstRepository specialRsvMstRepository;
	private final SpecialRsvDtlRepository specialRsvDtlRepository;
	private final UserMstRepository userMstRepository;
	private final SpecialMapper specialMapper;
	private final SalesMstRepository salesMstRepository;
	private final SalesDtlRepository salesDtlRepository;
	private final ProductDtlRepository productDtlRepository;
	private final SalesService salesService;
	private final GroupbuyAdminMapper groupbuyAdminMapper;
	private final GroupbuyAdminService groupbuyAdminService;
	
	
	
	public List<SpecialCalendarEventDto> selectSpecialsForCalendar(String startDate, String endDate ,int locationId) {
		return specialMapper.selectSpecialsForCalendar(startDate,endDate , locationId);
		
	}
	
	public List<SpecialDtlDto> getSpecialProductList(Long specialId) {
		return specialMapper.selectSpecialProductList(specialId);

	}

	/** 특가 등록 시 노출 소비기한 선택용: 가용재고(FIFO 잔여)≥1 lot의 소비기한 목록 (임박순) */
	public List<Map<String, Object>> getAvailableLots(Long productId, Long locationId) {
		return specialMapper.selectAvailableLots(productId, locationId);
	}

	/** "yyyy-MM-dd"(또는 "yy-MM-dd"·ISO) 문자열을 LocalDate로. 비거나 파싱 불가면 null → 자동 노출(가용 lot 최소값) */
	private LocalDate parseExpDate(String s) {
		if (s == null || s.trim().isEmpty()) return null;
		String v = s.trim();
		if (v.length() >= 10) v = v.substring(0, 10);   // "2026-06-10T00:00:00" → "2026-06-10"
		try {
			return LocalDate.parse(v);
		} catch (Exception e) {
			return null;
		}
	}

	@Transactional
	public Long saveOrUpdate(SpecialMstDto dto , String userId) {
	    SpecialMstEntity mstEntity;

	    // 1. 특가 마스터 수정 or 생성
	    if (dto.getSpecialId() != null && dto.getSpecialId() > 0) {
	        mstEntity = specialMstRepository.findById(dto.getSpecialId())
	                .orElseThrow(() -> new IllegalArgumentException("특가 정보가 존재하지 않습니다. ID=" + dto.getSpecialId()));

	        mstEntity.update(
	            dto.getStoreId(),
	            dto.getLocationId(),
	            dto.getSpecialNm(),
	            dto.getStartDate(),
	            dto.getEndDate(),
	            ProgressType.getProgressTypeFromKey(dto.getProgressType()),
	            dto.getSpecialType(),
	            dto.getDescription(),
	            userId,
	            dto.getPickupEndDate()
	        );
	    } else {
	        mstEntity = SpecialMstEntity.builder()
	                .storeId      (dto.getStoreId())
	                .locationId   (dto.getLocationId())
	                .specialNm    (dto.getSpecialNm())
	                .startDate    (dto.getStartDate())
	                .endDate      (dto.getEndDate())
	                .progressType (ProgressType.getProgressTypeFromKey(dto.getProgressType()))
	                .specialType  ("EVENT")//dto.getSpecialType() != null ? dto.getSpecialType() : "EVENT"
	                .description  (dto.getDescription())
	                .createUser   (userId)
	                .pickupEndDate(dto.getPickupEndDate())
	                .build();
	    }

	    specialMstRepository.save(mstEntity);
	    Long specialId = mstEntity.getSpecialId();

	    // 2. 특가 상세 삭제 후 재등록
	    specialDtlRepository.deleteBySpecialId(specialId); // 💥 기존 삭제

	    if (dto.getDtlList() != null) {
	        for (SpecialDtlDto d : dto.getDtlList()) {
	            SpecialDtlEntity detail = SpecialDtlEntity.builder()
	                    .specialId     (specialId)
	                    .productId     (d.getProductId())
	                    .qty           (d.getQty())
	                    .limitQty      (d.getLimitQty() == null ? 5 : d.getLimitQty())
	                    .orgSalesPrice (d.getOrgSalesPrice())
	                    .salesPrice    (d.getSalesPrice())
	                    .salesRate     (d.getSalesRate())
	                    .description   (d.getDescription())
	                    .pickupTag     (d.getPickupTag())
                    .tagOverride   (d.getTagOverride())
	                    .expirationDate(parseExpDate(d.getExpirationDate()))
	                    .createUser    (userId)
	                    .build();

	            specialDtlRepository.save(detail);
	        }
	    }
	    
	    return mstEntity.getSpecialId();
	}
	
	@Transactional
	public Long saveOrUpdatePartial(SpecialMstDto dto , String userId) {
	    SpecialMstEntity mstEntity = null;

	    // 1. 특가 마스터 수정 or 생성
	    if (dto.getSpecialId() != null && dto.getSpecialId() > 0) {
	        mstEntity = specialMstRepository.findById(dto.getSpecialId())
	                .orElseThrow(() -> new IllegalArgumentException("특가 정보가 존재하지 않습니다. ID=" + dto.getSpecialId()));

	        mstEntity.setSpecialNm(dto.getSpecialNm());
	        mstEntity.setProgressType(ProgressType.getProgressTypeFromKey(dto.getProgressType()));
	        mstEntity.setPickupEndDate(dto.getPickupEndDate());
	        mstEntity.setUpdateUser(userId);
	        specialMstRepository.save(mstEntity);

	        // 진행중(START) 상태에서 허용되는 dtl 부분수정: 노출 소비기한 + 픽업 시작 시간만.
	        // 행 삭제·재삽입 없이 기존 SPECIAL_DTL_ID 유지 → 예약(special_rsv_dtl) 참조 보존.
	        if (dto.getDtlList() != null) {
	            for (SpecialDtlDto d : dto.getDtlList()) {
	                if (d.getSpecialDtlId() == null) continue;
	                SpecialDtlEntity detail = specialDtlRepository.findById(d.getSpecialDtlId()).orElse(null);
	                if (detail == null) continue;
	                detail.setExpirationDate(parseExpDate(d.getExpirationDate()));
	                detail.setPickupTag(d.getPickupTag());
	                detail.setUpdateUser(userId);
	                specialDtlRepository.save(detail);
	            }
	        }
		    return mstEntity.getSpecialId();
	    } else {
	    	return null;
	    }
	}
	
	
	
	/** 특가등록 삭제 */
	@Transactional
	public void deleteSpecial(Long specialId) {
	    // 1. 예약자 존재 여부 확인
	    boolean hasReservation = specialRsvMstRepository.existsBySpecialId(specialId);

	    if (hasReservation) {
//	    	if (hasReservation) {
	        throw new IllegalStateException("예약자가 있는 특가는 삭제할 수 없습니다.");
	    }

	    // 2. 예약 상세 삭제 (안전하게 넣어둠, 없어도 무방)
	    specialRsvDtlRepository.deleteBySpecialId(specialId);

	    // 3. 예약 마스터 삭제
	    specialRsvMstRepository.deleteBySpecialId(specialId);

	    // 4. 특가 상세 삭제
	    specialDtlRepository.deleteBySpecialId(specialId);

	    // 5. 특가 마스터 삭제
	    specialMstRepository.deleteById(specialId);
	}
	
	
	
	


    
    
    /**
     * 예약 삭제 (마스터 + 상세 삭제)
     */
    @Transactional
    public void deleteReservation(Long specialRsvMstId) {
        SpecialRsvMstEntity mst = specialRsvMstRepository.findById(specialRsvMstId)
                .orElseThrow(() -> new GlobalException(ResponseCode.NOT_FOUND_OBJECT, "삭제할 예약 정보가 없습니다."));

        // 상세 먼저 삭제
        specialRsvDtlRepository.deleteBySpecialRsvMstId(mst.getSpecialRsvMstId());

        // 마스터 삭제
        specialRsvMstRepository.delete(mst);
    }
    
    
    
    
    /** 예약 리스트 조회 */
    public List<ReservationItemDto> getReservatioList(SpecialSearchCondition condition) {
    	
    	// Step1: 컬럼 정의 조회
    	List<SpecialColumnsDto> colDtoList = specialMapper.selectSpecialColumns(condition);

    	// Step2: 컬럼명을 동적으로 생성
    	List<String> colList = colDtoList.stream()
    	        .map(dto -> dto.getSpecialDtlId())
    	        .toList(); // Java 16+ or use .collect(Collectors.toList())

    	// Step2-1: ${col} 은 MyBatis 문자열 치환이므로 식별자 안전성 검증.
    	// XML에서 PRODUCT_<숫자> 형태로 사용되며 REPLACE 후 CAST UNSIGNED 한다. 영숫자/언더스코어만 허용.
    	for (String col : colList) {
    		if (col == null || !col.matches("^[A-Za-z0-9_]{1,64}$")) {
    			throw new GlobalException(ResponseCode.BAD_REQUEST,
    					"동적 컬럼명에 허용되지 않은 문자가 포함되어 있습니다: " + col);
    		}
    	}

    	// Step3: 조건 객체에 컬럼리스트 세팅
    	condition.setColList(colList);

    	// Step4: 실제 피벗 조회
    	List<Map<String, Object>> rawList = specialMapper.selectPivotReservation(condition);
    	
    	List<ReservationItemDto> resultList = rawList.stream()
    		    .map(row -> {
    		        ReservationItemDto dto = new ReservationItemDto();
    		        dto.setUserId            ((String)  row.get("USER_ID"));
    		        dto.setName              (row.get("NAME")           != null ? (String)  row.get("NAME") : "");
    		        dto.setPhone             (row.get("PHONE")          != null ? (String)  row.get("PHONE"): "");
    		        dto.setLocationId        (row.get("LOCATION_ID")          != null ? ((Number) row.get("LOCATION_ID")).longValue()        : 0L);
    		        dto.setSpecialRsvMstId   (row.get("SPECIAL_RSV_MST_ID")   != null ? ((Number) row.get("SPECIAL_RSV_MST_ID")).longValue() : 0L);
    		        dto.setSpecialId         (row.get("SPECIAL_ID")           != null ? ((Number) row.get("SPECIAL_ID")).longValue()         : 0L);
    		        dto.setReservationPrice  (row.get("RESERVATION_PRICE")    != null ? ((Number) row.get("RESERVATION_PRICE")).intValue()   : 0);
    		        dto.setSalesPrice        (row.get("SALES_PRICE")          != null ? ((Number) row.get("SALES_PRICE")).intValue()         : 0);
    		        dto.setUnitPrice         (row.get("UNIT_PRICE")           != null ? ((Number) row.get("UNIT_PRICE")).intValue()          : 0);
    		        dto.setTotQty            (row.get("TOT_QTY")              != null ? ((Number) row.get("TOT_QTY")).intValue()             : 0);
    		        dto.setRemainQty         (row.get("REMAIN_QTY")           != null ? ((Number) row.get("REMAIN_QTY")).intValue()          : 0);
    		        dto.setReservationStatus ((String)  row.get("RESERVATION_STATUS"));
    		        dto.setGender            ((String)  row.get("GENDER"));
    		        dto.setAge               ((String)  row.get("AGE"));
    		        dto.setDescription       ((String)  row.get("DESCRIPTION"));


    		        // 🔹 동적 컬럼 파싱
    		        Map<String, Integer> productQtyMap   = new HashMap<>();
    		        Map<String, Long>    rsvDtlIdMap     = new HashMap<>();
    		        Map<String, Long>    rsvMstIdMap     = new HashMap<>();
    		        Map<String, Long>    specialIdMstMap = new HashMap<>();
    		        Map<String, Long>    specialIdDtlMap = new HashMap<>();
    		        Map<String, Long>    specialPriceMap = new HashMap<>();
    		        /* 2025 10 31 추가내역 */
    		        Map<String, Long>    totQtyMap    = new HashMap<>();
    		        Map<String, Long>    remainQtyMap = new HashMap<>();
    		        for (String key : row.keySet()) {
//    		        	if (key.matches("^PRODUCT_\\d+$")) {
    		        		Object val = row.get(key);
    		                if (key.startsWith("PRODUCT_")) 
    		                {
    		                    productQtyMap.put(key, val instanceof Number ? ((Number) val).intValue() : 0);
    		                } 
    		                else if (key.startsWith("SPECIAL_RSV_DTL_ID_PRODUCT_") && val instanceof Number) 
    		                {
    		                    rsvDtlIdMap.put(key.replace("SPECIAL_RSV_DTL_ID_", ""), ((Number) val).longValue());
    		                } 
    		                else if (key.startsWith("SPECIAL_RSV_MST_ID_PRODUCT_") && val instanceof Number) 
    		                {
    		                    rsvMstIdMap.put(key.replace("SPECIAL_RSV_MST_ID_", ""), ((Number) val).longValue());
    		                } 
    		                else if (key.startsWith("SPECIAL_MST_ID_PRODUCT_") && val instanceof Number) 
    		                {
    		                	specialIdMstMap.put(key.replace("SPECIAL_MST_ID_", ""), ((Number) val).longValue());
    		                }
    		                else if (key.startsWith("SPECIAL_DTL_ID_PRODUCT_") && val instanceof Number) 
    		                {
    		                	specialIdDtlMap.put(key.replace("SPECIAL_DTL_ID_", ""), ((Number) val).longValue());
    		                }
    		                else if (key.startsWith("SPECIAL_PRICE_") && val instanceof Number) 
    		                {
    		                	specialPriceMap.put(key.replace("SPECIAL_PRICE_", "") , ((Number) val).longValue());
    		                }
    		                else if (key.startsWith("SPECIAL_TOT_QTY_") && val instanceof Number) 
    		                {
    		                	totQtyMap.put(key.replace("SPECIAL_TOT_QTY_", "") , ((Number) val).longValue());
    		                }
    		                else if (key.startsWith("SPECIAL_REMAIN_QTY_") && val instanceof Number) 
    		                {
    		                	remainQtyMap.put(key.replace("SPECIAL_REMAIN_QTY_", "") , ((Number) val).longValue());
    		                }
//    		        	}
    		            
    		        }
    		        dto.setProductQtyMap(productQtyMap);
    		        dto.setRsvDtlIdMap(rsvDtlIdMap);
    		        dto.setRsvMstIdMap(rsvMstIdMap);
    		        dto.setSpecialIdDtlMap(specialIdDtlMap);
    		        dto.setSpecialIdMstMap(specialIdMstMap);
    		        dto.setSpecialPriceMap(specialPriceMap);
    		        
    		        dto.setTotQtyMap(totQtyMap);
    		        dto.setRemainQtyMap(remainQtyMap);
    		        
    		        
    		        return dto;
    		    })
    		    .collect(Collectors.toList());

    	//상태값 한글화
    	resultList.forEach(x->{
    		x.setReservationStatus(ReservationStatus.getReservationStatusNameFromKey(x.getReservationStatus()));
    	});
    	// 고객정보 마스킹: 이름·전화 마스킹, 성별·나이 제거 (userId는 저장 키이므로 유지)
    	resultList.forEach(d -> {
    		d.setName(PiiMaskUtil.maskName(d.getName()));
    		d.setPhone(PiiMaskUtil.maskPhone(d.getPhone()));
    		d.setGender(null);
    		d.setAge(null);
    	});
    	return resultList;
    }
    
    
    
    /** 동적컬럼 조회 */
    public List<SpecialColumnsDto> getSpecialColumns(SpecialSearchCondition condition) {
    	List<SpecialColumnsDto> list = specialMapper.selectSpecialColumns(condition);
    	return list;
    }
    
    /**
	 * 특가 예약 저장 또는 수정
	 * 예약 저장시에만 사용가능 .. . 예약 저장시에는 특가 1건당 예약을 처리하므로
	 * if (dto.getSpecialRsvMstId() == 0 || dto.is_isNew()) { 해당 구문이 성립 하지만 
	 * 건바이건 처리시에는 다른 저장 로직 사용해야함
	 */
    @Transactional
    public boolean reservationListSave(List<ReservationItemDto> list ,String userId) {
        if (list == null || list.isEmpty()) return true;

        // 🔸 1. 마스터 먼저 저장 (신규는 생성, 기존은 그대로 둠)
        Map<String, SpecialRsvMstEntity> mstMap = new HashMap<>();

        for (ReservationItemDto dto : list) {
        	
        	if(dto.getReservationStatus().equals("완료") || dto.getReservationStatus().equals("COMPLETE") ) {
        		continue;
        	}
        	SpecialRsvMstEntity mst;
        	ReservationStatus rStatus = ReservationStatus.getReservationStatusFromDescription(dto.getReservationStatus());
            
            if (dto.getSpecialRsvMstId() > 0) {
            	 // 기존 마스터는 업데이트만
                mst = specialRsvMstRepository.findById(dto.getSpecialRsvMstId())
                    .orElseThrow(() -> new IllegalArgumentException("예약 마스터 없음: " + dto.getSpecialRsvMstId()));

                mst.update(
                    dto.getReservationStatus(),
                    dto.getReservationPrice(),
                    dto.getSalesPrice(),
                    dto.getDescription(),
                    userId
                );
            	
            } else {// 신규인 경우 마스터 생성
            	//user id 생성
                UserMstEntity userEntity = userMstRepository.getUserByUserId(dto.getUserId()).orElse(null);
                if(userEntity == null) {
                	Map userMap = getUserInfo();
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
                    		.createUser (userId)
                    		.build();
                	
                	userMstRepository.save(userEntity);
                }
            	
                mst = SpecialRsvMstEntity.builder()
                    .userId            (dto.getUserId())
                    .specialId         (dto.getSpecialId())
                    .reservationStatus (rStatus)//dto.getReservationStatus()
                    .reservationPrice  (dto.getReservationPrice())
                    .salesPrice        (dto.getSalesPrice())
                    .description       (dto.getDescription())
                    .createUser        (userId)
                    .build();

                specialRsvMstRepository.save(mst);
            }

            //user정보 업데이트
            UserMstEntity userEntity = userMstRepository.getUserByUserId(dto.getUserId())
            .orElseThrow(() -> new IllegalArgumentException("user정보 없음: " + dto.getSpecialRsvMstId()));
            if(dto.getGender() != null && !dto.getGender().isEmpty()) userEntity.setGender(dto.getGender());
            if(dto.getAge()    != null && !dto.getAge().isEmpty()   ) userEntity.setAge(dto.getAge());
            mstMap.put(dto.getUserId(), mst); // userId 기준으로 정리
        }
        
        
        // 🔸 2. 상세 저장 (존재하면 update, 없으면 insert)
        for (ReservationItemDto dto : list) {
        	
        	if(dto.getReservationStatus().equals("완료") || dto.getReservationStatus().equals("COMPLETE") ) {
        		continue;
        	}
        	
            SpecialRsvMstEntity mst = mstMap.get(dto.getUserId());

            for (Map.Entry<String, Integer> entry : dto.getProductQtyMap().entrySet()) {
                String key        = entry.getKey(); // PRODUCT_119
                Long specialDtlId = Long.parseLong(key.replace("PRODUCT_", ""));
                Integer qty       = entry.getValue();
                
                

//                if (qty == null || qty <= 0) continue;
                
                
               
                
                // 새 예약수량
                int newQty = (qty == null ? 0 : qty);

                // 기존 예약 상세 조회
                Optional<SpecialRsvDtlEntity> optDtl =
                        specialRsvDtlRepository.findBySpecialRsvMstIdAndSpecialDtlId(mst.getSpecialRsvMstId(), specialDtlId);

                // 기존 예약 수량
                int prevQty = optDtl.map(d -> d.getReservationCnt() == null ? 0 : d.getReservationCnt()).orElse(0);

                // 증감량 (양수면 차감, 음수면 복원)
                int delta = newQty - prevQty;

                // ▶ SPECIAL_DTL 재고 반영
                SpecialDtlEntity specialDtl = specialDtlRepository.findById(specialDtlId)
                        .orElseThrow(() -> new IllegalArgumentException("특가 상세 없음: " + specialDtlId));
                
                
                int specialPrice  = specialDtl.getSalesPrice();

                int remain = (specialDtl.getRemainQty() == null ? specialDtl.getQty() : specialDtl.getRemainQty());
                specialDtl.setRemainQty(remain - delta);   // delta > 0 → 차감, delta < 0 → 복원
                specialDtlRepository.save(specialDtl);
                
                

                if (optDtl.isPresent()) {
                    // 🔁 update
                    SpecialRsvDtlEntity dtl = optDtl.get();
                    dtl.setReservationCnt  (qty);
                    dtl.setUnitPrice       (specialPrice);
                    dtl.setReservationPrice(qty * specialPrice);
                    dtl.setCancelYn        ("N");
                    dtl.setUpdateUser      (userId);
                    dtl.setUpdateDate      (LocalDateTime.now());
                    specialRsvDtlRepository.save(dtl);
                } else {
                    // ➕ insert
                    SpecialRsvDtlEntity newDtl = SpecialRsvDtlEntity.builder()
                        .specialRsvMstId (mst.getSpecialRsvMstId())
                        .specialDtlId    (specialDtlId)
                        .reservationCnt  (qty)
                        .unitPrice       (specialPrice)
                        .reservationPrice(qty * specialPrice)
                        .salesCnt        (0)
                        .salesPrice      (0)
                        .cancelYn        ("N")
                        .createUser      (userId)
                        .createDate      (LocalDateTime.now())
                        .build();
                    specialRsvDtlRepository.save(newDtl);
                }
            } 
        }

        return true;
    }
    
//    @Transactional
//    public boolean reservationDelete(ReservationItemDto row) {
//    	
//    	
//    	 // 1. 예약 마스터 ID가 없으면 예외
//        if (row.getSpecialRsvMstId() == null || row.getSpecialRsvMstId() <= 0) {
//            throw new IllegalArgumentException("삭제할 예약 정보가 없습니다. (specialRsvMstId 없음)");
//        }
//
//        // 2. 예약 마스터 조회
//        SpecialRsvMstEntity mstEntity = specialRsvMstRepository.findById(row.getSpecialRsvMstId())
//            .orElseThrow(() -> new IllegalArgumentException("예약 마스터 정보 없음. ID = " + row.getSpecialRsvMstId()));
//
//        // 3. 예약 상세 정보 삭제
//        specialRsvDtlRepository.deleteBySpecialRsvMstId(mstEntity.getSpecialRsvMstId());
//
//        // 4. 예약 마스터 삭제
//        specialRsvMstRepository.delete(mstEntity);
//
//        return true;
//    }
	    @Transactional
	    public boolean reservationDelete(ReservationItemDto row) {
	
	        // 1. 예약 마스터 ID 검증
	        if (row.getSpecialRsvMstId() == null || row.getSpecialRsvMstId() <= 0) {
	            throw new IllegalArgumentException("삭제할 예약 정보가 없습니다. (specialRsvMstId 없음)");
	        }
	
	        // 2. 예약 마스터 조회
	        SpecialRsvMstEntity mstEntity = specialRsvMstRepository.findById(row.getSpecialRsvMstId())
	            .orElseThrow(() -> new IllegalArgumentException("예약 마스터 정보 없음. ID = " + row.getSpecialRsvMstId()));
	
	        // 3. 예약 상세 리스트 조회
	        List<SpecialRsvDtlEntity> dtlList = specialRsvDtlRepository.findBySpecialRsvMstId(mstEntity.getSpecialRsvMstId());
	
	        // 4. 예약 상세별 → 특가 상세(special_dtl) 재고 복구
	        for (SpecialRsvDtlEntity dtl : dtlList) {
	            SpecialDtlEntity specialDtl = specialDtlRepository.findById(dtl.getSpecialDtlId())
	                .orElseThrow(() -> new IllegalArgumentException("특가 상세 정보 없음. ID = " + dtl.getSpecialDtlId()));
	
	            // ✅ 예약 수량 복원
	            int restoreQty = dtl.getReservationCnt();
	            specialDtl.setRemainQty(specialDtl.getRemainQty() + restoreQty);
	
	            specialDtlRepository.save(specialDtl);
	        }
	
	        // 5. 예약 상세 삭제
	        specialRsvDtlRepository.deleteBySpecialRsvMstId(mstEntity.getSpecialRsvMstId());
	
	        // 6. 예약 마스터 삭제
	        specialRsvMstRepository.delete(mstEntity);
	
	        return true;
	    }
    
    /** 상태값만 업데이트 하는 로직*/
    @Transactional
    public void updateReservationStatus(ReservationItemDto dto) {
        // 상태값 (예: "COMPLETE")
        String newStatus = dto.getReservationStatus();

        // 예약 마스터 ID 목록 추출 (중복 제거)
        Set<Long> reservationMstIds = new HashSet<>(dto.getRsvMstIdMap().values());

        // 각각 업데이트
        for (Long mstId : reservationMstIds) {
            specialRsvMstRepository.updateStatus(mstId, newStatus);
        }
    }
    
    /** 예약 완료처리 로직 */
    @Transactional
    public boolean completeReservationList(List<ReservationItemDto> dtoList ,String userId) {
        try {
        	for (ReservationItemDto dto : dtoList) {
        		completeReservation(dto , userId);
        	}
        	return true;
			
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
    }
    /** 예약 단건 완료 처리 로직 */
    @Transactional
    public boolean completeReservation(ReservationItemDto dto , String userId) {

    	try {
    		// 중복 제거된 reservation master ID 목록
        	Set<Long> rsvMstIdSet = dto.getRsvMstIdMap().values().stream()
        		    .filter(id -> id != null && id > 0) // 0 또는 null 제거
        		    .collect(Collectors.toSet());
        	
        	Set<Integer> qtySet = dto.getProductQtyMap().values().stream()
        		    .filter(id -> id != null && id > 0) // 0 또는 null 제거
        		    .collect(Collectors.toSet());
        		
        	/** 수량없는것 예약 취소 저장할 수량 없음. */
        	if(qtySet.size() == 0) {
        		throw new GlobalException(ResponseCode.RESERVATION_SAVE_ZERO);
        	}

            for (Long mstId : rsvMstIdSet) {

                SpecialRsvMstEntity mst = specialRsvMstRepository.findById(mstId)
                        .orElseThrow(() -> new IllegalArgumentException("예약 마스터 ID 없음: " + mstId));

                List<SpecialRsvDtlEntity> dtls1 = specialRsvDtlRepository.findBySpecialRsvMstId(mstId);
                List<SpecialRsvDtlEntity> dtls = dtls1.stream().filter(x-> x.getCancelYn().equals("N")).collect(Collectors.toList());
               
                if(dto.getReservationStatus().equals("예약") || dto.getReservationStatus().equals("RESERVATION")) {
                	
                	long specialId = mst.getSpecialId();
                	SpecialMstEntity specialMstEntity = specialMstRepository.findById(specialId)
        	                .orElseThrow(() -> new IllegalArgumentException("특가 정보가 존재하지 않습니다. ID=" + dto.getSpecialId()));
                	
                	// 1. 판매 마스터 생성
                    SalesMstEntity salesMst = SalesMstEntity.builder()
                            .storeId       (specialMstEntity.getStoreId())
                            .userId        (dto.getUserId())
                            .totalQty      (dtls.stream().mapToInt(SpecialRsvDtlEntity::getReservationCnt).sum())
                            .totalPrice    (dtls.stream().mapToInt(SpecialRsvDtlEntity::getReservationPrice).sum())
                            .salesStatus   (SalesStatus.COMPLETE)
                            .salesType     (SalesType.RESERVATION)
                            .paymentType   (PaymentType.CARD)
                            .salesDate     (LocalDateTime.now())
                            .createUser    (userId)
                            .build();

                    salesMstRepository.save(salesMst);
                    
                    int lineNo = 1;
                    
                    dto.getSpecialPriceMap().get("");

                    for (SpecialRsvDtlEntity dtl : dtls) {
                    	int specialPrice = dto.getSpecialPriceMap().get("PRODUCT_"+dtl.getSpecialDtlId()).intValue();
                    	if(dtl.getReservationCnt() <= 0) continue;
                    	
                    	// sales dtl 생성 => FIFO  
                    	// 남은 재고는 sales dtl에서 판매수량을 빼고 나오기 때문에 남은수량 처리안함.
                    	SalesDtlDto sDto = SalesDtlDto.builder()
    							            			.productId     (dtl.getSpecialDtl().getProductId())
    							            			.qty           (dtl.getReservationCnt())
    							            			.salesType     (SalesType.RESERVATION.getKey())//특가 판매시 default
    							            			.paymentType   (PaymentType.CARD.getKey())//특가 판매시 default
    							            			.unitPrice     (dtl.getSpecialDtl().getOrgSalesPrice()) //원래 판매 가격임
    							            			.orgSalesPrice (specialPrice) // 특가 판매가
    							            			.description   ("특가상품 예약 구매")
    							            			.build();
                	    salesService.processSale(sDto, salesMst, dto.getLocationId() , userId);
                    	
                        // 3. special_rsv_dtl 구매 수량 업데이트
                        dtl.setSalesCnt      (dtl.getReservationCnt());
                        dtl.setReservationCnt(dtl.getReservationCnt());
                        dtl.setSalesPrice    (dtl.getReservationPrice());
                        dtl.setUnitPrice     (specialPrice);
                        specialRsvDtlRepository.save(dtl);
                    }
                    
                    // 4. special_rsv_mst 상태, 금액, 판매 ID 갱신
                    mst.setReservationStatus(ReservationStatus.COMPLETE);//여기 확인
                    mst.setSalesId(salesMst.getSalesId());
                    mst.setSalesCnt(salesMst.getTotalQty());
                    mst.setSalesPrice(salesMst.getTotalPrice());
                    mst.setUpdateDate(LocalDateTime.now());
                    mst.setUpdateUser(userId);
                    specialRsvMstRepository.save(mst);
                }
            }
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return false;
		}
    	return true;
    }
    
    /* ============================================================
     *  통합 픽업 (특가 + 공동구매)  -- 2026-06-05 추가
     * ============================================================ */

    private static int toInt(Object o)  { return (o instanceof Number) ? ((Number) o).intValue()  : 0; }
    private static Long toLong(Object o){ return (o instanceof Number) ? ((Number) o).longValue() : 0L; }

    /** 현재 픽업 가능한 예약이 있는 고객 목록 조회 (type=SPECIAL: 특가 시작일~픽업종료일 / GROUPBUY: 픽업 시작일~픽업종료일) */
    @Transactional(readOnly = true)
    public List<PickupUserDto> getPickupUsers(String type, Long storeId, Long locationId) {
        boolean isGroupbuy = "GROUPBUY".equalsIgnoreCase(type);
        List<Map<String, Object>> rows = isGroupbuy
                ? groupbuyAdminMapper.selectPickupUsers(storeId, locationId)
                : specialMapper.selectPickupUsers(storeId, locationId);

        List<PickupUserDto> result = new ArrayList<>();
        for (Map<String, Object> r : rows) {
            result.add(PickupUserDto.builder()
                    .type(isGroupbuy ? "GROUPBUY" : "SPECIAL")
                    .userId((String) r.get("USER_ID"))                         // userId만 노출
                    .name(PiiMaskUtil.maskName((String) r.get("NAME")))        // 이름 마스킹
                    .phone(PiiMaskUtil.maskPhone((String) r.get("PHONE")))     // 전화 마스킹
                    .itemCnt(toInt(r.get("ITEM_CNT")))
                    .totalQty(toInt(r.get("TOTAL_QTY")))
                    .totalAmount(toInt(r.get("TOTAL_AMOUNT")))
                    .pickupStartDate((String) r.get("PICKUP_START_DATE"))
                    .pickupEndDate((String) r.get("PICKUP_END_DATE"))
                    .build());
        }
        return result;
    }

    /** 고객 단위 통합 픽업 항목(특가+공동구매) 조회 */
    @Transactional(readOnly = true)
    public List<UserPickupItemDto> getUserPickupItems(String userId, Long storeId, Long locationId) {
        List<UserPickupItemDto> result = new ArrayList<>();

        // --- 특가 예약: 상품(special_rsv_dtl)별 1행 (상품별 부분 픽업 지원) ---
        for (Map<String, Object> r : specialMapper.selectUserSpecialPickupItems(userId, storeId, locationId)) {
            int qty    = toInt(r.get("QTY"));
            int unit   = toInt(r.get("UNIT_PRICE"));
            int amount = toInt(r.get("AMOUNT"));
            result.add(UserPickupItemDto.builder()
                    .type("SPECIAL")
                    .refId(toLong(r.get("REF_ID")))
                    .rsvDtlId(toLong(r.get("RSV_DTL_ID")))
                    .bizId(toLong(r.get("BIZ_ID")))
                    .productId(toLong(r.get("PRODUCT_ID")))
                    .title((String) r.get("TITLE"))
                    .productNm((String) r.get("PRODUCT_NM"))
                    .productCnt(1)
                    .qty(qty)
                    .unitPrice(unit)
                    .amount(amount)
                    .pickupStartDate((String) r.get("PICKUP_START_DATE"))
                    .pickupEndDate((String) r.get("PICKUP_END_DATE"))
                    .pickupable(toInt(r.get("PICKUPABLE")) == 1)
                    .completed(toInt(r.get("COMPLETED")) == 1)
                    .expired(toInt(r.get("EXPIRED")) == 1)
                    .status((String) r.get("STATUS"))
                    .build());
        }

        // --- 공동구매 참여(JOIN) ---
        for (Map<String, Object> r : groupbuyAdminMapper.selectUserGroupbuyPickupItems(userId, storeId, locationId)) {
            int qty  = toInt(r.get("QTY"));
            int unit = toInt(r.get("UNIT_PRICE"));
            result.add(UserPickupItemDto.builder()
                    .type("GROUPBUY")
                    .refId(toLong(r.get("REF_ID")))
                    .bizId(toLong(r.get("BIZ_ID")))
                    .title((String) r.get("TITLE"))
                    .productNm((String) r.get("PRODUCT_NM"))
                    .productCnt(1)
                    .qty(qty)
                    .unitPrice(unit)
                    .amount(unit * qty)
                    .pickupStartDate((String) r.get("PICKUP_START_DATE"))
                    .pickupEndDate((String) r.get("PICKUP_END_DATE"))
                    .pickupable(toInt(r.get("PICKUPABLE")) == 1)
                    .completed(toInt(r.get("COMPLETED")) == 1)
                    .expired(toInt(r.get("EXPIRED")) == 1)
                    .status((String) r.get("STATUS"))
                    .build());
        }
        return result;
    }

    /** 통합 픽업 일괄 완료 (특가=상품별 부분 픽업 + 공동구매, 단일 트랜잭션) */
    @Transactional
    public boolean completePickupBatch(PickupBatchRequestDto req, String adminUser) {
        if (req.getSpecialRsvDtlIds() != null && !req.getSpecialRsvDtlIds().isEmpty()) {
            completeSpecialByDtlIds(req.getSpecialRsvDtlIds(), adminUser);
        }
        if (req.getGroupbuyJoinIds() != null) {
            for (Long joinId : req.getGroupbuyJoinIds()) {
                if (joinId != null && joinId > 0) groupbuyAdminService.completeGroupbuyPickup(joinId, adminUser);
            }
        }
        return true;
    }

    /**
     * 특가 예약 상품별(special_rsv_dtl) 부분 픽업 완료.
     * 선택된 상세만 매출확정하고, 같은 예약(mst)의 활성 상세가 모두 완료되면 mst=COMPLETE,
     * 일부만 완료되면 mst는 RESERVATION 유지(부분 픽업). 실패 시 예외 전파 → 전체 롤백.
     */
    @Transactional
    public void completeSpecialByDtlIds(List<Long> dtlIds, String userId) {
        List<SpecialRsvDtlEntity> sel = specialRsvDtlRepository.findAllById(dtlIds).stream()
                .filter(d -> "N".equals(d.getCancelYn()) && d.getReservationCnt() != null && d.getReservationCnt() > 0)
                .filter(d -> d.getSalesCnt() == null || d.getSalesCnt() < d.getReservationCnt())   // 이미 완료된 상세 제외
                .collect(Collectors.toList());
        if (sel.isEmpty()) {
            throw new GlobalException(ResponseCode.BAD_REQUEST, "픽업할 항목이 없습니다.");
        }

        Map<Long, List<SpecialRsvDtlEntity>> byMst = sel.stream()
                .collect(Collectors.groupingBy(SpecialRsvDtlEntity::getSpecialRsvMstId));

        for (Map.Entry<Long, List<SpecialRsvDtlEntity>> e : byMst.entrySet()) {
            Long mstId = e.getKey();
            List<SpecialRsvDtlEntity> dtls = e.getValue();

            SpecialRsvMstEntity mst = specialRsvMstRepository.findById(mstId)
                    .orElseThrow(() -> new GlobalException(ResponseCode.NOT_FOUND_OBJECT, "예약 마스터 ID 없음: " + mstId));
            if (mst.getReservationStatus() != ReservationStatus.RESERVATION) {
                throw new GlobalException(ResponseCode.BAD_REQUEST, "완료 처리할 수 없는 상태입니다. (mstId=" + mstId + ")");
            }
            SpecialMstEntity specialMstEntity = specialMstRepository.findById(mst.getSpecialId())
                    .orElseThrow(() -> new GlobalException(ResponseCode.NOT_FOUND_OBJECT, "특가 정보가 존재하지 않습니다. ID=" + mst.getSpecialId()));

            // 선택된 상세에 대해서만 매출 생성
            SalesMstEntity salesMst = SalesMstEntity.builder()
                    .storeId    (specialMstEntity.getStoreId())
                    .userId     (mst.getUserId())
                    .totalQty   (dtls.stream().mapToInt(SpecialRsvDtlEntity::getReservationCnt).sum())
                    .totalPrice (dtls.stream().mapToInt(SpecialRsvDtlEntity::getReservationPrice).sum())
                    .salesStatus(SalesStatus.COMPLETE)
                    .salesType  (SalesType.RESERVATION)
                    .paymentType(PaymentType.CARD)
                    .salesDate  (LocalDateTime.now())
                    .createUser (userId)
                    .build();
            salesMstRepository.save(salesMst);

            for (SpecialRsvDtlEntity dtl : dtls) {
                int specialPrice = dtl.getSpecialDtl().getSalesPrice();   // 특가 판매가
                SalesDtlDto sDto = SalesDtlDto.builder()
                        .productId    (dtl.getSpecialDtl().getProductId())
                        .qty          (dtl.getReservationCnt())
                        .salesType    (SalesType.RESERVATION.getKey())
                        .paymentType  (PaymentType.CARD.getKey())
                        .unitPrice    (dtl.getSpecialDtl().getOrgSalesPrice())  // 정상가
                        .orgSalesPrice(specialPrice)                            // 특가가
                        .description  ("특가상품 예약 구매(통합픽업)")
                        .build();
                salesService.processSale(sDto, salesMst, specialMstEntity.getLocationId(), userId);

                dtl.setSalesCnt  (dtl.getReservationCnt());
                dtl.setSalesPrice(dtl.getReservationPrice());
                dtl.setUnitPrice (specialPrice);
                specialRsvDtlRepository.save(dtl);
            }

            // 같은 예약의 활성 상세가 모두 완료되면 mst 완료 처리. 누적 판매수량/금액 갱신.
            List<SpecialRsvDtlEntity> activeDtls = specialRsvDtlRepository.findBySpecialRsvMstId(mstId).stream()
                    .filter(x -> "N".equals(x.getCancelYn()) && x.getReservationCnt() != null && x.getReservationCnt() > 0)
                    .collect(Collectors.toList());
            boolean allDone = activeDtls.stream()
                    .allMatch(x -> x.getSalesCnt() != null && x.getSalesCnt() >= x.getReservationCnt());
            int doneQty   = activeDtls.stream().mapToInt(x -> x.getSalesCnt()   == null ? 0 : x.getSalesCnt()).sum();
            int donePrice = activeDtls.stream().mapToInt(x -> x.getSalesPrice() == null ? 0 : x.getSalesPrice()).sum();

            if (allDone) {
                mst.setReservationStatus(ReservationStatus.COMPLETE);
            }
            mst.setSalesId   (salesMst.getSalesId());
            mst.setSalesCnt  (doneQty);
            mst.setSalesPrice(donePrice);
            mst.setUpdateDate(LocalDateTime.now());
            mst.setUpdateUser(userId);
            specialRsvMstRepository.save(mst);
        }
    }

    /** 특가 예약 단건 완료 처리 (mstId 기준, 실패 시 예외 전파 → 롤백) */
    @Transactional
    public void completeReservationByMstId(Long mstId, String userId) {
        SpecialRsvMstEntity mst = specialRsvMstRepository.findById(mstId)
                .orElseThrow(() -> new GlobalException(ResponseCode.NOT_FOUND_OBJECT, "예약 마스터 ID 없음: " + mstId));

        if (mst.getReservationStatus() == ReservationStatus.COMPLETE) {
            throw new GlobalException(ResponseCode.BAD_REQUEST, "이미 완료된 예약입니다. (mstId=" + mstId + ")");
        }
        if (mst.getReservationStatus() != ReservationStatus.RESERVATION) {
            throw new GlobalException(ResponseCode.BAD_REQUEST, "완료 처리할 수 없는 상태입니다. (mstId=" + mstId + ")");
        }

        SpecialMstEntity specialMstEntity = specialMstRepository.findById(mst.getSpecialId())
                .orElseThrow(() -> new GlobalException(ResponseCode.NOT_FOUND_OBJECT, "특가 정보가 존재하지 않습니다. ID=" + mst.getSpecialId()));

        List<SpecialRsvDtlEntity> dtls = specialRsvDtlRepository.findBySpecialRsvMstId(mstId).stream()
                .filter(x -> "N".equals(x.getCancelYn()) && x.getReservationCnt() != null && x.getReservationCnt() > 0)
                .collect(Collectors.toList());

        if (dtls.isEmpty()) {
            throw new GlobalException(ResponseCode.BAD_REQUEST, "픽업할 수량이 없습니다. (mstId=" + mstId + ")");
        }

        SalesMstEntity salesMst = SalesMstEntity.builder()
                .storeId    (specialMstEntity.getStoreId())
                .userId     (mst.getUserId())
                .totalQty   (dtls.stream().mapToInt(SpecialRsvDtlEntity::getReservationCnt).sum())
                .totalPrice (dtls.stream().mapToInt(SpecialRsvDtlEntity::getReservationPrice).sum())
                .salesStatus(SalesStatus.COMPLETE)
                .salesType  (SalesType.RESERVATION)
                .paymentType(PaymentType.CARD)
                .salesDate  (LocalDateTime.now())
                .createUser (userId)
                .build();
        salesMstRepository.save(salesMst);

        for (SpecialRsvDtlEntity dtl : dtls) {
            int specialPrice = dtl.getSpecialDtl().getSalesPrice();   // 특가 판매가
            SalesDtlDto sDto = SalesDtlDto.builder()
                    .productId    (dtl.getSpecialDtl().getProductId())
                    .qty          (dtl.getReservationCnt())
                    .salesType    (SalesType.RESERVATION.getKey())
                    .paymentType  (PaymentType.CARD.getKey())
                    .unitPrice    (dtl.getSpecialDtl().getOrgSalesPrice())  // 정상가
                    .orgSalesPrice(specialPrice)                            // 특가가
                    .description  ("특가상품 예약 구매(통합픽업)")
                    .build();
            salesService.processSale(sDto, salesMst, specialMstEntity.getLocationId(), userId);

            dtl.setSalesCnt      (dtl.getReservationCnt());
            dtl.setReservationCnt(dtl.getReservationCnt());
            dtl.setSalesPrice    (dtl.getReservationPrice());
            dtl.setUnitPrice     (specialPrice);
            specialRsvDtlRepository.save(dtl);
        }

        mst.setReservationStatus(ReservationStatus.COMPLETE);
        mst.setSalesId   (salesMst.getSalesId());
        mst.setSalesCnt  (salesMst.getTotalQty());
        mst.setSalesPrice(salesMst.getTotalPrice());
        mst.setUpdateDate(LocalDateTime.now());
        mst.setUpdateUser(userId);
        specialRsvMstRepository.save(mst);
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
            
            
//            System.out.println("신규회원 토큰 정보 start==============================================");
//            System.out.println("userId======" + userId);
//            System.out.println("email=======" + email);
//            System.out.println("user_name===" + name);
//            System.out.println("phone=======" + phone);
//            System.out.println("gender======" + gender);
//            System.out.println("address=====" + address);
//            System.out.println("age_range===" + age_range);
//            System.out.println("신규회원 토큰 정보 start==============================================");

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
    
    

    
    
    
    
}
