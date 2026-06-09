package com.jjsoft.pos.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
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

/**
 * 특가등록 상세
 */
@Entity
@Table(name = "special_dtl")
@Comment("특가등록 상세")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SpecialDtlEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SPECIAL_DTL_ID")
    @Comment("특가 상세 ID")
    private Long specialDtlId;

    @Column(name = "SPECIAL_ID", nullable = false)
    @Comment("특가 ID")
    private Long specialId;

    @Column(name = "PRODUCT_ID", nullable = false)
    @Comment("상품 ID")
    private Long productId;

    @Column(name = "QTY")
    @Comment("판매 상품 수량")
    private Integer qty;
    
    @Column(name = "REMAIN_QTY")
    @Comment("판매 후 남은 수량")
    private Integer remainQty;
    
    @Column(name = "LIMIT_QTY")
    @Comment("1인당 구매 가능 수량")
    private Integer limitQty;

    @Column(name = "ORG_SALES_PRICE")
    @Comment("권장 소비자가")
    private Integer orgSalesPrice;

    @Column(name = "SALES_PRICE")
    @Comment("특별가")
    private Integer salesPrice;

    @Column(name = "SALES_RATE")
    @Comment("할인율")
    private Double salesRate;
    
    @Column(name = "SORT_ORDER")
    @Comment("상품 정렬 순서")
    private Integer sortOrder;

    @Column(name = "DESCRIPTION", length = 1000)
    @Comment("비고")
    private String description;

    @Column(name = "PICKUP_TAG", length = 50)
    @Comment("픽업 태그 (예: 픽업 17시 시작)")
    private String pickupTag;

    @Column(name = "TAG_OVERRIDE", length = 20)
    private String tagOverride;

    @Column(name = "EXPIRATION_DATE")
    @Comment("노출 소비기한(관리자가 선택한 가용 lot 기준, NULL이면 가용 lot 최소값 자동 노출)")
    private LocalDate expirationDate;

    @Column(name = "IMAGE_URL", length = 500)
    @Comment("특가 상품 썸네일")
    private String imageUrl;

    @CreationTimestamp
    @Column(name = "CREATE_DATE", updatable = false)
    @Comment("생성일자")
    private LocalDateTime createDate;

    @Column(name = "CREATE_USER")
    @Comment("생성자")
    private String createUser;

    @UpdateTimestamp
    @Column(name = "UPDATE_DATE")
    @Comment("수정일자")
    private LocalDateTime updateDate;

    @Column(name = "UPDATE_USER")
    @Comment("수정자")
    private String updateUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SPECIAL_ID", insertable = false, updatable = false)
    @Comment("특가 마스터 정보")
    private SpecialMstEntity special;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PRODUCT_ID", insertable = false, updatable = false)
    @Comment("상품 정보")
    private ProductMstEntity product;
}