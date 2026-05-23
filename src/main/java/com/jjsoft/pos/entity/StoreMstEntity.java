package com.jjsoft.pos.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import lombok.*;

/**
 * 매장 마스터
 */
@Entity
@Table(name = "store_mst")
@Comment("매장 마스터")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class StoreMstEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "STORE_ID")
    @Comment("내부 식별자")
    private Long storeId;

    @Column(name = "PARENT_STORE_ID")
    @Comment("부모 매장 ID")
    private Long parentStoreId;

    @Column(name = "COMPANY_NO")
    @Comment("사업자번호")
    private String companyNo;

    @Column(name = "STORE_NM", nullable = false)
    @Comment("매장명")
    private String storeNm;

    @Column(name = "SORT_ORDER")
    @Comment("정렬 순서")
    private Integer sortOrder;

    @Column(name = "DESCRIPTION", length = 1000)
    @Comment("설명")
    private String description;

    @Column(name = "USE_YN", length = 1)
    @Comment("사용 여부")
    private String useYn;

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
    @JoinColumn(name = "PARENT_STORE_ID", insertable = false, updatable = false)
    @Comment("부모 매장 정보")
    private StoreMstEntity parentStore;
}
