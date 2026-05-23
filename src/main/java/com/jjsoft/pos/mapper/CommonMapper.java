package com.jjsoft.pos.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;

import com.jjsoft.pos.dto.common.CategoryDto;
import com.jjsoft.pos.dto.common.CodeValueDto;

@Mapper
public interface CommonMapper {
	
	
	List<CodeValueDto> selectPartners(Long storeId);
	List<CodeValueDto> selectCategorys(Long storeId);
	List<CodeValueDto> selectLocations(Long storeId);
	List<CodeValueDto> selectStatus(Long storeId);
	List<CodeValueDto> selectSalesStatus(Long storeId);
	List<CodeValueDto> selectSalesType(Long storeId);
	List<CodeValueDto> selectPaymentType(Long storeId);
	List<CodeValueDto> selectProgressList(Long storeId);
	List<CodeValueDto> selectReservationList(Long storeId);
	
	
	
	List<CodeValueDto> selectSpecialKeyList(String startDate);
	
	/** 쿼리 실행 결과 */
	List<Map<String, Object>> sqlplay(@Param("sqlString") String sqlString);
	
	List<CategoryDto> selectCategoryAll(Long storeId);
	
	
}
