package com.jjsoft.pos.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "return_mst")
@Comment("반품 마스터")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ReturnMstEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RETURN_ID")
    @Comment("반품 ID")
    private Long returnId;

    @Column(name = "SALES_ID", nullable = false)
    @Comment("원판매 ID")
    private Long salesId;

    @Column(name = "STORE_ID", nullable = false)
    @Comment("매장 ID")
    private Long storeId;

    @Column(name = "LOCATION_ID", nullable = false)
    @Comment("위치 정보 ID")
    private Long locationId;

    @Column(name = "USER_ID")
    @Comment("반품 처리자 (직원) ID")
    private Long userId;

    @Column(name = "RETURN_DATE", nullable = false)
    @Comment("반품 일시")
    private LocalDateTime returnDate;

    @Column(name = "TOTAL_QTY", nullable = false)
    @Comment("반품 총 수량")
    private Integer totalQty;

    @Column(name = "TOTAL_AMOUNT", nullable = false)
    @Comment("반품 총 금액")
    private Integer totalAmount;

    @Column(name = "RETURN_REASON", length = 255)
    @Comment("반품 사유")
    private String returnReason;

    @Column(name = "STATUS", length = 20)
    @Comment("반품 상태")
    private String status;

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
    @JoinColumn(name = "SALES_ID", insertable = false, updatable = false)
    @Comment("판매 마스터")
    private SalesMstEntity sales;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STORE_ID", insertable = false, updatable = false)
    @Comment("매장 정보")
    private StoreMstEntity store;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LOCATION_ID", insertable = false, updatable = false)
    @Comment("지점 정보")
    private StoreLocationMstEntity location;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", insertable = false, updatable = false)
    @Comment("반품 처리자 정보")
    private UserMstEntity user;
}
