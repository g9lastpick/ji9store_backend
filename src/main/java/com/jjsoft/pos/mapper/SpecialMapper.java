package com.jjsoft.pos.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.jjsoft.pos.dto.special.ReservationItemDto;
import com.jjsoft.pos.dto.special.SpecialCalendarEventDto;
import com.jjsoft.pos.dto.special.SpecialColumnsDto;
import com.jjsoft.pos.dto.special.SpecialDtlDto;
import com.jjsoft.pos.dto.special.condition.SpecialSearchCondition;

@Mapper
public interface SpecialMapper {

	public List<SpecialCalendarEventDto>  selectSpecialsForCalendar(@Param("startDate") String startDate ,@Param("endDate") String endDate ,@Param("locationId") int locationId);
	public List<SpecialDtlDto>  selectSpecialProductList(@Param("specialId") Long specialId);

	
	
	public List<Map<String, Object>>  selectPivotReservation(SpecialSearchCondition condition);
	public List<SpecialColumnsDto>  selectSpecialColumns( SpecialSearchCondition condition);

	/** 고객 단위 특가 예약(예약중) 픽업 항목 조회 */
	public List<Map<String, Object>>  selectUserSpecialPickupItems(@Param("userId") String userId, @Param("storeId") Long storeId, @Param("locationId") Long locationId);

	/** 현재 픽업 가능한 특가 예약이 있는 고객 목록 (특가 시작일~픽업종료일 기준) */
	public List<Map<String, Object>>  selectPickupUsers(@Param("storeId") Long storeId, @Param("locationId") Long locationId);
}
