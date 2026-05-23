package com.jjsoft.pos.service.admin.pos;

import org.springframework.stereotype.Service;

import com.jjsoft.pos.dto.groupbuy.GroupbuyJoinResponseDto;
/**
 * POS 결제 연동 Service
 */
@Service
public class PosPaymentService {
	  /**
     * 공동구매 결제 요청
     * - 수량/금액 수정 불가
     */
    public void requestPaymentForGroupbuy(GroupbuyJoinResponseDto joinInfo) {
    	
    }
}
