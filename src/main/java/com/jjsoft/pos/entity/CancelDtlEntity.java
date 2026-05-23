package com.jjsoft.pos.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import lombok.*;

//@Entity
//@Table(name = "cancel_dtl")//2222222222
@Comment("결제 취소 상세")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CancelDtlEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CANCEL_DTL_ID")
    @Comment("취소 상세 ID")
    private Long cancelDtlId;

    /* =========================
     * FK 컬럼
     * ========================= */

    @Column(name = "CANCEL_ID", nullable = false)
    @Comment("취소 마스터 ID")
    private Long cancelId;

    @Column(name = "SALES_DTL_ID", nullable = false)
    @Comment("취소 대상 판매 상세 ID")
    private Long salesDtlId;

    /* =========================
     * 취소 정보
     * ========================= */

    @Column(name = "CANCEL_QTY", nullable = false)
    @Comment("취소 수량")
    private Integer cancelQty;

    @Column(name = "CANCEL_AMOUNT", nullable = false)
    @Comment("취소 금액")
    private Integer cancelAmount;

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
    @JoinColumn(name = "CANCEL_ID", insertable = false, updatable = false)
    @Comment("취소 마스터")
    private CancelMstEntity cancelMst;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SALES_DTL_ID", insertable = false, updatable = false)
    @Comment("판매 상세")
    private SalesDtlEntity salesDtl;
}
