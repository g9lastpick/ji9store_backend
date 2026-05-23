package com.jjsoft.pos.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jjsoft.pos.entity.PickupQrSessionEntity;
import com.jjsoft.pos.enums.PickupQrStatus;

/**
 * 픽업/결제 QR 세션 Repository
 */
//@Repository
public interface PickupQrSessionRepository  {//extends JpaRepository<PickupQrSessionEntity, Long>

    /**
     * QR 토큰으로 세션 조회
     */
    Optional<PickupQrSessionEntity> findByQrToken(String qrToken);

    /**
     * 유저 기준 최근 QR 세션 조회
     */
    Optional<PickupQrSessionEntity> findTopByUserIdOrderByCreateDateDesc(String userId);

    /**
     * 상태별 세션 조회
     */
    Optional<PickupQrSessionEntity> findByQrSessionIdAndPickupStatus(
            Long qrSessionId,
            PickupQrStatus pickupStatus
    );

    /**
     * 만료 대상 세션 조회 (배치용)
     */
    Iterable<PickupQrSessionEntity> findByPickupToBeforeAndPickupStatus(
            LocalDateTime now,
            PickupQrStatus pickupStatus
    );
}
