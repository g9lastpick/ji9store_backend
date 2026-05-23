package com.jjsoft.pos.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;

import com.jjsoft.pos.enums.ReceiptType;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "sales_receipt")
@Comment("매출 영수증")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SalesReceiptEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RECEIPT_ID")
    @Comment("영수증 ID")
    private Long receiptId;

    /* =========================
     * FK 컬럼
     * ========================= */

    @Column(name = "SALES_ID", nullable = false)
    @Comment("매출 ID")
    private Long salesId;

    /* =========================
     * POS / 영수증 정보
     * ========================= */

    @Column(name = "POS_TX_ID", length = 100)
    @Comment("POS 거래 ID")
    private String posTxId;

    @Column(name = "RECEIPT_NO", length = 100)
    @Comment("영수증 번호")
    private String receiptNo;

    @Enumerated(EnumType.STRING)
    @Column(name = "RECEIPT_TYPE", length = 10)
    @Comment("영수증 타입 (SALE / CANCEL)")
    private ReceiptType receiptType;

    @Column(name = "RECEIPT_DATA", columnDefinition = "MEDIUMTEXT")
    @Comment("POS 영수증 원문(JSON/Text)")
    private String receiptData;

    @Column(name = "RECEIPT_URL", length = 255)
    @Comment("영수증 이미지/PDF URL")
    private String receiptUrl;

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
    @JoinColumn(name = "SALES_ID", insertable = false, updatable = false)
    @Comment("매출 정보")
    private SalesMstEntity sales;
}
