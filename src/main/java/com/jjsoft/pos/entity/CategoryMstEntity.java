package com.jjsoft.pos.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;

import lombok.*;

/**
 * 카테고리 마스터 엔티티
 */
@Entity
@Table(name = "category_mst")
@Comment("카테고리 마스터")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"parent","children","store"})
public class CategoryMstEntity {

    /**
     * 내부 식별자
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CATEGORY_ID")
    @Comment("내부 식별자")
    private Long categoryId;

    /**
     * 부모 카테고리 ID
     */
    @Column(name = "PARENT_ID")
    @Comment("부모 카테고리 ID")
    private Long parentId;


    /**
     * 매장 ID
     */
    @Column(name = "STORE_ID")
    @Comment("매장 ID")
    private Long storeId;


    /**
     * 부모 카테고리 (Self Join)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARENT_ID", insertable = false, updatable = false)
    @Comment("부모 카테고리")
    private CategoryMstEntity parent;


    /**
     * 자식 카테고리 리스트
     */
    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
    @Comment("자식 카테고리")
    private List<CategoryMstEntity> children = new ArrayList<>();


    /**
     * 매장 정보
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STORE_ID", insertable = false, updatable = false)
    @Comment("매장 정보")
    private StoreMstEntity store;


    /**
     * 카테고리 코드
     */
    @Column(name = "CATEGORY_CD", nullable = false)
    @Comment("카테고리 코드")
    private String categoryCode;


    /**
     * 카테고리명
     */
    @Column(name = "CATEGORY_NM", nullable = false)
    @Comment("카테고리명")
    private String categoryName;


    /**
     * 정렬 순서
     */
    @Column(name = "SORT_ORDER")
    @Comment("정렬 순서")
    private Integer sortOrder;


    /**
     * 설명
     */
    @Column(name = "DESCRIPTION", length = 1000)
    @Comment("설명")
    private String description;


    /**
     * 사용 여부
     */
    @Column(name = "USE_YN", length = 1)
    @Comment("사용 여부")
    private String useYn;


    /**
     * 생성일자
     */
    @CreationTimestamp
    @Column(name = "CREATE_DATE", updatable = false)
    @Comment("생성일자")
    private LocalDateTime createDate;


    /**
     * 생성자
     */
    @Column(name = "CREATE_USER")
    @Comment("생성자")
    private String createUser;


    /**
     * 수정일자
     */
    @UpdateTimestamp
    @Column(name = "UPDATE_DATE")
    @Comment("수정일자")
    private LocalDateTime updateDate;


    /**
     * 수정자
     */
    @Column(name = "UPDATE_USER")
    @Comment("수정자")
    private String updateUser;

}