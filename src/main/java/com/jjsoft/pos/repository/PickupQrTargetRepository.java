package com.jjsoft.pos.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jjsoft.pos.entity.PickupQrTargetEntity;
import com.jjsoft.pos.enums.PickupQrTargetStatus;
import com.jjsoft.pos.enums.PickupQrTargetType;

/**
 * QR 결제 대상 매핑 Repository
 */
//@Repository
public interface PickupQrTargetRepository  {//extends JpaRepository<PickupQrTargetEntity, Long>

    /**
     * QR 세션 기준 대상 목록 조회
     */
    List<PickupQrTargetEntity> findByQrSessionId(Long qrSessionId);

    /**
     * 대상 타입 + 대상 ID 단건 조회
     */
    Optional<PickupQrTargetEntity> findByTargetTypeAndTargetMstId(
            PickupQrTargetType targetType,
            Long targetMstId
    );

    /**
     * QR 세션 + 상태별 대상 조회
     */
    List<PickupQrTargetEntity> findByQrSessionIdAndStatus(
            Long qrSessionId,
            PickupQrTargetStatus status
    );

    /**
     * 상태 기준 전체 조회 (배치용)
     */
    List<PickupQrTargetEntity> findByStatus(PickupQrTargetStatus status);
}
