package com.jjsoft.pos.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.jjsoft.pos.entity.SpecialRsvDtlEntity;

/**
 * 특가 예약 상세 Repository
 */
@Repository
public interface SpecialRsvDtlRepository extends JpaRepository<SpecialRsvDtlEntity, Long> {
	
	@Transactional
	@Modifying
	@Query("""
	    DELETE FROM SpecialRsvDtlEntity d 
	    WHERE d.specialRsvMstId IN (
	        SELECT r.specialRsvMstId 
	        FROM SpecialRsvMstEntity r 
	        WHERE r.specialId = :specialId
	    )
	""")
	void deleteBySpecialId(@Param("specialId") Long specialId);
	
	
	void deleteBySpecialRsvMstId(Long specialRsvMstId);
	
	
	@Modifying
	@Query("DELETE FROM SpecialRsvDtlEntity d WHERE d.specialRsvMstId = :specialRsvMstId")
	void deleteByMstId(@Param("specialRsvMstId") Long specialRsvMstId);
	
	
	/**
     * 특가 예약 마스터 ID로 상세 예약 리스트 조회
     * 
     * @param specialRsvMstId 예약 마스터 ID
     * @return 예약 상세 리스트
     */
    List<SpecialRsvDtlEntity> findBySpecialRsvMstId(Long specialRsvMstId);
    
    
    
    Optional<SpecialRsvDtlEntity> findBySpecialRsvMstIdAndSpecialDtlId(Long specialRsvMstId, Long specialDtlId);
    
    
    
    
    
    
    
    
    
    
    
    /* Mobile 용 start */
    void deleteBySpecialRsvMstIdAndSpecialDtlId(Long specialRsvMstId, Long specialDtlId);

    @Query("select coalesce(sum(d.reservationCnt), 0) " +
           "from SpecialRsvDtlEntity d " +
           "where d.specialRsvMstId = :specialRsvMstId and coalesce(d.cancelYn, 'N') = 'N'")
    Integer sumReservationCntByMstId(@Param("specialRsvMstId") Long specialRsvMstId);

    @Query("select coalesce(sum(d.reservationPrice), 0) " +
           "from SpecialRsvDtlEntity d " +
           "where d.specialRsvMstId = :specialRsvMstId and coalesce(d.cancelYn, 'N') = 'N'")
    Integer sumReservationPriceByMstId(@Param("specialRsvMstId") Long specialRsvMstId);
    
    
    // ✅ 소프트 취소
    @Modifying
    @Query("update SpecialRsvDtlEntity d " +
           "set d.cancelYn = 'Y', d.reservationCnt = 0, d.reservationPrice = 0 " +
           "where d.specialRsvMstId = :mstId and d.specialDtlId = :dtlId")
    int softCancel(@Param("mstId") Long mstId, @Param("dtlId") Long dtlId);
    
    /* Mobile 용 end */

}