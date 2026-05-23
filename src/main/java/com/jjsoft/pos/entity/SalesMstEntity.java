package com.jjsoft.pos.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.Comment;

import com.jjsoft.pos.enums.PaymentType;
import com.jjsoft.pos.enums.SalesStatus;
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

/**
 * 판매 마스터 엔티티
 */
@Entity
@Table(name = "sales_mst")
@Comment("판매 마스터")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SalesMstEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SALES_ID")
    @Comment("판매 ID")
    private Long salesId;

    @Column(name = "STORE_ID", nullable = false)
    @Comment("매장 ID")
    private Long storeId;

    @Column(name = "USER_ID")
    @Comment("고객 유저 ID")
    private String userId;

    @Column(name = "TOTAL_QTY", nullable = false)
    @Comment("총 수량")
    private Integer totalQty;

    @Column(name = "TOTAL_PRICE", nullable = false)
    @Comment("총 금액")
    private Integer totalPrice;

    @Column(name = "DISCOUNT_PRICE")
    @Comment("할인 금액")
    private Integer discountPrice;

    @Column(name = "FINAL_PRICE")
    @Comment("결제 금액")
    private Integer finalPrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "PAYMENT_TYPE")
    @Comment("결제 수단 (CASH, CARD 등)")
    private PaymentType paymentType;

    @Column(name = "SALES_DATE")
    @Comment("판매일시")
    private LocalDateTime salesDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "SALES_STATUS", nullable = false)
    @Comment("SALES 상태 COMPLETE , CANCEL , RETURN ,RESERVATION")
    private SalesStatus salesStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "SALES_TYPE")
    @Comment("구매 타입 - 방문 , 포장 , 예약 , 배달")
    private SalesType salesType;

    @Column(name = "DESCRIPTION", length = 1000)
    @Comment("설명")
    private String description;

    @Column(name = "CREATE_DATE", updatable = false)
    @Comment("생성일자")
    private LocalDateTime createDate;

    @Column(name = "CREATE_USER", length = 50)
    @Comment("생성자")
    private String createUser;

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
    @JoinColumn(name = "USER_ID", referencedColumnName = "USER_ID", insertable = false, updatable = false)
    @Comment("고객 유저 정보")
    private UserMstEntity user;
}
