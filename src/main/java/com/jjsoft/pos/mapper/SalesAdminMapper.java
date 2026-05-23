package com.jjsoft.pos.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param; 

import com.jjsoft.pos.dto.sales.SalesDtlDto;
import com.jjsoft.pos.dto.sales.SalesMstDto;
import com.jjsoft.pos.dto.sales.condition.SalesSearchCondition;

@Mapper
public interface SalesAdminMapper {

    List<SalesMstDto> selectSalesMstList(SalesSearchCondition condition);

    List<SalesDtlDto> selectSalesDtlList(Long salesId);

    List<Map<String, Object>> selectAvailableLots(
        @Param("productId") Long productId,
        @Param("locationId") Long locationId
    );
    
    
    List<Map<String, Object>> selectReservationAvailableLots(
            @Param("specialDtlId") Long specialDtlId,
            @Param("locationId") Long locationId
        );
}
