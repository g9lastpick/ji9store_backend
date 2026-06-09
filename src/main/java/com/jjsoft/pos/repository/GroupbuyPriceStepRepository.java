package com.jjsoft.pos.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.jjsoft.pos.entity.GroupbuyPriceStepEntity;

/**
 * 공동구매 단계별 가격 Repository
 */
@Repository
public interface GroupbuyPriceStepRepository extends JpaRepository<GroupbuyPriceStepEntity, Long> {


    void deleteByGroupbuyId(Long groupbuyId);

    /** 첫 번째 급간(최소 수량) — 자동 픽업 시작 판정용 */
    @Query("select min(p.stepQtyFrom) from GroupbuyPriceStepEntity p where p.groupbuyId = :groupbuyId")
    Integer findFirstStepQty(@Param("groupbuyId") Long groupbuyId);
    
    @Query("""
    	    SELECT p
    	    FROM   GroupbuyPriceStepEntity p
    	    WHERE  p.groupbuyId = :groupbuyId
    	      AND  p.stepQtyFrom <= :qty
    	      AND  p.stepQtyTo   >= :qty
    	""")
    	Optional<GroupbuyPriceStepEntity> findMatchedStep(
    	        @Param("groupbuyId") Long groupbuyId,
    	        @Param("qty") Integer qty
    	);
}
