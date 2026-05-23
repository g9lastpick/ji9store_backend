package com.jjsoft.pos.mapper;

import java.util.List;

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
	
	
	/**
     * 공동구매 현재 수량 증가 (동시성 안전)
     */
    int increaseCurrentQty( @Param("groupbuyId") Long groupbuyId, @Param("joinQty") int joinQty );
    
    /**
     * 공동구매 현재 수량 차감
     */
    int decreaseCurrentQty( @Param("groupbuyId") Long groupbuyId, @Param("cancelQty") int cancelQty );
    
    
    
    
    
    
    
    
    
    
    
    
    
    /** batch에서  READY인 공동구매 START 처리 */
    public int startGroupbuys();
    
    /** batch에서  실패 처리 */
    public int updateJoinStatusToFail(@Param("groupbuyId") Long groupbuyId);

    /** batch에서  성공 처리 */
    public int updateJoinSuccess(@Param("groupbuyId") Long groupbuyId,@Param("unitPrice") int unitPrice);
    
    
    /** batch에서  공동구매 픽업 시간 지난 후 노쇼 처리 처리 */
    public int updateNoShowEntries(@Param("groupbuyId") Long groupbuyId);
}
