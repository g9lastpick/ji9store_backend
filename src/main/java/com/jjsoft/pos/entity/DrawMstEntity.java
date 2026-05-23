package com.jjsoft.pos.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.jjsoft.pos.enums.DrawStatus;

import jakarta.persistence.*;
import lombok.*;

//@Entity
//@Table(name = "draw_mst")
@Comment("드로우 이벤트 마스터")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class DrawMstEntity {

    /* =========================
     * PK
     * ========================= */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DRAW_ID")
    @Comment("드로우 이벤트 ID")
    private Long drawId;

    /* =========================
     * FK 컬럼
     * ========================= */

    @Column(name = "STORE_ID", nullable = false)
    @Comment("STORE ID")
    private Long storeId;

    @Column(name = "LOCATION_ID", nullable = false)
    @Comment("지점 ID")
    private Long locationId;

    @Column(name = "PRODUCT_ID", nullable = false)
    @Comment("상품 ID")
    private Long productId;

    /* =========================
     * 기본 정보
     * ========================= */

    @Column(name = "DRAW_NM", nullable = false, length = 255)
    @Comment("드로우 이벤트 명")
    private String drawNm;

    @Column(name = "DRAW_URL", nullable = false, length = 500)
    @Comment("드로우 페이지 URL")
    private String drawUrl;

    /* =========================
     * 일정 정보
     * ========================= */

    @Column(name = "ENTRY_START_DATE", nullable = false)
    @Comment("응모 시작일시")
    private LocalDateTime entryStartDate;

    @Column(name = "ENTRY_END_DATE", nullable = false)
    @Comment("응모 종료일시")
    private LocalDateTime entryEndDate;

    @Column(name = "DRAW_DATE", nullable = false)
    @Comment("드로우 이벤트 추첨 일시")
    private LocalDateTime drawDate;

    @Column(name = "PICKUP_START_DATE", nullable = false)
    @Comment("픽업 시작 일시")
    private LocalDateTime pickupStartDate;

    @Column(name = "PICKUP_END_DATE", nullable = false)
    @Comment("픽업 종료 일시")
    private LocalDateTime pickupEndDate;

    /* =========================
     * 드로우 조건
     * ========================= */

    @Column(name = "WINNER_CNT", nullable = false)
    @Comment("총 당첨자 수")
    private Integer winnerCnt;

    @Column(name = "TOTAL_QTY", nullable = false)
    @Comment("드로우 상품 전체 수량")
    private Integer totalQty;

    @Column(name = "CURRENT_QTY", nullable = false)
    @Comment("현재 남은 수량")
    private Integer currentQty;

    @Column(name = "LIMIT_QTY", nullable = false)
    @Comment("당첨자 1인당 제한 수량")
    private Integer limitQty;

    @Column(name = "SALES_PRICE", nullable = false)
    @Comment("당첨 시 판매가")
    private Integer salesPrice;

    /* =========================
     * 상태
     * ========================= */

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", length = 20)
    @Comment("이벤트 상태")
    private DrawStatus status;

    /* =========================
     * 공통 컬럼
     * ========================= */

    @Column(name = "DESCRIPTION", length = 1000)
    @Comment("설명")
    private String description;
    
    @Column(name = "IMAGE_URL")
    @Comment("썸네일 경로")
    private String imageUrl;

    @CreationTimestamp
    @Column(name = "CREATE_DATE", updatable = false)
    @Comment("생성일자")
    private LocalDateTime createDate;

    @Column(name = "CREATE_USER", length = 50)
    @Comment("생성자")
    private String createUser;

    @UpdateTimestamp
    @Column(name = "UPDATE_DATE")
    @Comment("수정일자")
    private LocalDateTime updateDate;

    @Column(name = "UPDATE_USER", length = 50)
    @Comment("수정자")
    private String updateUser;

    /* =========================
     * 연관관계 (읽기 전용)
     * ========================= */

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STORE_ID", insertable = false, updatable = false)
    @Comment("매장 정보")
    private StoreMstEntity store;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LOCATION_ID", insertable = false, updatable = false)
    @Comment("지점 정보")
    private StoreLocationMstEntity location;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PRODUCT_ID", insertable = false, updatable = false)
    @Comment("상품 정보")
    private ProductMstEntity product;
}
