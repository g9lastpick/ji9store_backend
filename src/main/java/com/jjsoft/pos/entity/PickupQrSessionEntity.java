package com.jjsoft.pos.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;

import com.jjsoft.pos.enums.PickupQrStatus;

import jakarta.persistence.*;
import lombok.*;

//@Entity
//@Table(name = "pickup_qr_session")
@Comment("픽업/결제 QR 세션")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PickupQrSessionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "QR_SESSION_ID")
    @Comment("QR 세션 ID")
    private Long qrSessionId;

    /* =========================
     * 식별 정보
     * ========================= */

    @Column(name = "USER_ID", length = 150)
    @Comment("유저 ID (비회원 가능)")
    private String userId;

    @Column(name = "PHONE_NO", length = 20)
    @Comment("비회원 식별용")
    private String phoneNo;

    @Column(name = "QR_TOKEN", nullable = false, length = 255)
    @Comment("QR 토큰")
    private String qrToken;

    /* =========================
     * 픽업 가능 기간
     * ========================= */

    @Column(name = "PICKUP_FROM", nullable = false)
    @Comment("픽업 시작일(MIN)")
    private LocalDateTime pickupFrom;

    @Column(name = "PICKUP_TO", nullable = false)
    @Comment("픽업 종료일(MAX)")
    private LocalDateTime pickupTo;

    /* =========================
     * 상태
     * ========================= */

    @Enumerated(EnumType.STRING)
    @Column(name = "PICKUP_STATUS", length = 20)
    @Comment("픽업 여부")
    private PickupQrStatus pickupStatus;

    @Column(name = "USED_DATE")
    @Comment("사용 일시")
    private LocalDateTime usedDate;

    /* =========================
     * 공통 컬럼
     * ========================= */

    @CreationTimestamp
    @Column(name = "CREATE_DATE", updatable = false)
    @Comment("생성일자")
    private LocalDateTime createDate;

    @Column(name = "CREATE_USER", length = 50)
    @Comment("생성자")
    private String createUser;
}
