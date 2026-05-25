package com.jjsoft.pos.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

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

	List<CategoryDto> selectCategoryAll(Long storeId);
	
	
}
