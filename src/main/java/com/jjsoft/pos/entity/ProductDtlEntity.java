package com.jjsoft.pos.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.jjsoft.pos.enums.ProductStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

@Entity
@Table(name = "product_dtl")
@Comment("상품 상세")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ProductDtlEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PRODUCT_DTL_ID")
    @Comment("상품 랏 ID")
    private Long productDtlId;

    @Column(name = "PRODUCT_ID", nullable = false)
    @Comment("상품 ID")
    private Long productId;

    @Column(name = "LOCATION_ID", nullable = false)
    @Comment("지점(장소) ID")
    private Long locationId;

    @Column(name = "LOT_NO")
    @Comment("상품코드 조합 : STORE_ID + PARTNER_ID + CATEGORY_ID + LOCATION_ID + 입고일")
    private String lotNo;

    @Column(name = "BOX_QTY")
    @Comment("입고 박스 수량")
    private Integer boxQty;

    @Column(name = "ORG_STOCK_QTY")
    @Comment("최초 입고 수량")
    private Integer orgStockQty;

    @Column(name = "CUR_STOCK_QTY")
    @Comment("현재 남은 수량")
    private Integer curStockQty;

    @Column(name = "VARCODE_NO")
    @Comment("자체 바코드 넘버")
    private String varcodeNo;

    @Column(name = "P_PRODUCT_CD")
    @Comment("계열사 상품 코드")
    private String pProductCd;

    @Column(name = "P_VARCODE_NO")
    @Comment("계열사 바코드 넘버 (88코드)")
    private String pVarcodeNo;

    @Column(name = "RECEIVED_DATE")
    @Comment("입고일자")
    private LocalDateTime receivedDate;

    @Column(name = "MANUFACTURE_DATE")
    @Comment("제조일자")
    private LocalDateTime manufactureDate;

    @Column(name = "EXPIRATION_DATE")
    @Comment("유통기한")
    private LocalDateTime expirationDate;

    @Column(name = "COST_PRICE")
    @Comment("입고 시 매입가")
    private Integer costPrice;

    @Column(name = "ORG_PRICE")
    @Comment("권장소비자가")
    private Integer orgPrice;

    @Column(name = "AGREED_PRICE")
    @Comment("합의가")
    private Integer agreedPrice;

    @Column(name = "SALES_PRICE")
    @Comment("현장가 : 유통기한에 따른 현재가격")
    private Integer salesPrice;

    @Column(name = "ORG_SALES_PRICE")
    @Comment("최초 등록된 권장소비자가")
    private Integer orgSalesPrice;

    @Column(name = "ETC_PRICE")
    @Comment("기타 가격")
    private Integer etcPrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS")
    @Comment("상품 상태 : ACTIVE , FROZEN")
    private ProductStatus status;

    @Column(name = "DESCRIPTION", length = 1000)
    @Comment("상품 상세 설명")
    private String description;

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
    @JoinColumn(name = "PRODUCT_ID", insertable = false, updatable = false)
    @Comment("상품 마스터 정보")
    private ProductMstEntity product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LOCATION_ID", insertable = false, updatable = false)
    @Comment("매장 지점 정보")
    private StoreLocationMstEntity location;
}

