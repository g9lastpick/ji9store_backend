package com.jjsoft.pos.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.jjsoft.pos.entity.SalesDtlEntity;

@Repository
public interface SalesDtlRepository extends JpaRepository<SalesDtlEntity, Long> {
	
	
	 
	@Modifying
    @Transactional
    @Query("DELETE FROM SalesDtlEntity d WHERE d.sales.salesId = :salesId")
    void deleteAllBySalesId(@Param("salesId") Long salesId);
	
	
	/**
     * 판매 상세 조회 (최근 순)
     * sales_id + product_id 기준
     */
    List<SalesDtlEntity> findBySalesIdAndProductIdOrderBySalesDtlIdDesc(
            Long salesId,
            Long productId
    );

	
}



