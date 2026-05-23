package com.jjsoft.pos.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jjsoft.pos.entity.DrawMstEntity;
import com.jjsoft.pos.enums.DrawStatus;

/**
 * 드로우 이벤트 마스터 Repository
 */
//@Repository
public interface DrawMstRepository  {//extends JpaRepository<DrawMstEntity, Long>

    /**
     * 상태별 드로우 조회
     */
    List<DrawMstEntity> findByStatus(DrawStatus status);

    /**
     * 응모 가능 드로우 조회 (현재 시간 기준)
     */
    List<DrawMstEntity> findByEntryStartDateLessThanEqualAndEntryEndDateGreaterThanEqual(
            LocalDateTime now1,
            LocalDateTime now2
    );

    /**
     * 매장 기준 드로우 조회
     */
    List<DrawMstEntity> findByStoreId(Long storeId);

    /**
     * 지점 기준 드로우 조회
     */
    List<DrawMstEntity> findByLocationId(Long locationId);
}
