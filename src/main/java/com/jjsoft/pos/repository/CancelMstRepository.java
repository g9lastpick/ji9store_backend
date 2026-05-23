package com.jjsoft.pos.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jjsoft.pos.entity.CancelMstEntity;
import com.jjsoft.pos.enums.CancelType;

/**
 * 결제 취소 마스터 Repository
 */
//@Repository
public interface CancelMstRepository {// extends JpaRepository<CancelMstEntity, Long>

    /**
     * 매출 기준 취소 이력 조회
     */
    List<CancelMstEntity> findBySalesId(Long salesId);

    /**
     * 매출 + 취소 타입 조회
     */
    List<CancelMstEntity> findBySalesIdAndCancelType(
            Long salesId,
            CancelType cancelType
    );
}
