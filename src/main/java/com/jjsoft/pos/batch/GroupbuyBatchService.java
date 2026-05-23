package com.jjsoft.pos.batch;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jjsoft.pos.entity.GroupbuyMstEntity;
import com.jjsoft.pos.entity.GroupbuyPriceStepEntity;
import com.jjsoft.pos.enums.GroupbuyStatus;
import com.jjsoft.pos.mapper.GroupbuyAdminMapper;
import com.jjsoft.pos.repository.GroupbuyMstRepository;
import com.jjsoft.pos.repository.GroupbuyPriceStepRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
//@Service
@RequiredArgsConstructor
public class GroupbuyBatchService {

	private final GroupbuyMstRepository        groupbuyMstRepository;
    private final GroupbuyPriceStepRepository  groupbuyPriceStepRepository;
    private final GroupbuyAdminMapper          groupbuyAdminMapper;

    
    
    
    /************************************************
     * 공동구매 시작 처리
     ************************************************/
    @Transactional
    public void startGroupbuys() {

    	try {
    		System.out.println("startGroupbuys LocalDateTime.now() = " + LocalDateTime.now());
    		int updated = groupbuyAdminMapper.startGroupbuys();

            if (updated > 0) {
                log.info("[BATCH][GROUPBUY][START] startedCount={}", updated);
            }
		} catch (Exception e) {
			// TODO: handle exception
		}
        
    }
    
    
    
    
    
    /************************************************
     * 공동구매 참여 종료된 공동구매에 대한 일괄 처리
     ************************************************/
    @Transactional
    public void processEndedGroupbuys() {

        LocalDateTime now = LocalDateTime.now();

        /* 1. 참여 종료 대상 공동구매 조회 */
        List<GroupbuyMstEntity> targets =
                groupbuyMstRepository.findByStatusAndEndDateBefore(
                        GroupbuyStatus.START, 
                        now
                );

        if (targets.isEmpty()) {
            return;
        }

        log.info("[GROUPBUY][BATCH] targetCount={}", targets.size());

        /* 2. 공동구매별 처리 */
        for (GroupbuyMstEntity groupbuy : targets) {
            try {
                processSingleGroupbuy(groupbuy);
            } catch (Exception e) {
                log.error("[GROUPBUY][BATCH][ERROR] id={}", groupbuy.getGroupbuyId(), e);
            }
        }
    }

    /**
     * 공동구매 단건 처리
     */
    private void processSingleGroupbuy(GroupbuyMstEntity groupbuy) {

        /* 재처리 방어 */
        if (groupbuy.getStatus() != GroupbuyStatus.START) {
            return;
        }

        int finalQty = groupbuy.getCurrentQty();

        /* 3. 적용 가능한 가격 정책 조회 */
        Optional<GroupbuyPriceStepEntity> stepOpt =
                groupbuyPriceStepRepository
                        .findMatchedStep(groupbuy.getGroupbuyId(), finalQty);

        if (stepOpt.isEmpty()) {
            failGroupbuy(groupbuy);
        } else {
            successGroupbuy(groupbuy, stepOpt.get());
        }
    }

    /**
     * 공동구매 실패 처리
     */
    private void failGroupbuy(GroupbuyMstEntity groupbuy) {

        /* 참여자 FAIL 처리 */
        groupbuyAdminMapper.updateJoinStatusToFail(groupbuy.getGroupbuyId() );

        /* 공동구매 상태 FAIL */
        groupbuy.setStatus(GroupbuyStatus.FAIL);
//        groupbuyMstRepository.save(groupbuy);// 공동구매시 주석 풀어야함

        log.info("[GROUPBUY][BATCH][FAIL] id={} qty={}",
                 groupbuy.getGroupbuyId(),
                 groupbuy.getCurrentQty());
    }

    /**
     * 공동구매 성공 처리
     */
    private void successGroupbuy(GroupbuyMstEntity groupbuy,GroupbuyPriceStepEntity step) {

        int unitPrice = step.getSalesPrice();

        /* 참여자 단가 확정 + 상태 변경 */
        groupbuyAdminMapper.updateJoinSuccess( groupbuy.getGroupbuyId(), unitPrice );

        /* 공동구매 상태 SUCCESS - 픽업 시간이 끝나면 END 처리 */
        groupbuy.setStatus(GroupbuyStatus.SUCCESS);
//        groupbuyMstRepository.save(groupbuy);// 공동구매시 주석 풀어야함

        log.info("[GROUPBUY][BATCH][SUCCESS] id={} qty={} unitPrice={}",
                 groupbuy.getGroupbuyId(),
                 groupbuy.getCurrentQty(),
                 unitPrice);
    }
    
    
    
    
    /************************************************
     * 공동구매 픽업 종료 처리 
     ************************************************/
    @Transactional
    public void groupbuyPickupEnd() {
    	List<GroupbuyMstEntity> targets =
                groupbuyMstRepository.findPickupEndTargets(LocalDateTime.now());

        for (GroupbuyMstEntity groupbuy : targets) {
            try {
                processPickupEnd(groupbuy);
            } catch (Exception e) {
                log.error("[GROUPBUY][PICKUP_END][FAIL] id={}", groupbuy.getGroupbuyId(), e);
            }
        }
    }
    
    private void processPickupEnd(GroupbuyMstEntity groupbuy) {

        Long groupbuyId = groupbuy.getGroupbuyId();

        log.info("[GROUPBUY][PICKUP_END][START] id={}", groupbuyId);

        /* 1. 미결제 참여자 노쇼 처리 */
        int noShowCnt = groupbuyAdminMapper.updateNoShowEntries(groupbuyId);

        /* 2. 공동구매 상태 END 처리 */
        groupbuy.setStatus(GroupbuyStatus.END);
//        groupbuyMstRepository.save(groupbuy);// 공동구매시 주석 풀어야함

        log.info("[GROUPBUY][PICKUP_END][DONE] id={} noShowCnt={}", groupbuyId, noShowCnt);
    }
}
