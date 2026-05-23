package com.jjsoft.pos.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jjsoft.pos.entity.GroupbuyJoinMstEntity;
import com.jjsoft.pos.enums.GroupbuyJoinStatus;

/**
 * 공동구매 참여 마스터 Repository
 */
//@Repository
public interface GroupbuyJoinMstRepository {// extends JpaRepository<GroupbuyJoinMstEntity, Long>

    /**
     * 공동구매 기준 참여자 목록 조회
     */
    List<GroupbuyJoinMstEntity> findByGroupbuyId(Long groupbuyId);

    /**
     * 유저 기준 공동구매 참여 조회
     */
    List<GroupbuyJoinMstEntity> findByUserId(String userId);

    /**
     * 공동구매 + 유저 단건 참여 조회
     */
    Optional<GroupbuyJoinMstEntity> findByGroupbuyIdAndUserId(
            Long groupbuyId,
            String userId
    );

    /**
     * 공동구매 + 상태별 참여 조회
     */
    List<GroupbuyJoinMstEntity> findByGroupbuyIdAndJoinStatus(
            Long groupbuyId,
            GroupbuyJoinStatus joinStatus
    );
    
    
    /**
     * 공동구매 참여자 존재 여부 확인
     * @param groupbuyId 공동구매 ID
     * @return 참여자 존재 여부
     */
    boolean existsByGroupbuyId(Long groupbuyId);
    
    /** 기존 예약 조회 */
    Optional<GroupbuyJoinMstEntity> findByGroupbuyIdAndUserIdAndJoinStatus(
            Long groupbuyId,
            String userId,
            GroupbuyJoinStatus joinStatus
    );
}
