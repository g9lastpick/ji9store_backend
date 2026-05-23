package com.jjsoft.pos.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import lombok.*;

/**
 * 매장 상세
 */
@Entity
@Table(name = "store_location_mst")
@Comment("매장 상세")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class StoreLocationMstEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "LOCATION_ID")
    @Comment("내부 식별자")
    private Long locationId;

    @Column(name = "STORE_ID", nullable = false)
    @Comment("STORE_ID")
    private Long storeId;

    @Column(name = "LOCATION_NM", nullable = false)
    @Comment("장소명")
    private String locationNm;

    @Column(name = "ADDRESS", length = 255)
    @Comment("매장 주소")
    private String address;

    @Column(name = "PHONE", length = 20)
    @Comment("매장 전화번호")
    private String phone;

    @Column(name = "EMAIL", length = 255)
    @Comment("매장 이메일")
    private String email;

    @Column(name = "OPEN_DATE")
    @Comment("매장 개점일")
    private LocalDateTime openDate;

    @Column(name = "DESCRIPTION", length = 1000)
    @Comment("비고")
    private String description;

    @Column(name = "USE_TYPE", length = 50)
    @Comment("현재 매장 상태 OPEN , CLOSE 등")
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
