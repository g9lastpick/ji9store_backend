package com.jjsoft.pos.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.jjsoft.pos.enums.ProgressType;

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
 * 특가등록 마스터
 */
@Entity
@Table(name = "special_mst")
@Comment("특가등록 마스터")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SpecialMstEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SPECIAL_ID")
    @Comment("특가 ID")
    private Long specialId;

    @Column(name = "STORE_ID", nullable = false)
    @Comment("매장 ID")
    private Long storeId;

    @Column(name = "LOCATION_ID", nullable = false)
    @Comment("위치 정보 ID")
    private Long locationId;

    @Column(name = "SPECIAL_NM", nullable = false)
    @Comment("특가 명")
    private String specialNm;

    @Column(name = "START_DATE", nullable = false)
    @Comment("특가 시작 일시")
    private LocalDateTime startDate;

    @Column(name = "END_DATE", nullable = false)
    @Comment("특가 종료 일시")
    private LocalDateTime endDate;
    
//    @Column(name = "SPECIAL_PRICE")
//    @Comment("특가 금액")
//    private Integer specialPrice;

    @Column(name = "SPECIAL_TYPE")
    @Comment("특가 타입 EVENT ,ETC")
    private String specialType;

    @Enumerated(EnumType.STRING)
    @Column(name = "PROGRESS_TYPE")
    @Comment("진행상태 START, STOP, CANCEL")
    private ProgressType progressType;
    
    @Column(name = "PICKUP_END_DATE")
    @Comment("유저가 픽업 완료해야하는 일시")
    private LocalDateTime pickupEndDate;

    @Column(name = "DESCRIPTION", length = 1000)
    @Comment("비고")
    private String description;
    

    @Column(name = "IMAGE_URL", length = 500)
    @Comment("썸네일 이미지")
    private String imageUrl;


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
    @JoinColumn(name = "STORE_ID", insertable = false, updatable = false)
    @Comment("매장 정보")
    private StoreMstEntity store;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LOCATION_ID", insertable = false, updatable = false)
    @Comment("지점 정보")
    private StoreLocationMstEntity location;
    
    

    public void update(Long storeId, Long locationId, String specialNm,
                       LocalDateTime startDate, LocalDateTime endDate,
                       ProgressType progressType, 
                       String specialType, String description, String updateUser , LocalDateTime pickupEndDate) {//Integer specialPrice,
        this.storeId      = storeId;
        this.locationId   = locationId;
        this.specialNm    = specialNm;
        this.startDate    = startDate;
        this.endDate      = endDate;
        this.progressType = progressType;
//        this.specialPrice = specialPrice;
        this.specialType  = specialType;
        this.description  = description;
        this.updateUser   = updateUser;
        this.updateDate   = LocalDateTime.now();
        this.pickupEndDate = pickupEndDate;
    }
}
