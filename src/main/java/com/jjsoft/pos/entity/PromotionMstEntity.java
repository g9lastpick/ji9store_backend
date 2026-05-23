package com.jjsoft.pos.entity;

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
 * 프로모션 마스터 엔티티
 */
@Entity
@Table(name = "promotion_mst")
@Comment("프로모션 마스터")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PromotionMstEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PROMOTION_ID")
    @Comment("프로모션 ID")
    private Long promotionId;

    @Column(name = "PRODUCT_ID", nullable = false)
    @Comment("상품 ID")
    private Long productId;

    @Column(name = "STORE_ID", nullable = false)
    @Comment("매장 ID")
    private Long storeId;

    @Column(name = "LOCATION_ID", nullable = false)
    @Comment("위치 정보 ID")
    private Long locationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PRODUCT_ID", insertable = false, updatable = false)
    @Comment("상품 정보")
    private ProductMstEntity product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STORE_ID", insertable = false, updatable = false)
    @Comment("매장 정보")
    private StoreMstEntity store;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LOCATION_ID", insertable = false, updatable = false)
    @Comment("지점 정보")
    private StoreLocationMstEntity location;

    @Column(name = "DISCOUNT_RATE", nullable = false)
    @Comment("할인율(%)")
    private Integer discountRate;

    @Column(name = "DAYS_LEFT", nullable = false)
    @Comment("유통기한 남은 일수 기준")
    private Integer daysLeft;

    @Column(name = "START_DATE", nullable = false)
    @Comment("프로모션 시작일")
    private LocalDateTime startDate;

    @Column(name = "END_DATE", nullable = false)
    @Comment("프로모션 종료일")
    private LocalDateTime endDate;

    @Column(name = "USE_YN", length = 1)
    @Comment("사용 여부")
    private String useYn;

    @Column(name = "DESCRIPTION", length = 1000)
    @Comment("비고")
    private String description;

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
}
