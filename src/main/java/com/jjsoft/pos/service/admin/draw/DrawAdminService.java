package com.jjsoft.pos.service.admin.draw;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jjsoft.pos.dto.draw.DrawDetailResponseDto;
import com.jjsoft.pos.dto.draw.DrawRequestDto;
import com.jjsoft.pos.dto.draw.DrawResponseDto;
import com.jjsoft.pos.dto.draw.DrawSearchRequestDto;
import com.jjsoft.pos.entity.DrawEntryMstEntity;
import com.jjsoft.pos.entity.DrawEntryTicketEntity;
import com.jjsoft.pos.entity.DrawMstEntity;
import com.jjsoft.pos.enums.DrawEntryStatus;
import com.jjsoft.pos.enums.DrawStatus;
import com.jjsoft.pos.enums.ResponseCode;
import com.jjsoft.pos.exception.GlobalException;
import com.jjsoft.pos.mapper.DrawAdminMapper;
import com.jjsoft.pos.repository.DrawEntryMstRepository;
import com.jjsoft.pos.repository.DrawEntryTicketRepository;
import com.jjsoft.pos.repository.DrawMstRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 * 드로우 관리자 Service
 */
//@Service
@RequiredArgsConstructor
@Log4j2
@Transactional
public class DrawAdminService {

//    private final DrawMstRepository          drawMstRepository;
//    private final DrawEntryMstRepository     drawEntryMstRepository;
//    private final DrawEntryTicketRepository  drawEntryTicketRepository;
//    /** 복잡 조회 전용 */
//    private final DrawAdminMapper       drawAdminMapper;

    
    
    /** =========================================================
     * Draw 목록 조회 
     * ========================================================= */
    @Transactional(readOnly = true)
    public List<DrawResponseDto> getDrawList(DrawSearchRequestDto requestDto) {
//        return drawAdminMapper.selectDrawList(requestDto);
    	return null;
    }
    
    /** =========================================================
     * Draw 상세 조회
     * ========================================================= */
    @Transactional(readOnly = true)
    public DrawDetailResponseDto getDrawDDetail(Long drawId) {
//        return drawAdminMapper.selectDrawDetail(drawId);
    	return null;
    }
    
    
    /** 드로우 저장 */
    @Transactional
    public Long createDraw(DrawRequestDto dto) {

//        DrawMstEntity entity = DrawMstEntity.builder()
//                .storeId        (dto.getStoreId())
//                .locationId     (dto.getLocationId())
//                .drawNm         (dto.getDrawNm())
//                .drawUrl        (dto.getDrawUrl())
//                .productId      (dto.getProductId())
//                .entryStartDate (dto.getEntryStartDate())
//                .entryEndDate   (dto.getEntryEndDate())
//                .drawDate       (dto.getDrawDate())
//                .pickupStartDate(dto.getPickupStartDate())
//                .pickupEndDate  (dto.getPickupEndDate())
//                .winnerCnt      (dto.getWinnerCnt())
//                .totalQty       (dto.getTotalQty())// ㅇ이벤트 상품 겟수
//                .currentQty     (dto.getCurrentQty()) // 최초 = 전체 수량
//                .limitQty       (dto.getLimitQty())
//                .salesPrice     (dto.getSalesPrice())
//                .status         (dto.getStatus())
//                .description    (dto.getDescription())
//                .build();
//
//        drawMstRepository.save(entity);
//        return entity.getDrawId();
    	return null;
    }

    /** 드로우 수정 */
    @Transactional
    public void updateDraw(Long drawId, DrawRequestDto dto) {

//        DrawMstEntity entity = drawMstRepository.findById(drawId)
//                .orElseThrow(() -> new GlobalException(ResponseCode.NOT_FOUND_OBJECT, "드로우 정보가 없습니다."));
//
//        entity.setStoreId        (dto.getStoreId());
//        entity.setLocationId     (dto.getLocationId());
//        entity.setDrawNm         (dto.getDrawNm());
//        entity.setDrawUrl        (dto.getDrawUrl());
//        entity.setProductId      (dto.getProductId());
//        entity.setEntryStartDate (dto.getEntryStartDate());
//        entity.setEntryEndDate   (dto.getEntryEndDate());
//        entity.setDrawDate       (dto.getDrawDate());
//        entity.setPickupStartDate(dto.getPickupStartDate());
//        entity.setPickupEndDate  (dto.getPickupEndDate());
//        entity.setWinnerCnt      (dto.getWinnerCnt());
//        entity.setTotalQty       (dto.getTotalQty());
//        entity.setCurrentQty     (dto.getCurrentQty());
//        entity.setLimitQty       (dto.getLimitQty());
//        entity.setSalesPrice     (dto.getSalesPrice());
//        entity.setStatus         (dto.getStatus());
//        entity.setDescription    (dto.getDescription());
    }

    /** 드로우 취소 */
    @Transactional
    public void cancelDraw(Long drawId) {
    	/* ready 상태일경우 cancel 가능 
    	 * 프로세스적으로 협의해야함.  */
//        DrawMstEntity entity = drawMstRepository.findById(drawId)
//                .orElseThrow(() -> new GlobalException(ResponseCode.NOT_FOUND_OBJECT, "드로우 정보가 없습니다."));
//
//        entity.setStatus(DrawStatus.CANCEL);
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    /*****************************************************************************
     * 드로우 참여 / 취소 공통 로직
     * 모바일 constoller 쪽에서도 해당 함수 호출하여 admin과 user가 동시에 사용 할 수 있는 참여 및 수정 취소 시스템
     * ****************************************************************************
     * ****************************************************************************
     * ****************************************************************************
     * */
    @Transactional
    public void enterDraw(Long drawId , String userId , DrawEntryStatus requestStatus) {

//    	/* 1. 기존 참여 이력 조회 (ENTRY / CANCEL 모두) */
//        Optional<DrawEntryMstEntity> optionalEntry =
//                drawEntryMstRepository.findByDrawIdAndUserId(drawId, userId);
//    	
//    	
//        /* 2. 취소 요청 */
//        if (requestStatus == DrawEntryStatus.CANCEL) {
//
//            if (optionalEntry.isEmpty()) {
//                throw new GlobalException(ResponseCode.NOT_FOUND_OBJECT, "취소할 드로우 참여 이력이 없습니다." );
//            }
//
//            handleCancelDrawEntry(optionalEntry.get());
//            return;
//        }
//        
//        
//        /* 3. 참여 요청 (ENTRY) */
//
//        /* 3-1. 이미 참여 이력이 있는 경우 */
//        if (optionalEntry.isPresent()) {
//
//            DrawEntryMstEntity entry = optionalEntry.get();
//
//            if (entry.getEntryStatus() == DrawEntryStatus.ENTRY) {
//                throw new GlobalException( ResponseCode.BAD_REQUEST, "이미 참여한 드로우입니다." );
//            }
//            /* 취소 후 재참여시  */
//            validateDrawEntry(userId, drawId);
//            entry.setEntryStatus(DrawEntryStatus.ENTRY);
//            entry.setUpdateDate(LocalDateTime.now());
//            drawEntryMstRepository.save(entry);
//            
//            /* 가중치(티켓) 재생성 */
//            createEntryTickets(entry);
//            return;
//        }
//        
//        /* 1. 참여 가능 여부 검증 */
//        validateDrawEntry(userId, drawId);
//
//        /* 2. 참여자 정보 저장 */
//        DrawEntryMstEntity entry = DrawEntryMstEntity.builder()
//                .drawId(drawId)
//                .userId(userId)
//                .entryStatus(DrawEntryStatus.ENTRY)
//                .build();
//
//        drawEntryMstRepository.save(entry);
//
//        /* 3. 가중치(티켓) 생성 */
//        createEntryTickets(entry);
    }
    
    /**
     * 드로우 참여 취소 처리
     */
    private void handleCancelDrawEntry(DrawEntryMstEntity entry) {

//        if (entry.getEntryStatus() == DrawEntryStatus.CANCEL) {
//            throw new GlobalException( ResponseCode.BAD_REQUEST, "이미 취소된 드로우입니다." );
//        }
//
//        entry.setEntryStatus(DrawEntryStatus.CANCEL);
//        entry.setUpdateDate(LocalDateTime.now());
//
//        drawEntryMstRepository.save(entry);
//
//        /* 가중치(티켓) 무효 처리 */
//        invalidateEntryTickets(entry);
    }
    
    /**
     * 드로우 티켓 무효 처리
     */
    private void invalidateEntryTickets(DrawEntryMstEntity entry) {

//        drawEntryTicketRepository.deleteByDrawEntryId(entry.getDrawEntryId());
    }

    /** 참여자 티켓 생성 기본 2개 - 가중치 계산로직 넣어야함. */
    private void createEntryTickets(DrawEntryMstEntity entry) {

//        /* 기본 가중치 (조건 계산은 나중에 여기서 추가) */
//        int defaultCnt = 2;
//
//        /* 현재 드로우의 마지막 티켓 번호 조회 (락 포함) */
//        Integer maxTicketNo = drawAdminMapper .selectMaxTicketNoForUpdate(entry.getDrawId());
//
//        int startTicketNo = (maxTicketNo == null ? 1 : maxTicketNo + 1);
//
//        /* 티켓 생성 */
//        for (int i = 0; i < defaultCnt; i++) {
//
//            DrawEntryTicketEntity ticket = DrawEntryTicketEntity.builder()
//                    .drawId(entry.getDrawId())
//                    .drawEntryId(entry.getDrawEntryId())
//                    .ticketNo(startTicketNo + i)
//                    .build();
//
//            drawEntryTicketRepository.save(ticket);
//        }
    }
    
    /** 참여 여부 확인 */
    private void validateDrawEntry(String userId, Long drawId) {

//        DrawMstEntity draw = drawMstRepository.findById(drawId)
//                .orElseThrow(() -> new GlobalException( ResponseCode.NOT_FOUND, "존재하지 않는 드로우입니다." ));
//
//        /* 드로우 상태 확인 */
//        if (!DrawStatus.ENTRY.equals(draw.getStatus())) {
//            throw new GlobalException( ResponseCode.BAD_REQUEST, "현재 참여할 수 없는 드로우 상태입니다." );
//        }
//
//        /* 참여 시간 체크 */
//        LocalDateTime now = LocalDateTime.now();
//
//        if (now.isBefore(draw.getEntryStartDate()) || now.isAfter(draw.getEntryEndDate())) {
//            throw new GlobalException( ResponseCode.BAD_REQUEST, "드로우 참여 가능 시간이 아닙니다." );
//        }
    }
    
    
    
    
    
    
    
    
    /** 추첨
     * 설정한 시간이 되면 추첨 로직 실행.
     * 배치에서 실행 */
    @Transactional
    public void drawWinners(Long drawId) {
    	
        /*  드로우 정보 조회 */
//        DrawMstEntity draw = drawMstRepository.findById(drawId).orElse(null);
//
//        if (draw == null || draw.getStatus() != DrawStatus.DRAW) {
//            log.warn("[BATCH][DRAW][SKIP] drawId={}", drawId);
//            return;
//        }
//
//        int winnerCnt = draw.getWinnerCnt();
//        
//        if (winnerCnt <= 0) {
//            log.warn("[BATCH][DRAW][NO-WINNER-CNT] drawId={}", drawId);
//            drawAdminMapper.updateLoseEntries(drawId);
//            return;
//        }
//
//        /*  랜덤 티켓 추첨 */
//        List<Integer> ticketNos = drawAdminMapper.selectRandomWinnerTickets(drawId, winnerCnt);
//
//        if (ticketNos.isEmpty()) {
//        	
//        	log.warn("[BATCH][DRAW][NO-TICKET] drawId={}", drawId);
//            return;
//        }
//
//        /*  티켓 → 참여자 매핑 draw enter id list */
//        List<Long> winnerEntryIds = drawAdminMapper.selectWinnerEntryIdsByTicketNos(drawId, ticketNos);
//        
//        if (!winnerEntryIds.isEmpty()) {
//        	/*  당첨자 상태 업데이트 */
//            drawAdminMapper.updateEntryStatus( drawId, winnerEntryIds );//쿼리에서 WIN 넣음
//        }
//
//        /*  탈락자 처리 */
//        drawAdminMapper.updateLoseEntries(drawId);
    }
}
