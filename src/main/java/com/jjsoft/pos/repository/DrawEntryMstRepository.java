package com.jjsoft.pos.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jjsoft.pos.entity.DrawEntryMstEntity;

/**
 * 드로우 응모 마스터 Repository
 */
//@Repository
public interface DrawEntryMstRepository {// extends JpaRepository<DrawEntryMstEntity, Long>
	
	 /** 드로우 중복 참여 여부 확인 */
    boolean existsByDrawIdAndUserId(Long drawId, String userId);
    
    /** 참여 유저 조회 */
    Optional<DrawEntryMstEntity> findByDrawIdAndUserId(Long drawId, String userId);
    

}
