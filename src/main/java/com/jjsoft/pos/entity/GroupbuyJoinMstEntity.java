package com.jjsoft.pos.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.jjsoft.pos.enums.GroupbuyJoinStatus;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "groupbuy_join_mst")
@Comment("공동구매 참여 마스터")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class GroupbuyJoinMstEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "GROUPBUY_JOIN_MST_ID")
    @Comment("참여 마스터 ID")
    private Long groupbuyJoinMstId;

    /* =========================
     * FK 컬럼
     * ========================= */

    @Column(name = "GROUPBUY_ID", nullable = false)
    @Comment("공동구매 ID")
    private Long groupbuyId;

    @Column(name = "USER_ID", nullable = false)
    @Comment("유저 ID")
    private String userId;

    /* =========================
     * 참여 / 결제 상태
     * ========================= */

    @Enumerated(EnumType.STRING)
    @Column(name = "JOIN_STATUS", length = 20)
    @Comment("참여 상태 (JOIN / PAYED / CANCEL / FAIL)")
    private GroupbuyJoinStatus joinStatus;

    /* =========================
     * 집계 정보
     * ========================= */

    @Column(name = "TOTAL_QTY")
    @Comment("구매 수량")
    private Integer totalQty;
    
    @Column(name = "UNIT_PRICE")
    @Comment("확정 단가")
    private Integer unitPrice;

    @Column(name = "SALES_ID")
    @Comment("성공 시 판매 ID")
    private Long salesId;

    /* =========================
     * 공통 컬럼
     * ========================= */

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

    /* =========================
     * 연관관계 (읽기 전용)
     * ========================= */

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GROUPBUY_ID", insertable = false, updatable = false)
    @Comment("공동구매 정보")
    private GroupbuyMstEntity groupbuy;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "USER_ID", insertable = false, updatable = false)
//    @Comment("유저 정보")
//    private UserMstEntity user;
}
