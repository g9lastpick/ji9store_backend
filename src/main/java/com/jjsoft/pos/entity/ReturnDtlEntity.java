package com.jjsoft.pos.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "return_dtl")
@Comment("반품 상세")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ReturnDtlEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RETURN_DTL_ID")
    @Comment("반품 상세 ID")
    private Long returnDtlId;

    @Column(name = "RETURN_ID", nullable = false)
    @Comment("반품 ID")
    private Long returnId;

    @Column(name = "SALES_DTL_ID", nullable = false)
    @Comment("원판매 상세 ID")
    private Long salesDtlId;

    @Column(name = "PRODUCT_ID", nullable = false)
    @Comment("상품 ID")
    private Long productId;

    @Column(name = "PRODUCT_DTL_ID", nullable = false)
    @Comment("상품 랏 ID")
    private Long productDtlId;

    @Column(name = "LINE_NO", nullable = false)
    @Comment("반품 순번")
    private Integer lineNo;

    @Column(name = "QTY", nullable = false)
    @Comment("반품 수량")
    private Integer qty;

    @Column(name = "UNIT_PRICE", nullable = false)
    @Comment("단가")
    private Integer unitPrice;

    @Column(name = "RETURN_AMOUNT", nullable = false)
    @Comment("반품 금액")
    private Integer returnAmount;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RETURN_ID", insertable = false, updatable = false)
    @Comment("반품 마스터 정보")
    private ReturnMstEntity returnEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SALES_DTL_ID", insertable = false, updatable = false)
    @Comment("판매 상세 정보")
    private SalesDtlEntity salesDetail;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PRODUCT_ID", insertable = false, updatable = false)
    @Comment("상품 정보")
    private ProductMstEntity product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PRODUCT_DTL_ID", insertable = false, updatable = false)
    @Comment("상품 랏 정보")
    private ProductDtlEntity productDtl;
}
