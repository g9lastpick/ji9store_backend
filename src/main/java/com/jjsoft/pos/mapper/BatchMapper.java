package com.jjsoft.pos.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;

@Mapper
public interface BatchMapper {

	/** 상품 정보  판매가 초기화  */
	void resetOriginalPrice();
	/** 상품정보 할인 정책 처리 */
    void applyDiscountPolicy();
    
    void cancelTodayNoVisitReservations(@Param("today") String today);
    void stopTodaySpecialMst(@Param("today") String today);
    
    /** 1개월 지난 비즈톡 로그 삭제 */
    int deleteOldSendLogs();
}
