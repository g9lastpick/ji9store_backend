package com.jjsoft.pos.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.jjsoft.pos.enums.PaymentType;
import com.jjsoft.pos.enums.SalesType;

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
@Table(name = "sales_dtl")
@Comment("판매 상세 테이블")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SalesDtlEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SALES_DTL_ID")
    @Comment("판매 DTL ID")
    private Long salesDtlId;

    @Column(name = "SALES_ID", nullable = false)
    @Comment("판매 ID")
    private Long salesId;

    @Column(name = "PRODUCT_ID", nullable = false)
    @Comment("상품 ID")
    private Long productId;

    @Column(name = "PRODUCT_DTL_ID", nullable = false)
    @Comment("상품 랏 ID")
    private Long productDtlId;

    @Column(name = "LINE_NO", nullable = false)
    @Comment("판매 순번")
    private Integer lineNo;

    @Enumerated(EnumType.STRING)
    @Column(name = "SALES_TYPE")
    @Comment("구매 타입 - 매장, 배달, 예약 등")
    private SalesType salesType;

    @Enumerated(EnumType.STRING)
    @Column(name = "PAYMENT_TYPE")
    @Comment("결제 타입 (CARD, CASH, POINT, GRP)")
    private PaymentType paymentType;

    @Column(name = "QTY", nullable = false)
    @Comment("판매 수량")
    private Integer qty;

    @Column(name = "UNIT_PRICE", nullable = false)
    @Comment("원래 단가")
    private Integer unitPrice;

    @Column(name = "DISCOUNT_PRICE")
    @Comment("할인 금액")
    private Integer discountPrice;

    @Column(name = "DISCOUNT_RATE")
    @Comment("할인율")
    private Integer discountRate;

    @Column(name = "SALES_PRICE")
    @Comment("실제 구매 금액")
    private Integer salesPrice;

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

    @UpdateTimestamp
    @Column(name = "UPDATE_DATE")
    @Comment("수정일자")
    private LocalDateTime updateDate;

    @Column(name = "UPDATE_USER", length = 50)
    @Comment("수정자")
    private String updateUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SALES_ID", insertable = false, updatable = false)
    @Comment("판매 마스터 정보")
    private SalesMstEntity sales;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PRODUCT_ID", insertable = false, updatable = false)
    @Comment("상품 마스터 정보")
    private ProductMstEntity product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PRODUCT_DTL_ID", insertable = false, updatable = false)
    @Comment("상품 랏 정보")
    private ProductDtlEntity productDtl;
}
