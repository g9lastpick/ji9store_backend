package com.jjsoft.pos.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jjsoft.pos.entity.GroupbuyResultLogEntity;
import com.jjsoft.pos.enums.GroupbuyResultType;

/**
 * 공동구매 결과 로그 Repository
 */
//@Repository
public interface GroupbuyResultLogRepository  {//extends JpaRepository<GroupbuyResultLogEntity, Long>

    /**
     * 공동구매 기준 결과 로그 조회
     */
    List<GroupbuyResultLogEntity> findByGroupbuyId(Long groupbuyId);

    /**
     * 공동구매 + 결과 타입 조회
     */
    List<GroupbuyResultLogEntity> findByGroupbuyIdAndResultType(
            Long groupbuyId,
            GroupbuyResultType resultType
    );
}
