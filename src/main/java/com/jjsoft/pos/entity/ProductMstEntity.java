package com.jjsoft.pos.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.jjsoft.pos.enums.ProductStatus;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "product_mst")
@Comment("상품 마스터")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ProductMstEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PRODUCT_ID")
    @Comment("내부 식별자")
    private Long productId;

    @Column(name = "STORE_ID")
    @Comment("매장 ID")
    private Long storeId;

    @Column(name = "CATEGORY_ID")
    @Comment("카테고리 ID")
    private Long categoryId;

    @Column(name = "PARTNER_ID")
    @Comment("계열사 ID")
    private Long partnerId;

    @Column(name = "PRODUCT_NM", nullable = false)
    @Comment("상품명")
    private String productNm;

    @Column(name = "P_PRODUCT_CD", nullable = false)
    @Comment("계열사 상품코드 88코드")
    private String pproductCd;

    @Column(name = "P_VARCODE_NO", nullable = false)
    @Comment("계열사 바코드 상품에 있는 고유 바코드 안바뀜")
    private String pvarcodeNo;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS")
    @Comment("상태")
    private ProductStatus status;

    @Column(name = "ORG_PRICE")
    @Comment("master table의 소비자가")
    private Integer orgPrice;

    @Column(name = "ORG_SALES_PRICE")
    @Comment("master table의 현장가")
    private Integer orgSalesPrice;

    @Column(name = "DESCRIPTION", length = 1000)
    @Comment("상품 설명")
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
    @JoinColumn(name = "STORE_ID", insertable = false, updatable = false)
    @Comment("매장 정보")
    private StoreMstEntity store;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CATEGORY_ID", insertable = false, updatable = false)
    @Comment("카테고리 정보")
    private CategoryMstEntity category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARTNER_ID", insertable = false, updatable = false)
    @Comment("계열사 정보")
    private StorePartnerMstEntity partner;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ProductDtlEntity> productDtls;
}
