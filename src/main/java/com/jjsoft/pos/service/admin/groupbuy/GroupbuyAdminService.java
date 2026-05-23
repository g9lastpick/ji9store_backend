package com.jjsoft.pos.service.admin.groupbuy;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jjsoft.pos.dto.groupbuy.GroupbuyDetailResponseDto;
import com.jjsoft.pos.dto.groupbuy.GroupbuyPriceStepDto;
import com.jjsoft.pos.dto.groupbuy.GroupbuyRequestDto;
import com.jjsoft.pos.dto.groupbuy.GroupbuyResponseDto;
import com.jjsoft.pos.dto.groupbuy.GroupbuySearchRequestDto;
import com.jjsoft.pos.entity.GroupbuyJoinMstEntity;
import com.jjsoft.pos.entity.GroupbuyMstEntity;
import com.jjsoft.pos.entity.GroupbuyPriceStepEntity;
import com.jjsoft.pos.enums.GroupbuyJoinStatus;
import com.jjsoft.pos.enums.GroupbuyStatus;
import com.jjsoft.pos.enums.ResponseCode;
import com.jjsoft.pos.exception.GlobalException;
import com.jjsoft.pos.mapper.GroupbuyAdminMapper;
import com.jjsoft.pos.repository.GroupbuyJoinMstRepository;
import com.jjsoft.pos.repository.GroupbuyMstRepository;
import com.jjsoft.pos.repository.GroupbuyPriceStepRepository;
import com.jjsoft.pos.service.admin.pos.PosPaymentService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 * 공동구매 관리자 Service
 */
//@Service
@RequiredArgsConstructor
@Log4j2
@Transactional
public class GroupbuyAdminService {

//    private final GroupbuyMstRepository     groupbuyMstRepository;
//    private final GroupbuyJoinMstRepository groupbuyJoinMstRepository;
//    private final GroupbuyPriceStepRepository groupbuyPriceStepRepository;
//
//    /** 복잡 조회 전용 */
//    private final GroupbuyAdminMapper       groupbuyAdminMapper;
//
//    /** POS 결제 연동 */
//    private final PosPaymentService         posPaymentService;

    
    
    /* =========================================================
     * 공동구매 목록 조회 
     * ========================================================= */
    @Transactional(readOnly = true)
    public List<GroupbuyResponseDto> getGroupbuyList(GroupbuySearchRequestDto requestDto) {
//        return groupbuyAdminMapper.selectGroupbuyList(requestDto);
    	return null;
    }
    
    /* =========================================================
     * 공동구매 상세 조회
     * ========================================================= */
    @Transactional(readOnly = true)
    public GroupbuyDetailResponseDto getGroupbuyDetail(Long groupbuyId) {
//        return groupbuyAdminMapper.selectGroupbuyDetail(groupbuyId);
    	return null;
    }
    
    
    
    
    /* =========================================================
     * 공동구매 등록
     * ========================================================= */
    @Transactional
    public Long createGroupbuy(GroupbuyRequestDto dto) {
    	//공동구매 가격 정책 검증 
//    	validatePriceSteps(dto.getPriceList(), dto.getTargetQty());
//    	
//        GroupbuyMstEntity entity = GroupbuyMstEntity.builder()
//                .storeId      (dto.getStoreId())
//                .locationId   (dto.getLocationId())
//                .productId    (dto.getProductId())
//                .groupbuyNm   (dto.getGroupbuyNm())
//                .groupbuyType (dto.getGroupbuyType())
//                .targetQty    (dto.getTargetQty())
//                .targetAmount (dto.getTargetAmount())
//                .limitQty       (dto.getLimitQty())
//                .currentQty   (0)
//                .currentAmount(0)
//                .startDate    (dto.getStartDate())
//                .endDate      (dto.getEndDate())
//                .pickupStartDate (dto.getPickupStartDate())
//                .pickupEndDate   (dto.getPickupEndDate())
//                .payType      (dto.getPayType())
//                .description  (dto.getDescription())
//                .status       (GroupbuyStatus.READY)
//                .createUser   (dto.getCreateUser())
//                .build();
//
//        groupbuyMstRepository.save(entity);
//        
//        /** 가격 정책 저장 */
//        for (GroupbuyPriceStepDto priceDto : dto.getPriceList()) {
//
//            GroupbuyPriceStepEntity stepEntity = GroupbuyPriceStepEntity.builder()
//                    .groupbuyId  (entity.getGroupbuyId())
//                    .stepQtyFrom (priceDto.getStepQtyFrom())
//                    .stepQtyTo   (priceDto.getStepQtyTo())
//                    .salesPrice (priceDto.getSalesPrice())
//                    .salesRate  (priceDto.getSalesRate())
//                    .createUser (dto.getCreateUser())
//                    .build();
//
//            groupbuyPriceStepRepository.save(stepEntity);
//        }
//
//        log.info("[GROUPBUY][CREATE] id={}", entity.getGroupbuyId());
//
//        return entity.getGroupbuyId();
    	return null;
    }

    /* =========================================================
     * 공동구매 수정 : READY 상태에서만 가능
     * ========================================================= */
    @Transactional
    public void updateGroupbuy(Long groupbuyId, GroupbuyRequestDto dto) {
    	
//        GroupbuyMstEntity entity = groupbuyMstRepository.findById(groupbuyId)
//                .orElseThrow(() -> new GlobalException(ResponseCode.NOT_FOUND_OBJECT, "공동구매가 존재하지 않습니다."));
//
//        if (entity.getStatus() != GroupbuyStatus.READY) {
//            log.info("[GROUPBUY][UPDATE][DENY] groupbuyId={} status={}",
//                     groupbuyId, entity.getStatus());
//            return;
//        }
//        
//        //공동구매 가격 정책 검증
//    	validatePriceSteps(dto.getPriceList(), dto.getTargetQty());
//
//        /** 마스터 정보 수정 */
//        entity.setStoreId        (dto.getStoreId());
//        entity.setLocationId     (dto.getLocationId());
//        entity.setProductId      (dto.getProductId());
//        entity.setGroupbuyNm     (dto.getGroupbuyNm());
//        entity.setGroupbuyType   (dto.getGroupbuyType());
//        entity.setTargetQty      (dto.getTargetQty());
//        entity.setTargetAmount   (dto.getTargetAmount());
//        entity.setLimitQty         (dto.getLimitQty());  
//        entity.setStartDate      (dto.getStartDate());
//        entity.setEndDate        (dto.getEndDate());
//        entity.setPickupStartDate(dto.getPickupStartDate());
//        entity.setPickupEndDate  (dto.getPickupEndDate());
//        entity.setPayType        (dto.getPayType());
//        entity.setStatus         (dto.getStatus());
//        entity.setDescription    (dto.getDescription());
//        entity.setUpdateUser     (dto.getUpdateUser());
//        
//        /** 기존 가격 정책 전체 삭제 */
//        groupbuyPriceStepRepository.deleteByGroupbuyId(groupbuyId);
//        groupbuyPriceStepRepository.flush();//즉시반영
//
//        /** 가격 정책 재등록 */
//        for (GroupbuyPriceStepDto priceDto : dto.getPriceList()) {
//
//            GroupbuyPriceStepEntity stepEntity = GroupbuyPriceStepEntity.builder()
//                    .groupbuyId  (groupbuyId)
//                    .stepQtyFrom (priceDto.getStepQtyFrom())
//                    .stepQtyTo   (priceDto.getStepQtyTo())
//                    .salesPrice (priceDto.getSalesPrice())
//                    .salesRate  (priceDto.getSalesRate())
//                    .createUser (dto.getUpdateUser())
//                    .build();
//
//            groupbuyPriceStepRepository.save(stepEntity);
//        }
//
//        log.info("[GROUPBUY][UPDATE] id={}", groupbuyId);
    	
    }

    /* =========================================================
     * 공동구매 삭제 (CANCEL 처리) - 삭제는 status를 cancel로 변경 : 
     *   참여자 있는경우 변경 불가 , 
     *   이미 종료 취소 된경우 변경 불가 
     *   가격 정책은 테이블에서 실재 삭제 
     * ========================================================= */
    @Transactional
    public void cancelGroupbuy(Long groupbuyId) {

//        GroupbuyMstEntity entity = groupbuyMstRepository.findById(groupbuyId)
//                .orElseThrow(() -> new GlobalException(ResponseCode.NOT_FOUND_OBJECT, "공동구매가 존재하지 않습니다."));
//
//        // 이미 종료/취소된 경우 방어
//        if (entity.getStatus() == GroupbuyStatus.CANCEL
//            || entity.getStatus() == GroupbuyStatus.END) {
//
//            log.info("[GROUPBUY][CANCEL][SKIP] id={} status={}",
//                     groupbuyId, entity.getStatus());
//            return;
//        }
//        
//        if (groupbuyJoinMstRepository.existsByGroupbuyId(groupbuyId)) {
//        	
//            throw new GlobalException(ResponseCode.BAD_REQUEST, "참여자가 있는 공동구매는 취소할 수 없습니다.");
//        }
//
//        /** 상태 변경 */
//        entity.setStatus(GroupbuyStatus.CANCEL);
//
//        /** 가격 정책 삭제 */
//        groupbuyPriceStepRepository.deleteByGroupbuyId(groupbuyId);
//
//        log.info("[GROUPBUY][CANCEL] id={}", groupbuyId);
    }
    
    
    
    
    /**
     * 공동구매 가격 정책 검증
     */
    private void validatePriceSteps(List<GroupbuyPriceStepDto> priceList, Integer targetQty) {

    	
//        if (priceList == null || priceList.isEmpty()) {
//            throw new GlobalException(ResponseCode.BAD_REQUEST, "가격 정책은 최소 1개 이상 필요합니다.");
//        }
//
//        // 1. stepQtyFrom 기준 정렬
//        List<GroupbuyPriceStepDto> sorted =
//                priceList.stream()
//                         .sorted(Comparator.comparing(GroupbuyPriceStepDto::getStepQtyFrom))
//                         .toList();
//
//        Integer prevTo = null;
//
//        for (GroupbuyPriceStepDto step : sorted) {
//
//            Integer from = step.getStepQtyFrom();
//            Integer to   = step.getStepQtyTo();
//
//            // 2. 기본 값 검증
//            if (from == null || to == null) {
//            	
//                throw new GlobalException(ResponseCode.BAD_REQUEST, "수량 구간은 필수입니다.");
//            }
//
//            if (from <= 0 || to <= 0) {
//            	
//                throw new GlobalException(ResponseCode.BAD_REQUEST, "수량 구간은 1 이상이어야 합니다.");
//            }
//
//            if (from > to) {
//            	
//                throw new GlobalException(ResponseCode.BAD_REQUEST, "수량 시작은 종료보다 클 수 없습니다.");
//            }
//
//            // 3. 이전 구간과 겹침 검사
//            if (prevTo != null) {
//                if (from <= prevTo) {
//                	
//                    throw new GlobalException(ResponseCode.BAD_REQUEST
//                    						, String.format("가격 정책 수량 구간이 겹칩니다. 이전 종료=%d, 현재 시작=%d",prevTo, from));
//                }
//            }
//
//            // 4. targetQty 범위 초과 여부
//            if (targetQty != null && to > targetQty) {
//            	
//                throw new GlobalException(ResponseCode.BAD_REQUEST
//						, String.format("가격 정책 종료 수량(%d)이 목표 수량(%d)을 초과합니다.",to, targetQty));
//            }
//
//            prevTo = to;
//        }
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    /*****************************************************************************
     * 공동구매 참여 로직 start
     * 모바일 constoller 쪽에서도 해당 함수 호출하여 admin과 user가 동시에 사용 할 수 있는 참여 및 수정 취소 시스템
     * 참여 후 변경 수량이 0 이면 참여 취소로 판단함.
     * ****************************************************************************
     * ****************************************************************************
     * ****************************************************************************
     * */
    @Transactional
    public void enterGroupbuy( Long groupbuyId, String userId, int joinQty ,GroupbuyJoinStatus requestStatus) {
    	
    	 
        
//        /* 1. 기존 예약 조회 (JOIN / PAYED 모두 조회) */
//        Optional<GroupbuyJoinMstEntity> optionalJoin = 
//        		groupbuyJoinMstRepository.findByGroupbuyIdAndUserId(groupbuyId, userId);
//
//        /* 2. 취소 요청 */
//        if (requestStatus == GroupbuyJoinStatus.CANCEL) {
//
//            if (optionalJoin.isEmpty()) {
//            	
//                throw new GlobalException(ResponseCode.NOT_FOUND_OBJECT , "취소할 예약이 없습니다.");
//            }
//
//            handleCancelJoin(groupbuyId, optionalJoin.get());
//            return;
//        }
//
//        /* 3. 참여 / 수량 변경 */
//        if (optionalJoin.isPresent()) {
//            handleExistingJoin(groupbuyId, optionalJoin.get(), joinQty);
//        } else {
//            handleNewJoin(groupbuyId, userId, joinQty);
//        }

    }
    /** 공동구매 신규 참여 */
    private void handleNewJoin(Long groupbuyId, String userId, int joinQty) {
    	
//    	if (joinQty <= 0) {
//    		
//            throw new GlobalException(ResponseCode.BAD_REQUEST , "참여 수량은 1 이상이어야 합니다.");
//        }
//
//        validateGroupbuyJoin(groupbuyId, joinQty);
//
//        GroupbuyJoinMstEntity join = GroupbuyJoinMstEntity.builder()
//                .groupbuyId(groupbuyId)
//                .userId    (userId)
//                .joinStatus(GroupbuyJoinStatus.JOIN)
//                .totalQty  (joinQty)
//                .build();
//
//        groupbuyJoinMstRepository.save(join);
//
//        applyGroupbuyJoinResult(groupbuyId, joinQty);
        
    }
    
    /** 참여자 수정 */
    private void handleExistingJoin( Long groupbuyId, GroupbuyJoinMstEntity existingJoin, int newQty ) {
    	
//    	/* 결제 완료 건은 변경 불가 */
//        if (existingJoin.getJoinStatus() == GroupbuyJoinStatus.PAYED) {
//        	
//            throw new GlobalException(ResponseCode.BAD_REQUEST , "결제 완료된 예약은 변경할 수 없습니다.");
//        }
//
//        int oldQty = existingJoin.getTotalQty();
//        int diffQty = newQty - oldQty;
//
//        /* 수량 0 → 취소 */
//        if (newQty == 0) {
//            handleCancelJoin(groupbuyId, existingJoin);
//            return;
//        }
//
//        /* 수량 변경 */
//        if (diffQty != 0) {
//
//            if (diffQty > 0) {
//                validateGroupbuyJoin(groupbuyId, diffQty);
//                applyGroupbuyJoinResult(groupbuyId, diffQty);
//            } else {
//                applyGroupbuyCancelResult(groupbuyId, Math.abs(diffQty));
//            }
//
//            existingJoin.setTotalQty(newQty);
//        }
//        existingJoin.setJoinStatus(GroupbuyJoinStatus.JOIN);
//        groupbuyJoinMstRepository.save(existingJoin);
    }
    
    /** 공동구매 참여 취소 */
    private void handleCancelJoin( Long groupbuyId, GroupbuyJoinMstEntity existingJoin ) {

//    	/* 공동구매 상태 검증 (취소 가능 여부) */
//        GroupbuyMstEntity groupbuy =
//            groupbuyMstRepository.findById(groupbuyId)
//                .orElseThrow(() ->
//                    new GlobalException(ResponseCode.NOT_FOUND_OBJECT, "공동구매 정보가 없습니다.")
//                );
//    	
//        if (groupbuy.getStatus() != GroupbuyStatus.START) {
//            throw new GlobalException(ResponseCode.BAD_REQUEST, "취소 가능한 상태가 아닙니다.");
//        }
//        
//        /* 결제 완료 건은 취소 불가 */
//        if (existingJoin.getJoinStatus() == GroupbuyJoinStatus.PAYED) {
//        	
//            throw new GlobalException(ResponseCode.BAD_REQUEST , "결제 완료된 예약은 취소할 수 없습니다.");
//        }
//
//        int cancelQty = existingJoin.getTotalQty();
//
//        /* 이미 취소된 경우 */
//        if (existingJoin.getJoinStatus() == GroupbuyJoinStatus.CANCEL) {
//            return;
//        }
//
//        /* 상태 변경 */
//        existingJoin.setJoinStatus(GroupbuyJoinStatus.CANCEL);
//        existingJoin.setTotalQty(0);
//        groupbuyJoinMstRepository.save(existingJoin);
//
//        /* 수량 차감 */
//        applyGroupbuyCancelResult(groupbuyId, cancelQty);
    }

    private void applyGroupbuyCancelResult(Long groupbuyId, int cancelQty) {

//        int updated = groupbuyAdminMapper .decreaseCurrentQty(groupbuyId, cancelQty);
//
//        if (updated == 0) {
//        	log.warn("[GROUPBUY][CANCEL][FAIL] groupbuyId={} cancelQty={}",
//                    groupbuyId, cancelQty);
//            throw new GlobalException(ResponseCode.BAD_REQUEST , "공동구매 수량 차감 실패");
//        }
    }

    
    
    private void validateGroupbuyJoin(Long groupbuyId, int joinQty) {

//        GroupbuyMstEntity groupbuy = groupbuyMstRepository.findById(groupbuyId).orElseThrow(() -> new IllegalStateException("공동구매 정보 없음"));
//
//        /* 상태 체크 */
//        if (groupbuy.getStatus() != GroupbuyStatus.START) {
//        	
//            throw new GlobalException(ResponseCode.BAD_REQUEST , "참여 가능한 상태가 아닙니다.");
//        }
//        /* 기간 체크 */
//        LocalDateTime now = LocalDateTime.now();
//        if (now.isBefore(groupbuy.getStartDate()) || now.isAfter(groupbuy.getEndDate())) {
//        	
//            throw new GlobalException(ResponseCode.BAD_REQUEST , "공동구매 기간이 아닙니다.");
//        }
//        /* 수량 초과 방지 */
//        if (groupbuy.getCurrentQty() + joinQty > groupbuy.getTargetQty()) {
//        	
//            throw new GlobalException(ResponseCode.BAD_REQUEST , "남은 수량을 초과했습니다.");
//        }
    }
    
    /**
     * 공동구매 참여 결과 반영 (수량/금액 누적)
     */
    private void applyGroupbuyJoinResult(Long groupbuyId, int joinQty) {

        /* 동시성 고려한 수량 증가 */
//        int updated = groupbuyAdminMapper.increaseCurrentQty(groupbuyId, joinQty);
//
//        if (updated == 0) {
//        	
//            throw new GlobalException(ResponseCode.BAD_REQUEST , "동시 참여로 인해 공동구매 수량이 초과되었습니다.");
//        }
    }

    

    

    
    
    
    
    
    
}
