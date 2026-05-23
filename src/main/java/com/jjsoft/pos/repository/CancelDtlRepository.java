package com.jjsoft.pos.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jjsoft.pos.entity.CancelDtlEntity;

/**
 * 결제 취소 상세 Repository
 */
//@Repository
public interface CancelDtlRepository  {//extends JpaRepository<CancelDtlEntity, Long>

    /**
     * 취소 마스터 기준 상세 조회
     */
    List<CancelDtlEntity> findByCancelId(Long cancelId);

    /**
     * 판매 상세 기준 취소 이력 조회
     */
    List<CancelDtlEntity> findBySalesDtlId(Long salesDtlId);
}
