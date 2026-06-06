package com.jjsoft.pos.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.jjsoft.pos.entity.GroupbuyMstEntity;
import com.jjsoft.pos.enums.GroupbuyStatus;


/**
 * 공동구매 마스터 Repository 2222222222
 */
@Repository
public interface GroupbuyMstRepository extends JpaRepository<GroupbuyMstEntity, Long> {

	
	/** batch : 공동구매 참여 종료 처리 타켓 리스트  */
	public List<GroupbuyMstEntity> findByStatusAndEndDateBefore(
	        GroupbuyStatus status,
	        LocalDateTime endDate
	);
	
	
	
	
	
	/** 공동구매 성공된 건에 대한 픽업시간 지난건에 대한 리스트 */
	@Query("""
        select g
        from GroupbuyMstEntity g
        where g.status = com.jjsoft.pos.enums.GroupbuyStatus.SUCCESS
          and g.pickupEndDate < :now
	""")
	public List<GroupbuyMstEntity> findPickupEndTargets(@Param("now") LocalDateTime now);
    
}
