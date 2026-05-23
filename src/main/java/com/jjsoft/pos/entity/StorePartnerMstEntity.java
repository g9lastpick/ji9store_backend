package com.jjsoft.pos.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import lombok.*;

/**
 * 계열사 마스터
 */
@Entity
@Table(name = "store_partner_mst")
@Comment("계열사 마스터")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class StorePartnerMstEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PARTNER_ID")
    @Comment("내부 식별자")
    private Long partnerId;

    @Column(name = "STORE_ID", nullable = false)
    @Comment("STORE_ID")
    private Long storeId;

    @Column(name = "PARTNER_NM", length = 255)
    @Comment("계열사 명")
    private String partnerNm;

    @Column(name = "ADDRESS", length = 255)
    @Comment("주소")
    private String address;

    @Column(name = "PHONE", length = 20)
    @Comment("전화번호")
    private String phone;

    @Column(name = "EMAIL", length = 100)
    @Comment("매장 이메일")
    private String email;

    @Column(name = "SORT_ORDER")
    @Comment("정렬 순서")
    private Integer sortOrder;

    @Column(name = "DESCRIPTION", length = 1000)
    @Comment("비고")
    private String description;

    @Column(name = "USE_TYPE", length = 50)
    @Comment("현재 계열사 상태 OPEN, CLOSE, 휴업 등")
    private String useType;

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
    @JoinColumn(name = "STORE_ID", insertable = false, updatable = false)
    @Comment("매장 정보")
    private StoreMstEntity store;
}
