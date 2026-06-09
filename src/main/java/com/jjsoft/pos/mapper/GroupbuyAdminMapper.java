package com.jjsoft.pos.mapper;

import java.util.List;
import java.time.LocalDateTime;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.jjsoft.pos.dto.groupbuy.GroupbuyDetailResponseDto;
import com.jjsoft.pos.dto.groupbuy.GroupbuyJoinResponseDto;
import com.jjsoft.pos.dto.groupbuy.GroupbuyResponseDto;
import com.jjsoft.pos.dto.groupbuy.GroupbuySearchRequestDto;

@Mapper
public interface GroupbuyAdminMapper {

	/** 공동구매 월별 리스트 조회 (관리자) */
    List<GroupbuyResponseDto> selectGroupbuyList(GroupbuySearchRequestDto requestDto);
	
	/** 공동구매 상세 조회 - 단계별 가격 정보 같이 조회 됨 */
	public GroupbuyDetailResponseDto selectGroupbuyDetail(@Param("groupbuyId") Long groupbuyId);
	
	/** 공동구매 참여유저 조회 */
	public List<GroupbuyJoinResponseDto> selectGroupbuyJoinList(@Param("groupbuyId") Long groupbuyId, @Param("joinStatus") String joinStatus);

	/** 모바일 공동구매 활성 목록 */
	List<GroupbuyResponseDto> selectMobileGroupbuyList(@Param("storeId") Long storeId, @Param("locationId") Long locationId);

	/** 모바일 마이페이지 - 내 공동구매 예약 목록 */
	List<GroupbuyJoinResponseDto> selectMyGroupbuyList(@Param("userId") String userId, @Param("storeId") Long storeId);

	/** 고객 단위 공동구매 참여(JOIN) 픽업 항목 조회 (지점별) */
	java.util.List<java.util.Map<String,Object>> selectUserGroupbuyPickupItems(@Param("userId") String userId, @Param("storeId") Long storeId, @Param("locationId") Long locationId);

	/** 현재 픽업 가능한 공동구매 예약이 있는 고객 목록 (픽업 시작일~픽업종료일 기준) */
	java.util.List<java.util.Map<String,Object>> selectPickupUsers(@Param("storeId") Long storeId, @Param("locationId") Long locationId);
	
	
	/**
     * 공동구매 현재 수량 증가 (동시성 안전)
     */
    int increaseCurrentQty( @Param("groupbuyId") Long groupbuyId, @Param("joinQty") int joinQty );
    
    /**
     * 공동구매 현재 수량 차감
     */
    int decreaseCurrentQty( @Param("groupbuyId") Long groupbuyId, @Param("cancelQty") int cancelQty );

    /** 자동 픽업: 첫 급간 달성 시 픽업창(now~당일20시) 1회 세팅 (DB current_qty 기준, JPA 캐시 무관) */
    int autoStartPickup(@Param("groupbuyId") Long groupbuyId, @Param("now") LocalDateTime now, @Param("end") LocalDateTime end);
    
    
    
    
    
    
    
    
    
    
    
    
    
    /** batch에서  READY인 공동구매 START 처리 */
    public int startGroupbuys(@Param("now") LocalDateTime now);
    
    /** batch에서  실패 처리 */
    public int updateJoinStatusToFail(@Param("groupbuyId") Long groupbuyId);

    /** batch에서  성공 처리 */
    public int updateJoinSuccess(@Param("groupbuyId") Long groupbuyId,@Param("unitPrice") int unitPrice);
    
    
    /** batch에서  공동구매 픽업 시간 지난 후 노쇼 처리 처리 */
    public int updateNoShowEntries(@Param("groupbuyId") Long groupbuyId);

    /* ===== 픽업완료 → 매출처리 ===== */
    int selectProductOrgSalesPrice(@Param("productId") Long productId);
    java.util.Map<String,Object> selectGroupbuyAchievedStep(@Param("groupbuyId") Long groupbuyId, @Param("qty") int qty);
    java.util.List<java.util.Map<String,Object>> selectGroupbuyAvailableLots(@Param("productId") Long productId, @Param("locationId") Long locationId);
    void insertGroupbuySalesMst(java.util.Map<String,Object> param);
    void insertGroupbuySalesDtl(java.util.Map<String,Object> param);
    int decreaseLotStock(@Param("productDtlId") Long productDtlId, @Param("qty") int qty);
    int updateJoinPickupComplete(@Param("joinId") Long joinId, @Param("salesId") Long salesId, @Param("unitPrice") int unitPrice, @Param("updateUser") String updateUser);
}
