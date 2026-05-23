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
 * 상품 이미지 상세 엔티티
 */
@Entity
@Table(name = "product_image_dtl")
@Comment("상품 이미지 상세")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ProductImageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "P_IMAGE_ID")
    @Comment("상품 이미지 ID")
    private Long pImageId;

    @Column(name = "PRODUCT_ID", nullable = false)
    @Comment("상품 ID")
    private Long productId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PRODUCT_ID", insertable = false, updatable = false)
    @Comment("상품 정보")
    private ProductMstEntity product;

    @Column(name = "IMAGE_URL", nullable = false)
    @Comment("이미지 URL")
    private String imageUrl;

    @Column(name = "SORT_ORDER")
    @Comment("이미지 정렬 순서 (1부터)")
    private Integer sortOrder;

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