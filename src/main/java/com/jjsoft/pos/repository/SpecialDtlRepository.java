package com.jjsoft.pos.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.jjsoft.pos.dto.special.SpecialDtlDto;
import com.jjsoft.pos.entity.SpecialDtlEntity;

/**
 * 특가 상세 Repository
 */
@Repository
public interface SpecialDtlRepository extends JpaRepository<SpecialDtlEntity, Long> {
	
	@Transactional
	@Modifying
	@Query("DELETE FROM SpecialDtlEntity d WHERE d.specialId = :specialId")
	void deleteBySpecialId(@Param("specialId") Long specialId);
	
	
	/**
     * processStatus != 'START' 인 특가 마스터에 속하면서
     * imageUrl 이 NULL 이 아닌 상세 목록 조회
     */
	@Query("SELECT d FROM SpecialDtlEntity d " +
	           "JOIN d.special m " +
	           "WHERE m.progressType <> 'START' " +
	           "AND d.imageUrl IS NOT NULL")
	    List<SpecialDtlEntity> findExpiredWithImage();
	
	
	/** 상품명 조회*/
	@Query("SELECT p.productNm " +
	           "FROM SpecialDtlEntity d " +
	           "JOIN d.product p " +
	           "WHERE d.specialDtlId = :specialDtlId")
	    String findProductNmBySpecialDtlId(@Param("specialDtlId") Long specialDtlId);
	
	@Query("SELECT new com.jjsoft.pos.dto.special.SpecialDtlDto(p.productNm, d.remainQty) " +
	           "FROM SpecialDtlEntity d " +
	           "JOIN d.product p " +
	           "WHERE d.specialDtlId = :specialDtlId")
	SpecialDtlDto findProductNmAndRemainQty(@Param("specialDtlId") Long specialDtlId);
}