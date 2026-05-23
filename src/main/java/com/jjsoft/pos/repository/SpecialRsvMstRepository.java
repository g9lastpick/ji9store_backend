package com.jjsoft.pos.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.jjsoft.pos.entity.SpecialRsvMstEntity;
import com.jjsoft.pos.enums.ReservationStatus;

/**
 * 특가 예약 마스터 Repository
 */
@Repository
public interface SpecialRsvMstRepository extends JpaRepository<SpecialRsvMstEntity, Long> {
	
	@Transactional
	@Modifying
	@Query("DELETE FROM SpecialRsvMstEntity r WHERE r.specialId = :specialId")
	void deleteBySpecialId(@Param("specialId") Long specialId);
	
	boolean existsBySpecialId(Long specialId);
	
	
	@Modifying
	@Query("UPDATE SpecialRsvMstEntity e SET e.reservationStatus = :status WHERE e.id = :mstId")
	void updateStatus(@Param("mstId") Long mstId, @Param("status") String status);
	
//	@Query("SELECT m FROM SpecialRsvMstEntity m " +
//		       "WHERE m.specialId = :specialId AND m.userId = :userId")
//	Optional<SpecialRsvMstEntity> findFirstBySpecialIdAndUserId(@Param("specialId") Long specialId,
//		                                                            @Param("userId") String userId);
//	
	@Query("SELECT m FROM SpecialRsvMstEntity m WHERE m.specialId = :specialId AND m.userId = :userId")
	Optional<SpecialRsvMstEntity> findFirstBySpecialIdAndUserId(@Param("specialId") Long specialId,
	                                                            @Param("userId") String userId);
	
	
	/**
     * 특정 SPECIAL_ID, USER_ID, STATUS로 예약 마스터 조회
     * 
     * @param specialId  특가 ID
     * @param userId     사용자 ID
     * @param status     예약 상태 (예: RESERVATION)
     * @return SpecialRsvMstEntity
     */
    @Query("""
        SELECT m
        FROM SpecialRsvMstEntity m
        WHERE m.specialId = :specialId
          AND m.userId = :userId
          AND m.reservationStatus = :status
    """)
    SpecialRsvMstEntity findBySpecialIdAndUserIdAndStatus(
            @Param("specialId") Long specialId,
            @Param("userId") String userId,
            @Param("status") ReservationStatus status
    );
}

