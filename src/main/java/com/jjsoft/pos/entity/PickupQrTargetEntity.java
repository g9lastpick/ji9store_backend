package com.jjsoft.pos.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;

import com.jjsoft.pos.enums.PickupQrTargetStatus;
import com.jjsoft.pos.enums.PickupQrTargetType;

import jakarta.persistence.*;
import lombok.*;

//@Entity
//@Table(name = "pickup_qr_target")
@Comment("QR 결제 대상 매핑")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PickupQrTargetEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "QR_TARGET_ID")
    @Comment("QR 대상 ID")
    private Long qrTargetId;

    /* =========================
     * FK 컬럼
     * ========================= */

    @Column(name = "QR_SESSION_ID", nullable = false)
    @Comment("QR 세션 ID")
    private Long qrSessionId;

    /* =========================
     * 대상 정보
     * ========================= */

    @Enumerated(EnumType.STRING)
    @Column(name = "TARGET_TYPE", length = 20, nullable = false)
    @Comment("대상 타입 (SPECIAL / GROUPBUY)")
    private PickupQrTargetType targetType;

    @Column(name = "TARGET_MST_ID", nullable = false)
    @Comment("특가 예약 ID / 공동구매 참여 ID / DRAW_ENTRY_ID 등")
    private Long targetMstId;

    @Column(name = "AMOUNT", nullable = false)
    @Comment("결제 대상 금액")
    private Integer amount;

    /* =========================
     * 상태
     * ========================= */

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", length = 20)
    @Comment("픽업 가능 상태")
    private PickupQrTargetStatus status;

    /* =========================
     * 공통 컬럼
     * ========================= */

    @CreationTimestamp
    @Column(name = "CREATE_DATE", updatable = false)
    @Comment("생성일자")
    private LocalDateTime createDate;

    /* =========================
     * 연관관계 (읽기 전용)
     * ========================= */

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "QR_SESSION_ID", insertable = false, updatable = false)
    @Comment("QR 세션")
    private PickupQrSessionEntity qrSession;
}
