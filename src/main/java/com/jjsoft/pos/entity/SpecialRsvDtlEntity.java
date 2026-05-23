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
 * 특가 예약 상세
 */
@Entity
@Table(name = "special_rsv_dtl")
@Comment("특가 예약 상세")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SpecialRsvDtlEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SPECIAL_RSV_DTL_ID")
    @Comment("특가 예약 상세 ID")
    private Long specialRsvDtlId;

    @Column(name = "SPECIAL_RSV_MST_ID")
    @Comment("특가 예약 마스터 ID")
    private Long specialRsvMstId;

    @Column(name = "SPECIAL_DTL_ID")
    @Comment("특가 상세 ID")
    private Long specialDtlId;

    @Column(name = "RESERVATION_CNT")
    @Comment("예약 수량")
    private Integer reservationCnt;

    @Column(name = "SALES_CNT")
    @Comment("구매 수량")
    private Integer salesCnt;

    @Column(name = "RESERVATION_PRICE")
    @Comment("예약 당시 총 금액")
    private Integer reservationPrice;

    @Column(name = "SALES_PRICE")
    @Comment("실제 결제 금액")
    private Integer salesPrice;

    @Column(name = "UNIT_PRICE")
    @Comment("제품 한개당 단가")
    private Integer unitPrice;
    
    @Column(name = "CANCEL_YN", length = 10)
    @Comment("취소여부")
    private String cancelYn;


    @Column(name = "DESCRIPTION", length = 1000)
    @Comment("비고")
    private String description;
//
//    @Column(name = "USE_YN")
//    @Comment("사용 여부")
//    private String useYn;

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
    @JoinColumn(name = "SPECIAL_RSV_MST_ID", insertable = false, updatable = false)
    @Comment("특가 예약 마스터 정보")
    private SpecialRsvMstEntity specialRsvMst;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SPECIAL_DTL_ID", insertable = false, updatable = false)
    @Comment("특가 상세 정보")
    private SpecialDtlEntity specialDtl;
}
