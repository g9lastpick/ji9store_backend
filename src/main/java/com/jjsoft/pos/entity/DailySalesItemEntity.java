package com.jjsoft.pos.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;

import lombok.*;

/**
 * 일별 상품 매출 집계 (엑셀 업로드 데이터)
 */
@Entity
@Table(name = "daily_sales_item")
@Comment("일별 상품 매출 집계")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class DailySalesItemEntity {

    /* =========================
     * PK
     * ========================= */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    @Comment("pkey ID")
    private Long id;

    /* =========================
     * FK 컬럼
     * ========================= */

    @Column(name = "LOCATION_ID", nullable = false)
    @Comment("지점 ID")
    private Long locationId;

    @Column(name = "CATEGORY_ID")
    @Comment("카테고리 ID")
    private Long categoryId;

    /* =========================
     * 판매 정보
     * ========================= */

    @Column(name = "SALES_DATE")
    @Comment("판매일자")
    private LocalDate salesDate;

    @Column(name = "LOCATION_NM", length = 100)
    @Comment("지점명")
    private String locationNm;

    @Column(name = "CATEGORY_NM", length = 100)
    @Comment("카테고리명")
    private String categoryNm;

    @Column(name = "PRODUCT_NM", length = 255)
    @Comment("상품명")
    private String productNm;

    @Column(name = "BARCODE", length = 20)
    @Comment("88코드")
    private String barcode;

    /* =========================
     * 판매 금액 정보
     * ========================= */

    @Column(name = "UNIT_PRICE")
    @Comment("상품별 단가")
    private Integer unitPrice;

    @Column(name = "QTY")
    @Comment("수량")
    private Integer qty;

    @Column(name = "TOTAL_AMOUNT")
    @Comment("총 매출")
    private Integer totalAmount;

    @Column(name = "REAL_AMOUNT")
    @Comment("실매출")
    private Integer realAmount;

    @Column(name = "DISCOUNT_AMOUNT")
    @Comment("할인")
    private Integer discountAmount;

    @Column(name = "REFUND_AMOUNT")
    @Comment("환불 금액")
    private Integer refundAmount;

    @Column(name = "REFUND_QTY")
    @Comment("환불 수량")
    private Integer refundQty;

    /* =========================
     * 상태
     * ========================= */

    @Column(name = "PROCESS_YN", length = 1)
    @Comment("판매 반영 여부")
    private String processYn;

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
    @JoinColumn(name = "LOCATION_ID", insertable = false, updatable = false)
    @Comment("지점 정보")
    private StoreLocationMstEntity location;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CATEGORY_ID", insertable = false, updatable = false)
    @Comment("카테고리 정보")
    private CategoryMstEntity category;

}