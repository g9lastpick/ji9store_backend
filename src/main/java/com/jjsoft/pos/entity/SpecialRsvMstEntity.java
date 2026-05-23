package com.jjsoft.pos.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.jjsoft.pos.enums.ReservationStatus;

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

/**
 * 특가 예약 마스터
 */
@Entity
@Table(name = "special_rsv_mst")
@Comment("특가 예약 마스터")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SpecialRsvMstEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SPECIAL_RSV_MST_ID")
    @Comment("특가 예약 마스터 ID")
    private Long specialRsvMstId;

    @Column(name = "SPECIAL_ID", nullable = false)
    @Comment("특가 마스터 ID")
    private Long specialId;

    @Column(name = "SALES_ID")
    @Comment("판매 ID")
    private Long salesId;

    @Column(name = "USER_ID")
    @Comment("예약자 유저 ID")
    private String userId;

    @Column(name = "TMP_USER_ID")
    @Comment("임시 유저 ID")
    private String tmpUserId;

    @Column(name = "PHONE_NO")
    @Comment("예약자 전화번호")
    private String phoneNo;

    @Enumerated(EnumType.STRING)
    @Column(name = "RESERVATION_STATUS")
    @Comment("예약 상태")
    private ReservationStatus reservationStatus;

    @Column(name = "RESERVATION_CNT")
    @Comment("예약 총 수량")
    private Integer reservationCnt;

    @Column(name = "SALES_CNT")
    @Comment("구매 수량")
    private Integer salesCnt;

    @Column(name = "RESERVATION_PRICE")
    @Comment("예약 총 금액")
    private Integer reservationPrice;

    @Column(name = "SALES_PRICE")
    @Comment("실제 결제 금액")
    private Integer salesPrice;

    @Column(name = "VISIT_DATE")
    @Comment("수령 예정 일자")
    private LocalDateTime visitDate;

    @Column(name = "CANCEL_YN")
    @Comment("취소 여부")
    private String cancelYn;

    @Column(name = "VISIT_YN")
    @Comment("방문 여부")
    private String visitYn;

    @Column(name = "DESCRIPTION", length = 1000)
    @Comment("비고")
    private String description;

    @Column(name = "USE_YN")
    @Comment("사용 여부")
    private String useYn;

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
    @JoinColumn(name = "SPECIAL_ID", insertable = false, updatable = false)
    @Comment("특가 마스터 정보")
    private SpecialMstEntity special;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SALES_ID", insertable = false, updatable = false)
    @Comment("판매 마스터 정보")
    private SalesMstEntity sales;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", referencedColumnName = "USER_ID", insertable = false, updatable = false)
    @Comment("예약 유저 정보")
    private UserMstEntity user;

    public void update(String reservationStatus,
                       Integer reservationPrice,
                       Integer salesPrice,
                       String description, String updateUser) {
        this.reservationStatus = ReservationStatus.getReservationStatusFromDescription(reservationStatus);
        this.reservationPrice  = reservationPrice;
        this.salesPrice        = salesPrice;
        this.description       = description;
        this.updateUser        = updateUser;
    }
}
