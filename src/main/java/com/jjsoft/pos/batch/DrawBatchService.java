package com.jjsoft.pos.batch;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jjsoft.pos.mapper.DrawAdminMapper;
import com.jjsoft.pos.service.admin.draw.DrawAdminService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
//@Service
@RequiredArgsConstructor
public class DrawBatchService {

	private final DrawAdminService drawAdminService;
	private final DrawAdminMapper drawAdminMapper;
	
	/** 드로우 추첨 및 완료 처리 */
	@Transactional
	public void processEndedDraws() {
	    /* 1. ENTRY 상태 + 시간 지난 드로우 조회 */
	    List<Long> drawIds = drawAdminMapper.selectDrawIdsToDraw();

	    for (Long drawId : drawIds) {
	        try {
	        	int updated = drawAdminMapper.updateDrawStatusToDraw(drawId);
	        	if (updated == 0) {
	        	    log.warn("[BATCH][DRAW][SKIP-STATUS] drawId={}", drawId);
	        	    continue;
	        	}
	        	/* 추첨 */
	            drawAdminService.drawWinners(drawId);
	        } catch (Exception e) {
	            log.error("[BATCH][DRAW][DRAW] drawId={}", drawId, e);
	        }
	    }
		
	}
	
	
	/** 드로우 픽업 기간 종료 → 노쇼 처리 */
	@Transactional
	public void drawPickupEnd() {

	    /* 1. 픽업 종료 대상 드로우 조회 */
	    List<Long> drawIds = drawAdminMapper.selectDrawIdsForPickupEnd();

	    for (Long drawId : drawIds) {
	        try {
	            /* 2. 노쇼 처리 (WIN 중 판매 없는 경우) */
	            int noShowCnt = drawAdminMapper.updateNoShowWinners(drawId);

	            /* 3. 드로우 상태 END 처리 */
	            int endCnt = drawAdminMapper.updateDrawStatusToEnd(drawId);

	            log.info(
	                "[BATCH][DRAW][PICKUP-END] drawId={} noShowCnt={} endCnt={}",
	                drawId, noShowCnt, endCnt
	            );
	        } catch (Exception e) {
	            log.error("[BATCH][DRAW][PICKUP-END] drawId={}", drawId, e);
	        }
	    }
	}
}
