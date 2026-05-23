package com.jjsoft.pos.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;

import com.jjsoft.pos.enums.CancelType;
import com.jjsoft.pos.enums.YesNoType;

import jakarta.persistence.*;
import lombok.*;

//@Entity
//@Table(name = "cancel_mst")//2222222222
@Comment("결제 취소 마스터")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CancelMstEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CANCEL_ID")
    @Comment("취소 ID")
    private Long cancelId;

    /* =========================
     * FK 컬럼
     * ========================= */

    @Column(name = "SALES_ID", nullable = false)
    @Comment("원 매출 ID")
    private Long salesId;

    /* =========================
     * 취소 정보
     * ========================= */

    @Enumerated(EnumType.STRING)
    @Column(name = "CANCEL_TYPE", length = 10)
    @Comment("부분/전체 취소 (PART / ALL)")
    private CancelType cancelType;

    @Column(name = "TOTAL_AMOUNT", nullable = false)
    @Comment("취소 금액")
    private Integer totalAmount;

    /* =========================
     * POS 연동 결과
     * ========================= */

    @Enumerated(EnumType.STRING)
    @Column(name = "POS_CANCEL_YN", length = 1)
    @Comment("POS 취소 성공 여부")
    private YesNoType posCancelYn;

    @Column(name = "POS_CANCEL_CD", length = 50)
    @Comment("POS 취소 결과 코드")
    private String posCancelCd;

    @Column(name = "POS_CANCEL_MSG", length = 255)
    @Comment("POS 취소 메시지")
    private String posCancelMsg;

    @Column(name = "POS_RES", columnDefinition = "TEXT")
    @Comment("POS 취소 응답 원문(JSON)")
    private String posRes;

    /* =========================
     * 기타
     * ========================= */

    @Column(name = "CANCEL_REASON", length = 255)
    @Comment("취소 사유")
    private String cancelReason;

    @CreationTimestamp
    @Column(name = "CREATE_DATE", updatable = false)
    @Comment("생성일자")
    private LocalDateTime createDate;

    @Column(name = "CREATE_USER", length = 50)
    @Comment("생성자")
    private String createUser;

    /* =========================
     * 연관관계 (읽기 전용)
     * ========================= */

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SALES_ID", insertable = false, updatable = false)
    @Comment("원 매출 정보")
    private SalesMstEntity sales;
}
