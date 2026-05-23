package com.jjsoft.pos.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

//@Entity
//@Table(name = "groupbuy_price_step")
@Comment("공동구매 단계별 가격")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class GroupbuyPriceStepEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PRICE_STEP_ID")
    @Comment("가격 단계 ID")
    private Long priceStepId;

    /* =========================
     * FK 컬럼
     * ========================= */

    @Column(name = "GROUPBUY_ID", nullable = false)
    @Comment("공동구매 상품 ID")
    private Long groupbuyId;

    /* =========================
     * 단계 조건
     * ========================= */

    @Column(name = "STEP_QTY_FROM", nullable = false)
    @Comment("수량 시작")
    private Integer stepQtyFrom;

    @Column(name = "STEP_QTY_TO", nullable = false)
    @Comment("수량 종료")
    private Integer stepQtyTo;

    /* =========================
     * 가격 정보
     * ========================= */

    @Column(name = "SALES_PRICE", nullable = false)
    @Comment("단가")
    private Integer salesPrice;

    @Column(name = "SALES_RATE", precision = 5, scale = 2)
    @Comment("할인율")
    private BigDecimal  salesRate;

    /* =========================
     * 공통 컬럼
     * ========================= */

    @Column(name = "DESCRIPTION", length = 1000)
    @Comment("설명")
    private String description;

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
    @JoinColumn(name = "GROUPBUY_ID", insertable = false, updatable = false)
    @Comment("공동구매 상품 정보")
    private GroupbuyMstEntity groupbuyMst;
}
