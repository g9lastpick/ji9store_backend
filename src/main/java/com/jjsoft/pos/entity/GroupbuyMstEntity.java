package com.jjsoft.pos.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.jjsoft.pos.enums.GroupbuyStatus;
import com.jjsoft.pos.enums.GroupbuyType;
import com.jjsoft.pos.enums.PayType;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "groupbuy_mst")
@Comment("공동구매 마스터")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class GroupbuyMstEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "GROUPBUY_ID")
    @Comment("공동구매 ID")
    private Long groupbuyId;

    /* =========================
     * FK 컬럼 영역
     * ========================= */

    @Column(name = "STORE_ID", nullable = false)
    @Comment("매장 ID")
    private Long storeId;

    @Column(name = "LOCATION_ID", nullable = false)
    @Comment("지점 ID")
    private Long locationId;

    /* =========================
     * 기본 정보
     * ========================= */
    
    @Column(name = "PRODUCT_ID", nullable = false)
    @Comment("상품 ID")
    private Long productId;

    @Column(name = "GROUPBUY_NM", nullable = false, length = 255)
    @Comment("공동구매 명")
    private String groupbuyNm;

    @Enumerated(EnumType.STRING)
    @Column(name = "GROUPBUY_TYPE", length = 20)
    @Comment("목표 기준 (QTY / AMOUNT)")
    private GroupbuyType groupbuyType;

    @Column(name = "TARGET_QTY")
    @Comment("목표 수량")
    private Integer targetQty;

    @Column(name = "TARGET_AMOUNT")
    @Comment("목표 금액")
    private Integer targetAmount;

    @Column(name = "CURRENT_QTY")
    @Comment("현재 참여 수량")
    private Integer currentQty;

    @Column(name = "CURRENT_AMOUNT")
    @Comment("현재 참여 금액")
    private Integer currentAmount;

    /* =========================
     * 일정 / 상태
     * ========================= */

    @Column(name = "START_DATE", nullable = false)
    @Comment("시작일시")
    private LocalDateTime startDate;

    @Column(name = "END_DATE", nullable = false)
    @Comment("종료일시")
    private LocalDateTime endDate;

    @Column(name = "PICKUP_START_DATE", nullable = false)
    @Comment("픽업 시작 일시")
    private LocalDateTime pickupStartDate;

    @Column(name = "PICKUP_END_DATE", nullable = false)
    @Comment("픽업 종료 일시")
    private LocalDateTime pickupEndDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", length = 20)
    @Comment("진행 상태")
    private GroupbuyStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "PAY_TYPE", length = 20)
    @Comment("결제 방식 (PRE / POST)")
    private PayType payType;
    
    @Column(name = "LIMIT_QTY")
    @Comment("상품별 최대 판매 수량")
    private Integer limitQty;

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
}
