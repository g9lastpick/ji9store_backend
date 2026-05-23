package com.jjsoft.pos.batch;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class BatchScheduler {

//	private final GroupbuyBatchService groupbuyBatchService;
//	private final DrawBatchService drawBatchService;

    
    /** 공동구매 시작 처리 */
//    @Scheduled(cron = "0 */1 * * * *")
//    public void startGroupbuys() {
//        try {
//            groupbuyBatchService.startGroupbuys();
//        } catch (Exception e) {
//            log.error("[BATCH][GROUPBUY][START] failed", e);
//        }
//    }

    /** 공동구매 참여 종료 처리 (성공 / 실패 판정) */
//    @Scheduled(cron = "0 */10 * * * *")
    public void endGroupbuys() {
        try {
//            groupbuyBatchService.processEndedGroupbuys();// 공동구매시 주석 풀어야함
        } catch (Exception e) {
            log.error("[BATCH][GROUPBUY][END] failed", e);
        } 
    }

    /** 픽업 종료 → 노쇼 처리 */
//    @Scheduled(cron = "0 */10 * * * *")
    public void pickupEndGroupbuys() {
        try {
//            groupbuyBatchService.groupbuyPickupEnd();// 공동구매시 주석 풀어야함
        } catch (Exception e) {
            log.error("[BATCH][GROUPBUY][PICKUP-END] failed", e);
        }
    }
    
    
    
    /** 드로우 참여 종료 → 추첨 처리 */
//	@Scheduled(cron = "0 */1 * * * *")
	public void endDraws() {
		try {
//			drawBatchService.processEndedDraws();// 공동구매시 주석 풀어야함
		} catch (Exception e) {
			log.error("[BATCH][DRAW][END] failed", e);
		}
	}
    
	 
	 /** 드로우 픽업 종료 → 노쇼 처리 */
//	@Scheduled(cron = "0 */1 * * * *")
	public void pickupEndDraws() {
	    try {
//	        drawBatchService.drawPickupEnd();// 공동구매시 주석 풀어야함
	    } catch (Exception e) {
	        log.error("[BATCH][DRAW][PICKUP-END] failed", e);
	    }
	}
    
    
    
    
    
    
    
    
    
    
    
    
    
}
